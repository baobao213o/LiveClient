package com.live.utils;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.live.LiveApp;
import com.live.api.MainService;
import com.live.network.HttpDownLoadCallBack;
import com.live.network.RetrofitManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DownloadManager {

    public static void downloadApk(final Activity activity, final String url, final HttpDownLoadCallBack callBack) {
        RetrofitManager.getInstance().getService(MainService.class).downloadApk().enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, final Response<ResponseBody> response) {
                if (response == null) {
                    showErrorDialog(activity, "请求出错");
                    return;
                }
                new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        //保存到本地
                        String apkName = url.substring(url.indexOf("/") + 1, url.length());
                        DownloadManager.writeFile2Disk(activity, response, apkName, callBack);
                    }
                }.start();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                showErrorDialog(activity, t.getMessage());
            }
        });
    }

    private static File createFile(String filename) {
        String folder = "nyannyannyan";
        File file;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            file = new File(Environment.getExternalStorageDirectory(), folder);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                file = new File(LiveApp.getInstance().getFilesDir(), folder);
            } else {
                return null;
            }
        }

        if (!file.exists()) {
            file.mkdir();
        }
        return new File(file, filename);

    }

    private static void writeFile2Disk(final Activity activity, Response<ResponseBody> response, String filename, HttpDownLoadCallBack httpCallBack) {

        File file = createFile(filename);
        if (file == null) {
            showErrorDialog(activity, "存储空间不足");
            return;
        }

        ResponseBody responseBody = response.body();
        if (responseBody == null) {
            showErrorDialog(activity, "请求出错");
            return;
        }
        long currentLength = 0;
        OutputStream os = null;

        InputStream is = responseBody.byteStream();
        long totalLength = responseBody.contentLength();
        try {
            os = new FileOutputStream(file);
            int len;
            byte[] buff = new byte[1024];
            while ((len = is.read(buff)) != -1) {
                os.write(buff, 0, len);
                currentLength += len;
                httpCallBack.onLoading(currentLength, totalLength);
            }
            // 通过Intent安装APK文件
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                String authority = SecureValidate.getPkgName() + ".fileProvider";
                Uri apkUri = FileProvider.getUriForFile(activity, authority, file);
                i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                i.setDataAndType(apkUri, activity.getContentResolver().getType(apkUri));
            } else {
                i.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
            }
            activity.startActivity(i);
            activity.finish();
        } catch (IOException e) {
            e.printStackTrace();
            activity.finish();
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void showErrorDialog(final Activity activity, String content) {


        new MaterialDialog.Builder(activity).content(content).onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                activity.finish();
            }
        }).positiveText("确定").cancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                activity.finish();
            }
        }).build().show();

    }
}

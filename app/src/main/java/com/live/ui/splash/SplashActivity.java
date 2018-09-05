package com.live.ui.splash;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.live.R;
import com.live.api.ValidateService;
import com.live.base.BaseActivity;
import com.live.entity.BaseServerResponse;
import com.live.entity.ServerInfo;
import com.live.network.HttpCallBack;
import com.live.network.RetrofitManager;
import com.live.ui.main.MainActivity;
import com.live.utils.DownloadManager;
import com.live.utils.MaterialDialogUtil;
import com.live.utils.NetworkUtil;
import com.live.utils.SecureValidate;
import com.live.utils.ServerInfoManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashActivity extends BaseActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        RetrofitManager.getInstance().getService(ValidateService.class).getServerInfo(SecureValidate.printSignatureMD5(), SecureValidate.getPkgName(), String.valueOf(SecureValidate.getVerCode())).enqueue(new Callback<BaseServerResponse<ServerInfo>>() {
            @Override
            public void onResponse(Call<BaseServerResponse<ServerInfo>> call, Response<BaseServerResponse<ServerInfo>> response) {
                BaseServerResponse<ServerInfo> temp = response.body();
                if (temp == null) {
                    showErrorDialog("请求出错");
                    return;
                }
                if (!BaseServerResponse.isSuccess(temp)) {
                    showErrorDialog(temp.msg);
                    return;
                }
                final ServerInfo data = temp.data;
                if (data == null) {
                    showErrorDialog("请求数据为空");
                    return;
                }
                ServerInfoManager.getInstance().setServerInfo(data);
                int curVerCode = SecureValidate.getVerCode();
                if (curVerCode < data.version) {
                    MaterialDialogUtil.showMsgWithoutTitle(SplashActivity.this, "更新啦", "更新", new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            if (!NetworkUtil.isWifiConnected()) {
                                MaterialDialogUtil.showMsgWithoutTitle(SplashActivity.this, "未连接到WiFi，是否继续？", "确定", new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                        fetchDownLoad(data.update_addr);
                                    }
                                });
                                return;
                            }
                            fetchDownLoad(data.update_addr);
                        }
                    });
                    return;
                }
                if (!TextUtils.isEmpty(data.hongbao_addr)) {
                    MaterialDialogUtil.showMsgWithoutTitle(SplashActivity.this, "点一点支付宝领红包", "我点", "鬼信", new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            Uri uri = Uri.parse(data.hongbao_addr);
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, uri);
                            Intent mainIntent = new Intent(SplashActivity.this, MainActivity.class);

                            Intent[] intents = new Intent[]{mainIntent, browserIntent};
                            startActivities(intents);
                            finish();
                        }
                    }, new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            jump();
                        }
                    });
                } else {
                    jump();
                }
            }

            @Override
            public void onFailure(Call<BaseServerResponse<ServerInfo>> call, Throwable t) {
                showErrorDialog("网络连接失败");
            }
        });
    }

    private void jump() {
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }


    private void showErrorDialog(String content) {

        MaterialDialogUtil.showMsgWithoutTitle(this, content, "确定", new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                finish();
            }
        });
    }

    private void fetchDownLoad(String url) {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_tip, null);
        final TextView tv_common_dialog_tip = view.findViewById(R.id.tv_common_dialog_tip);
        new MaterialDialog.Builder(this).customView(view, false).build().show();
        DownloadManager.downloadApk(this, url, new HttpCallBack() {
            @Override
            public void onLoading(final long current, final long total) {
                //更新进度条
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tv_common_dialog_tip.setText("下载进度:" + String.valueOf((int) (((float) current / total) * 100)));
                    }
                });
            }
        });
    }


}

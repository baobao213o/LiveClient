package com.live.network;

import android.app.Activity;
import android.support.annotation.NonNull;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.live.entity.BaseServerResponse;
import com.live.utils.MaterialDialogUtil;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CheckBaseServerInfoResult<T> implements Callback<BaseServerResponse<T>> {

    public interface HttpCallBack<T> {

        void onSucess(T data);

        void onFailure();
    }

    private Activity activity;

    private HttpCallBack<T> callBack;

    //0 不显示dialog
    //1 显示dialog
    //2 显示强制关闭dialog
    private int showDialog = 1;

    public CheckBaseServerInfoResult(Activity activity, HttpCallBack<T> callBack) {
        this.activity = activity;
        this.callBack = callBack;
    }

    public CheckBaseServerInfoResult(Activity activity, int showDialog, HttpCallBack<T> callBack) {
        this.activity = activity;
        this.callBack = callBack;
        this.showDialog = showDialog;
    }


    @Override
    public void onResponse(Call<BaseServerResponse<T>> call, Response<BaseServerResponse<T>> response) {

        BaseServerResponse<T> temp = response.body();
        if (temp == null) {
            handleResult("请求出错");
            callBack.onFailure();
            return;
        }
        if (!BaseServerResponse.isSuccess(temp)) {
            handleResult(temp.msg);
            callBack.onFailure();
            return;
        }
        final T data = temp.data;
        if (data == null) {
            handleResult("请求数据为空");
            callBack.onFailure();
            return;
        }
        callBack.onSucess(data);

    }

    @Override
    public void onFailure(Call<BaseServerResponse<T>> call, Throwable t) {
        handleResult("网络连接失败");
        callBack.onFailure();

    }

    private void handleResult(String content) {
        switch (showDialog) {
            case 1:
                showErrorDialog(activity, content);
                break;
            case 2:
                showForceErrorDialog(activity, content);
                break;
        }
    }


    private void showErrorDialog(final Activity activity, String content) {
        new MaterialDialog.Builder(activity).content(content).positiveText("确定").build().show();
    }

    private void showForceErrorDialog(final Activity activity, String content) {
        MaterialDialogUtil.showMsgWithoutTitle(activity, content, "确定", new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                activity.finish();
            }
        });
    }
}

package com.live.ui.splash;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.live.R;
import com.live.api.ValidateService;
import com.live.entity.BaseServerResponse;
import com.live.entity.ServerInfo;
import com.live.network.RetrofitManager;
import com.live.ui.main.MainActivity;
import com.live.utils.SecureValidate;
import com.live.view.ToastHelper;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        RetrofitManager.getInstance().getService(ValidateService.class).getServerInfo(SecureValidate.printSignatureMD5(), SecureValidate.getPkgName()).enqueue(new Callback<BaseServerResponse<ServerInfo>>() {
            @Override
            public void onResponse(Call<BaseServerResponse<ServerInfo>> call, Response<BaseServerResponse<ServerInfo>> response) {
                BaseServerResponse<ServerInfo> temp = response.body();
                if (temp == null) {
                    ToastHelper.showToast("请求出错");
                    finish();
                    return;
                }
                if (!BaseServerResponse.isSuccess(temp)) {
                    ToastHelper.showToast(temp.msg);
                    finish();
                    return;
                }
                ServerInfo data = temp.data;
                if (data == null) {
                    ToastHelper.showToast("请求数据为空");
                    finish();
                    return;
                }
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                intent.putExtra("AppData", data);
                startActivity(intent);
                finish();
            }

            @Override
            public void onFailure(Call<BaseServerResponse<ServerInfo>> call, Throwable t) {
                ToastHelper.showToast(t.getMessage());
            }
        });
    }
}

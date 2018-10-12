package com.live.base;

import android.os.Bundle;

import com.live.network.RetrofitManager;
import com.live.utils.ServerInfoManager;

import androidx.appcompat.app.AppCompatActivity;

public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        ServerInfoManager.getInstance().saveServerInfo(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void finish() {
        super.finish();
        RetrofitManager.getInstance().cancelAllRequest();
    }
}

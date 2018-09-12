package com.live.base;

import android.os.Bundle;
import android.support.annotation.Nullable;

public abstract class BaseFreshActivity extends BaseActivity {

    interface FreshCallBack {
        void onfresh();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


}

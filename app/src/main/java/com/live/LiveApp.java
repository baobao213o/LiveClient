package com.live;

import android.app.Application;
import android.content.Context;
import android.os.StrictMode;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.squareup.leakcanary.LeakCanary;

import static com.live.BuildConfig.DEBUG;

public class LiveApp extends Application{

    private static LiveApp instance;

    public static LiveApp getInstance() {
        return instance;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        instance = this;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        if (DEBUG) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .build());
        }
        LeakCanary.install(this);
        Fresco.initialize(this);
    }

}

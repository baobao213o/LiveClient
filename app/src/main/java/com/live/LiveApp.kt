package com.live

import android.app.Application
import android.content.Context

import com.facebook.drawee.backends.pipeline.Fresco
import com.squareup.leakcanary.LeakCanary

class LiveApp : Application() {

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        instance = this
    }


    override fun onCreate() {
        super.onCreate()
        //        if (DEBUG) {
        //            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
        //                    .detectAll()
        //                    .penaltyLog()
        //                    .build());
        //            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
        //                    .detectAll()
        //                    .penaltyLog()
        //                    .build());
        //        }
        LeakCanary.install(this)
        Fresco.initialize(this)
    }

    companion object {

        var instance: LiveApp? = null
            private set
    }

}

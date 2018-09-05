package com.live.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.live.LiveApp;

public class NetworkUtil {

    /**
     * 检查网络是否可用
     */
    public static boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager) LiveApp.getInstance().getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (manager == null) {
            return false;
        }
        NetworkInfo networkinfo = manager.getActiveNetworkInfo();
        return networkinfo != null && networkinfo.isAvailable();
    }

    /**
     * 判断WIFI网络是否可用
     */
    public static boolean isWifiConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) LiveApp.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) {
            return false;
        }
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetInfo != null&& activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI;

    }


}

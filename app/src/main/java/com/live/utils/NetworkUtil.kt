package com.live.utils

import android.content.Context
import android.net.ConnectivityManager
import com.live.LiveApp

object NetworkUtil {

    /**
     * 检查网络是否可用
     */
    val isNetworkAvailable: Boolean
        get() {
            val manager = LiveApp.instance!!.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkinfo = manager.activeNetworkInfo
            return networkinfo != null && networkinfo.isAvailable
        }

    /**
     * 判断WIFI网络是否可用
     */
    val isWifiConnected: Boolean
        get() {
            val connectivityManager = LiveApp.instance!!.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetInfo = connectivityManager.activeNetworkInfo
            return activeNetInfo != null && activeNetInfo.type == ConnectivityManager.TYPE_WIFI

        }


}

package com.live.utils

import android.util.DisplayMetrics

import com.live.LiveApp


/**
 * Created by gaoruochen on 18-6-4.
 */

object DeviceUtil {

    val screenWidth: Int
        get() {
            val dm: DisplayMetrics = LiveApp.instance!!.resources.displayMetrics
            return dm.widthPixels
        }

    val screenHeight: Int
        get() {
            val dm: DisplayMetrics = LiveApp.instance!!.resources.displayMetrics
            return dm.heightPixels
        }

    val systemVersion: String
        get() = android.os.Build.VERSION.RELEASE

    val systemModel: String
        get() = android.os.Build.MODEL

    fun dip2px(dpValue: Float): Int {
        val scale = LiveApp.instance!!.resources.displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }


}

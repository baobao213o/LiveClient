package com.live.utils;

import android.util.DisplayMetrics;

import com.live.LiveApp;


/**
 * Created by gaoruochen on 18-6-4.
 */

public class DeviceUtil {

    public static int getScreenWidth() {
        DisplayMetrics dm;
        dm = LiveApp.getInstance().getResources().getDisplayMetrics();
        return dm.widthPixels;
    }

    public static int getScreenHeight() {
        DisplayMetrics dm;
        dm = LiveApp.getInstance().getResources().getDisplayMetrics();
        return dm.heightPixels;
    }

    public static int dip2px(float dpValue) {
        final float scale = LiveApp.getInstance().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }


}

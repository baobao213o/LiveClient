package com.live.view;

import android.widget.Toast;

import com.live.LiveApp;


public class ToastHelper {

    public static void showToast(String content) {
        Toast toast = new Toast(LiveApp.getInstance());
        toast.setText(content);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.show();
    }

}


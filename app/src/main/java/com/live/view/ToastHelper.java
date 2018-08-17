package com.live.view;

import android.widget.Toast;

import com.live.LiveApp;


public class ToastHelper {

    public static void showToast(String content) {
        Toast toast = Toast.makeText(LiveApp.getInstance(), "", Toast.LENGTH_SHORT);
        toast.setText(content);
        toast.show();
    }

}


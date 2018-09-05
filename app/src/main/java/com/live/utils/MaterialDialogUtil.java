package com.live.utils;

import android.app.Activity;
import android.content.DialogInterface;

import com.afollestad.materialdialogs.MaterialDialog;

public class MaterialDialogUtil {

    public static void showMsgWithoutTitle(final Activity activity, String content, String positiveLabel, MaterialDialog.SingleButtonCallback callback) {

        new MaterialDialog.Builder(activity).content(content).positiveText(positiveLabel).onPositive(callback)
                .cancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        activity.finish();
                    }
                }).build().show();
    }

    public static void showMsgWithoutTitle(Activity activity, String content, String positiveLabel,String negativeLabel,
                                           MaterialDialog.SingleButtonCallback positiveCallback, MaterialDialog.SingleButtonCallback negativeCallback) {
        new MaterialDialog.Builder(activity).content(content).positiveText(positiveLabel).onPositive(positiveCallback)
                .negativeText(negativeLabel).onNegative(negativeCallback).build().show();
    }


}

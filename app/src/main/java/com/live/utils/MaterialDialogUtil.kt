package com.live.utils

import android.app.Activity
import com.afollestad.materialdialogs.DialogCallback

import com.afollestad.materialdialogs.MaterialDialog

object MaterialDialogUtil {

    fun showMsgWithoutTitle(activity: Activity, content: String, positiveLabel: String, callback: DialogCallback) {
        MaterialDialog(activity).message(null, content).positiveButton(null, positiveLabel, callback).negativeButton { activity.finish() }.show()
    }

}

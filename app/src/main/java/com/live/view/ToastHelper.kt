package com.live.view

import android.widget.Toast
import com.live.LiveApp

fun toast(content: String) {

    Toast.makeText(LiveApp.instance, content, Toast.LENGTH_LONG).show()

}
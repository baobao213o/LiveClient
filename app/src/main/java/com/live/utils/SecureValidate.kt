package com.live.utils

import android.content.pm.PackageManager.GET_SIGNATURES
import com.live.LiveApp
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException


object SecureValidate {

    val pkgName: String?
        get() {
            var pkName: String? = null
            try {

                pkName = LiveApp.instance!!.packageName
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return pkName
        }

    val verCode: Int
        get() {
            var verCode = 0
            try {
                val pkName = LiveApp.instance!!.packageName
                verCode = LiveApp.instance!!.packageManager.getPackageInfo(pkName, 0).versionCode
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return verCode
        }

    val verName: String?
        get() {
            var verName: String? = null
            try {
                val pkName = LiveApp.instance!!.packageName
                verName = LiveApp.instance!!.packageManager.getPackageInfo(pkName, 0).versionName
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return verName
        }

    fun printSignatureMD5(): String? {
        try {
            val packageInfo = LiveApp.instance!!.packageManager.getPackageInfo(LiveApp.instance!!.packageName, GET_SIGNATURES)

            return getMD5MessageDigest(packageInfo.signatures[0].toByteArray())

        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }

    private fun getMD5MessageDigest(bytes: ByteArray): String {
        val sb = StringBuilder()
        try {
            val messageDigest = MessageDigest.getInstance("MD5")
            val digest:ByteArray = messageDigest.digest(bytes)//对字符串加密，返回字节数组

            for (b in digest) {
                val i :Int = b.toInt() and 0xff//获取低八位有效值
                var hexString = Integer.toHexString(i)//将整数转化为16进制
                if (hexString.length < 2) {
                    hexString = "0$hexString"//如果是一位的话，补0
                }
                sb.append(hexString)
            }
            return sb.toString()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }
        return sb.toString()
    }


}

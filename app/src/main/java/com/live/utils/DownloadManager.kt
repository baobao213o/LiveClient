package com.live.utils

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import androidx.core.content.FileProvider
import com.afollestad.materialdialogs.MaterialDialog
import com.live.LiveApp
import com.live.api.MainService
import com.live.network.HttpDownLoadCallBack
import com.live.network.RetrofitManager
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream

object DownloadManager {

    fun downloadApk(activity: Activity, url: String, callBack: HttpDownLoadCallBack) {
        RetrofitManager.getInstance()?.getService(MainService::class.java)?.downloadApk()?.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>?) {
                if (response == null) {
                    showErrorDialog(activity, "请求出错")
                    return
                }
                object : Thread() {
                    override fun run() {
                        super.run()
                        //保存到本地
                        val apkName = url.substring(url.indexOf("/") + 1, url.length)
                        DownloadManager.writeFile2Disk(activity, response, apkName, callBack)
                    }
                }.start()
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                showErrorDialog(activity, t.message!!)
            }
        })
    }

    private fun createFile(filename: String): File? {
        val folder = "nyannyannyan"
        val file: File
        file = if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
            File(Environment.getExternalStorageDirectory(), folder)
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                File(LiveApp.instance!!.filesDir, folder)
            } else {
                return null
            }
        }

        if (!file.exists()) {
            file.mkdir()
        }
        return File(file, filename)

    }

    private fun writeFile2Disk(activity: Activity, response: Response<ResponseBody>?, filename: String, httpCallBack: HttpDownLoadCallBack) {

        val file = createFile(filename)
        if (file == null) {
            showErrorDialog(activity, "存储空间不足")
            return
        }

        val responseBody = response!!.body()
        if (responseBody == null) {
            showErrorDialog(activity, "请求出错")
            return
        }
        var currentLength: Long = 0
        var os: OutputStream? = null

        val inputStream = responseBody.byteStream()
        val totalLength = responseBody.contentLength()
        try {
            os = FileOutputStream(file)
            var len = 0
            val buff = ByteArray(1024)
            while (len != -1) {
                len = inputStream!!.read(buff)
                os.write(buff, 0, len)
                currentLength += len.toLong()
                httpCallBack.onLoading(currentLength, totalLength)
            }
            // 通过Intent安装APK文件
            val i = Intent(Intent.ACTION_VIEW)
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                val authority = SecureValidate.pkgName!! + ".fileProvider"
                val apkUri = FileProvider.getUriForFile(activity, authority, file)
                i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                i.setDataAndType(apkUri, activity.contentResolver.getType(apkUri))
            } else {
                i.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive")
            }
            activity.startActivity(i)
            activity.finish()
        } catch (e: IOException) {
            e.printStackTrace()
            activity.finish()
        } finally {
            if (os != null) {
                try {
                    os.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }
            if (inputStream != null) {
                try {
                    inputStream.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }
        }
    }

    private fun showErrorDialog(activity: Activity, content: String) {


        MaterialDialog.Builder(activity).content(content).onPositive { _, _ -> activity.finish() }.positiveText("确定").cancelListener { activity.finish() }.build().show()

    }
}

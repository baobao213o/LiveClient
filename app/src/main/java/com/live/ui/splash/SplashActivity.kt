package com.live.ui.splash

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.live.R
import com.live.R.layout.dialog_tip
import com.live.api.MainService
import com.live.base.BaseActivity
import com.live.entity.ServerInfo
import com.live.network.CheckBaseServerInfoResult
import com.live.network.HttpDownLoadCallBack
import com.live.network.RetrofitManager
import com.live.ui.main.MainActivity
import com.live.utils.*
import kotlinx.android.synthetic.main.dialog_tip.*

class SplashActivity : BaseActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        RetrofitManager.getInstance()?.getService(MainService::class.java)?.getServerInfo(SecureValidate.printSignatureMD5(), SecureValidate.pkgName, SecureValidate.verCode.toString())?.enqueue(CheckBaseServerInfoResult(this, 2, object : CheckBaseServerInfoResult.HttpCallBack<ServerInfo> {
            override fun onSucess(data: ServerInfo) {
                ServerInfoManager.getInstance().setServerInfo(data)
                val curVerCode = SecureValidate.verCode
                if (curVerCode < data.version) {
                    MaterialDialogUtil.showMsgWithoutTitle(this@SplashActivity, "更新啦", "更新") {
                        if (!NetworkUtil.isWifiConnected) {
                            MaterialDialogUtil.showMsgWithoutTitle(this@SplashActivity, "未连接到WiFi，是否继续？", "确定") { fetchDownLoad(data.update_addr) }
                            return@showMsgWithoutTitle
                        }
                        fetchDownLoad(data.update_addr)
                    }
                    return
                }
                if (!TextUtils.isEmpty(data.hongbao_addr)) {
                    MaterialDialog(this@SplashActivity).message(null, "点一点支付宝领红包").positiveButton(null, "试试") {
                        val uri = Uri.parse(data.hongbao_addr)
                        val browserIntent = Intent(Intent.ACTION_VIEW, uri)
                        val mainIntent = Intent(this@SplashActivity, MainActivity::class.java)
                        val intents = arrayOf(mainIntent, browserIntent)
                        startActivities(intents)
                        finish()
                    }.negativeButton(null, "鬼信") { jump() }.cancelable(false).show()
                } else {
                    jump()
                }
            }

            override fun onFailure() {

            }
        }))
    }

    private fun jump() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }


    private fun fetchDownLoad(url: String) {
        MaterialDialog(this).customView(dialog_tip).show()
        DownloadManager.downloadApk(this, url, HttpDownLoadCallBack { current, total ->
            //更新进度条
            runOnUiThread { tv_common_dialog_tip.text = "下载进度:${(current.toFloat() / total * 100).toInt()}" }
        })
    }
}

package com.live.network

import android.app.Activity
import com.afollestad.materialdialogs.MaterialDialog
import com.live.entity.BaseServerResponse
import com.live.utils.MaterialDialogUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CheckBaseServerInfoResult<T> : Callback<BaseServerResponse<T>> {

    private var activity: Activity

    private var callBack: HttpCallBack<T>? = null

    //0 不显示dialog
    //1 显示dialog
    //2 显示强制关闭dialog
    private var showDialog = 1

    interface HttpCallBack<T> {

        fun onSucess(data: T)

        fun onFailure()
    }

    constructor(activity: Activity, callBack: HttpCallBack<T>) {
        this.activity = activity
        this.callBack = callBack
    }

    constructor(activity: Activity, showDialog: Int, callBack: HttpCallBack<T>) {
        this.activity = activity
        this.callBack = callBack
        this.showDialog = showDialog
    }


    override fun onResponse(call: Call<BaseServerResponse<T>>, response: Response<BaseServerResponse<T>>) {

        val temp = response.body()
        if (temp == null) {
            handleResult("请求出错")
            callBack?.onFailure()
            return
        }
        if (!BaseServerResponse.isSuccess(temp)) {
            handleResult(temp.msg)
            callBack?.onFailure()
            return
        }
        val data = temp.data
        if (data == null) {
            handleResult("请求数据为空")
            callBack?.onFailure()
            return
        }
        callBack?.onSucess(data)

    }

    override fun onFailure(call: Call<BaseServerResponse<T>>, t: Throwable) {
        handleResult("网络连接失败")
        callBack?.onFailure()

    }

    private fun handleResult(content: String) {
        when (showDialog) {
            1 -> showErrorDialog(activity, content)
            2 -> showForceErrorDialog(activity, content)
        }
    }


    private fun showErrorDialog(activity: Activity, content: String) {
        MaterialDialog(activity).message(null, content).positiveButton(null, "确定").show()
    }

    private fun showForceErrorDialog(activity: Activity, content: String) {
        MaterialDialogUtil.showMsgWithoutTitle(activity, content, "确定") {
            activity.finish()
        }
    }
}

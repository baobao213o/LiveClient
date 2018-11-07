package com.live.ui.my

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import com.live.R
import com.live.adpter.NoticeAdapter
import com.live.api.MainService
import com.live.base.BaseActivity
import com.live.entity.Notice
import com.live.network.CheckBaseServerInfoResult
import com.live.network.RetrofitManager
import com.live.utils.DeviceUtil
import kotlinx.android.synthetic.main.activity_notice.*
import java.util.*

class NoticeActivity : BaseActivity() {

    private var notices = ArrayList<Notice>()
    private var adapter: NoticeAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notice)

        et_activity_notice_send!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable) {
                switchState(s.isNotEmpty())
            }
        })

        btn_activity_notice_send.setOnClickListener {
            val notice = Notice()
            notice.feedbackContent = et_activity_notice_send!!.text.toString().trim { it <= ' ' }
            notice.feedbackModel = DeviceUtil.systemModel
            notice.feedbackOs = DeviceUtil.systemVersion
            notice.itemType = Notice.RIGHT
            adapter!!.addData(notice)
            rv_activity_notice!!.smoothScrollToPosition(adapter!!.data.size - 1)
            et_activity_notice_send!!.setText("")


            RetrofitManager.getInstance()?.getService(MainService::class.java)?.postFeedback(notice)?.enqueue(CheckBaseServerInfoResult(this, object : CheckBaseServerInfoResult.HttpCallBack<Boolean> {
                override fun onSucess(data: Boolean) {
                    if (data) {
                        val noticeS = Notice()
                        noticeS.noticeContent = "感谢反馈"
                        adapter!!.addData(noticeS)
                        rv_activity_notice!!.smoothScrollToPosition(adapter!!.data.size - 1)
                    } else {
                        val noticeF = Notice()
                        noticeF.noticeContent = "沒看到 再发一遍"
                        adapter!!.addData(noticeF)
                        rv_activity_notice!!.smoothScrollToPosition(adapter!!.data.size - 1)
                    }
                }

                override fun onFailure() {

                }
            }))
        }
        adapter = NoticeAdapter(notices)
        rv_activity_notice!!.adapter = adapter
        requestData()
    }

    private fun requestData() {
        RetrofitManager.getInstance()?.getService(MainService::class.java)?.notices?.enqueue(CheckBaseServerInfoResult(this, object : CheckBaseServerInfoResult.HttpCallBack<List<Notice>> {
            override fun onSucess(data: List<Notice>) {
                var lastDate: String? = null
                for (i in data.indices) {
                    val notice = data[i]
                    if (i == 0) {
                        notice.showDate = true
                        lastDate = notice.noticeDate
                        continue
                    }
                    if (notice.noticeDate == lastDate) {
                        notice.showDate = false
                        continue
                    }
                    if (notice.noticeDate != lastDate) {
                        notice.showDate = true
                        lastDate = notice.noticeDate
                    }
                }
                notices = data as ArrayList<Notice>
                adapter!!.setNewData(notices)
                rv_activity_notice!!.smoothScrollToPosition(adapter!!.data.size - 1)
            }

            override fun onFailure() {

            }
        }))
    }

    private fun switchState(flag: Boolean) {
        if (flag) {
            btn_activity_notice_send!!.background = getDrawable(R.drawable.ic_send_available)
            btn_activity_notice_send!!.isEnabled = true
        } else {
            btn_activity_notice_send!!.background = getDrawable(R.drawable.ic_send)
            btn_activity_notice_send!!.isEnabled = false
        }
    }
}

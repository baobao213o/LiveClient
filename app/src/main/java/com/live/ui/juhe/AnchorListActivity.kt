package com.live.ui.juhe

import android.os.Bundle
import com.live.R
import com.live.adpter.AnchorAdapter
import com.live.api.JuheService
import com.live.base.BaseActivity
import com.live.entity.Anchor
import com.live.network.RetrofitManager
import kotlinx.android.synthetic.main.activity_anchor.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class AnchorListActivity : BaseActivity() {


    private var list: List<Anchor.ZhuboBean> = ArrayList()
    private var adapter: AnchorAdapter? = null

    private var addr: String? = null

    private var title: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_anchor)

        adapter = AnchorAdapter(list)
        rv_activity_anchor!!.adapter = adapter

        addr = savedInstanceState?.getString("addr") ?: intent.getStringExtra("addr")
        title = savedInstanceState?.getString("title") ?: intent.getStringExtra("title")

        toolbar_activity_anchor!!.title = title
        setSupportActionBar(toolbar_activity_anchor)

        srl_activity_anchor!!.setOnRefreshListener { requestData() }

        srl_activity_anchor!!.post {
            srl_activity_anchor!!.isRefreshing = true
            requestData()
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        outState?.putString("title", title)
        outState?.putString("addr", addr)
        super.onSaveInstanceState(outState)
    }

    override fun onRestart() {
        super.onRestart()
        //从home点进来 跳轉到直播页
        if (BDLiveActivity.startBackground == 1) {
            startActivity(intent)
        }
    }

    private fun requestData() {
        RetrofitManager.getInstance()?.getService(JuheService::class.java)?.getAnchor(addr)?.enqueue(object : Callback<Anchor> {
            override fun onResponse(call: Call<Anchor>, response: Response<Anchor>) {
                srl_activity_anchor!!.isRefreshing = false
                val temp = response.body()
                if (temp != null) {
                    list = temp.zhubo
                    adapter!!.setNewData(list)
                }
            }

            override fun onFailure(call: Call<Anchor>, t: Throwable) {
                srl_activity_anchor!!.isRefreshing = false
            }
        })
    }
}

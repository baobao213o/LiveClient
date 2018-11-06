package com.live.ui.juhe

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.Nullable
import androidx.fragment.app.Fragment
import com.live.R
import com.live.adpter.JuheMenuAdapter
import com.live.api.JuheService
import com.live.entity.JuheMenu
import com.live.network.RetrofitManager
import kotlinx.android.synthetic.main.fragment_live_menu.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class LiveMenuFragment : Fragment() {

    private var list: List<JuheMenu.PingtaiBean> = ArrayList()
    private var adapter: JuheMenuAdapter? = null

    @Nullable
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_live_menu, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = JuheMenuAdapter(list)
        rv_fragment_live!!.adapter = adapter

        srl_fragment_live!!.setOnRefreshListener { requestData() }

        srl_fragment_live!!.post {
            srl_fragment_live!!.isRefreshing = true
            requestData()
        }
    }

    private fun requestData() {

        RetrofitManager.getInstance()?.getService(JuheService::class.java)?.liveMenu?.enqueue(object : Callback<JuheMenu> {
            override fun onResponse(call: Call<JuheMenu>, response: Response<JuheMenu>) {
                srl_fragment_live!!.isRefreshing = false
                val temp = response.body()
                if (temp != null) {
                    list = temp.pingtai
                    adapter!!.setNewData(list)
                }
            }

            override fun onFailure(call: Call<JuheMenu>, t: Throwable) {
                srl_fragment_live!!.isRefreshing = false
            }
        })
    }

}

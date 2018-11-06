package com.live.ui.my

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.Nullable
import androidx.fragment.app.Fragment
import com.live.R
import com.live.utils.SecureValidate
import kotlinx.android.synthetic.main.fragment_my.*

class MyFragment : Fragment() {

    @Nullable
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_my, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tv_fragment_my_vername!!.text = String.format(getString(R.string.my_version_name), SecureValidate.verName)
        tv_fragment_my_notice.setOnClickListener { startActivity(Intent(activity, NoticeActivity::class.java)) }
    }
}

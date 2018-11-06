package com.live.ui.main

import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentTransaction
import com.linroid.filtermenu.library.FilterMenu
import com.live.R
import com.live.base.BaseActivity
import com.live.entity.ServerInfo
import com.live.network.RetrofitManager
import com.live.ui.juhe.LiveMenuFragment
import com.live.ui.my.MyFragment
import com.live.utils.ServerInfoManager
import com.live.view.toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity() {

    private var liveMenuFragment: LiveMenuFragment? = null
    private var myFragment: MyFragment? = null

    private var serverInfo: ServerInfo? = null
    internal var index = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (savedInstanceState != null) {
            liveMenuFragment = supportFragmentManager.findFragmentByTag(FRAG_LIVE) as LiveMenuFragment
            myFragment = supportFragmentManager.findFragmentByTag(FRAG_MY) as MyFragment
        }
        serverInfo = ServerInfoManager.getInstance().getServerInfo(savedInstanceState)
        attachMenu()
        showFragment(FRAG_LIVE)
    }

    private fun attachMenu() {
        FilterMenu.Builder(this)
                .addItem(R.drawable.ic_action_add)
                .addItem(R.drawable.ic_action_live)
                .addItem(R.drawable.ic_action_user)
                .attach(menu_activity_main)
                .withListener(object : FilterMenu.OnMenuChangeListener {
                    override fun onMenuItemClick(view: View, position: Int) {
                        when (position) {
                            0 -> toast("敬请期待")
                            1 -> showFragment(FRAG_LIVE)
                            2 -> showFragment(FRAG_MY)
                        }
                    }

                    override fun onMenuCollapse() {

                    }

                    override fun onMenuExpand() {

                    }
                }).build()
    }

    private fun showFragment(index: String) {
        when (index) {
            FRAG_LIVE -> {
                RetrofitManager.getInstance()?.applyNewUrl(serverInfo!!.client_addr)
                toolbar_activity_main!!.title = "聚合直播"
            }
            FRAG_MY -> {
                RetrofitManager.getInstance()?.applyNewUrl(RetrofitManager.BASE_URL)
                toolbar_activity_main!!.title = "关于"
            }
        }
        if (this.index == index) {
            return
        }
        this.index = index
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
        hideFragment(fragmentTransaction)
        when (index) {
            FRAG_LIVE -> if (liveMenuFragment == null) {
                liveMenuFragment = LiveMenuFragment()
                fragmentTransaction.add(R.id.fl_activity_main_container, liveMenuFragment!!, FRAG_LIVE)
            } else {
                fragmentTransaction.show(liveMenuFragment!!)
            }
            FRAG_MY -> if (myFragment == null) {
                myFragment = MyFragment()
                fragmentTransaction.add(R.id.fl_activity_main_container, myFragment!!, FRAG_MY)
            } else {
                fragmentTransaction.show(myFragment!!)
            }
        }
        fragmentTransaction.commit()
    }

    private fun hideFragment(fragmentTransaction: FragmentTransaction) {
        if (index != FRAG_LIVE && liveMenuFragment != null) {
            fragmentTransaction.hide(liveMenuFragment!!)
        }
        if (index != FRAG_MY && myFragment != null) {
            fragmentTransaction.hide(myFragment!!)
        }
    }


    override fun onBackPressed() {
        moveTaskToBack(isTaskRoot)
    }

    companion object {

        private const val FRAG_LIVE = "LIVE"
        private const val FRAG_MY = "MY"
    }
}

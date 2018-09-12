package com.live.ui.main;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.linroid.filtermenu.library.FilterMenu;
import com.linroid.filtermenu.library.FilterMenuLayout;
import com.live.R;
import com.live.base.BaseActivity;
import com.live.entity.ServerInfo;
import com.live.network.RetrofitManager;
import com.live.ui.juhe.LiveMenuFragment;
import com.live.ui.my.MyFragment;
import com.live.utils.ServerInfoManager;
import com.live.view.ToastHelper;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity {

    @BindView(R.id.menu_activity_main)
    FilterMenuLayout menuActivityMain;
    @BindView(R.id.toolbar_activity_main)
    Toolbar toolbar_activity_main;


    private static final String FRAG_LIVE = "LIVE";
    private static final String FRAG_MY = "MY";

    private LiveMenuFragment liveMenuFragment;
    private MyFragment myFragment;

    private ServerInfo serverInfo;
    String index = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        if (savedInstanceState != null) {
            liveMenuFragment = (LiveMenuFragment) getSupportFragmentManager().findFragmentByTag(FRAG_LIVE);
            myFragment = (MyFragment) getSupportFragmentManager().findFragmentByTag(FRAG_MY);
        }
        serverInfo = ServerInfoManager.getInstance().getServerInfo(savedInstanceState);
        attachMenu();
        showFragment(FRAG_LIVE);
    }

    private void attachMenu() {
        new FilterMenu.Builder(this)
                .addItem(R.drawable.ic_action_add)
                .addItem(R.drawable.ic_action_live)
                .addItem(R.drawable.ic_action_user)
                .attach(menuActivityMain)
                .withListener(new FilterMenu.OnMenuChangeListener() {
                    @Override
                    public void onMenuItemClick(View view, int position) {
                        switch (position) {
                            case 0:
                                ToastHelper.showToast("敬请期待");
                                break;
                            case 1:
                                showFragment(FRAG_LIVE);
                                break;
                            case 2:
                                showFragment(FRAG_MY);
                                break;
                        }
                    }

                    @Override
                    public void onMenuCollapse() {

                    }

                    @Override
                    public void onMenuExpand() {

                    }
                }).build();
    }

    private void showFragment(String index) {
        switch (index) {
            case FRAG_LIVE:
                RetrofitManager.getInstance().applyNewUrl(serverInfo.client_addr);
                toolbar_activity_main.setTitle("聚合直播");
                break;
            case FRAG_MY:
                RetrofitManager.getInstance().applyNewUrl(RetrofitManager.BASE_URL);
                toolbar_activity_main.setTitle("关于");
                break;
        }
        if (this.index.equals(index)) {
            return;
        }
        this.index = index;
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
        hideFragment(fragmentTransaction);
        switch (index) {
            case FRAG_LIVE:
                if (liveMenuFragment == null) {
                    liveMenuFragment = new LiveMenuFragment();
                    fragmentTransaction.add(R.id.fl_activity_main_container, liveMenuFragment, FRAG_LIVE);
                } else {
                    fragmentTransaction.show(liveMenuFragment);
                }
                break;
            case FRAG_MY:
                if (myFragment == null) {
                    myFragment = new MyFragment();
                    fragmentTransaction.add(R.id.fl_activity_main_container, myFragment, FRAG_MY);
                } else {
                    fragmentTransaction.show(myFragment);
                }
                break;
        }
        fragmentTransaction.commit();
    }

    private void hideFragment(FragmentTransaction fragmentTransaction) {
        if (!index.equals(FRAG_LIVE) && liveMenuFragment != null) {
            fragmentTransaction.hide(liveMenuFragment);
        }
        if (!index.equals(FRAG_MY) && myFragment != null) {
            fragmentTransaction.hide(myFragment);
        }
    }


    @Override
    public void onBackPressed() {
        moveTaskToBack(isTaskRoot());
    }
}

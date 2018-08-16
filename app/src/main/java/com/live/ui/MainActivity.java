package com.live.ui;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.live.R;

public class MainActivity extends AppCompatActivity {

    private String index = FRAG_LIVE;
    private static final String FRAG_LIVE = "LIVE";
    private LiveMenuFragment liveMenuFragment;

    private static final String SELECTED_ITEM = "arg_selected_item";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState != null) {
            index = savedInstanceState.getString(SELECTED_ITEM, index);
            liveMenuFragment = (LiveMenuFragment) getSupportFragmentManager().findFragmentByTag(FRAG_LIVE);
        }
        showFragment();
    }

    private void showFragment() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
        hideFragment(fragmentTransaction);
        switch (index) {
            case FRAG_LIVE:
                if (liveMenuFragment == null) {
                    liveMenuFragment = new LiveMenuFragment();
                    fragmentTransaction.add(R.id.main_container, liveMenuFragment, FRAG_LIVE);
                } else {
                    fragmentTransaction.show(liveMenuFragment);
                }
                break;
        }
        fragmentTransaction.commit();
    }

    private void hideFragment(FragmentTransaction fragmentTransaction) {
        if (!index.equals(FRAG_LIVE) && liveMenuFragment != null) {
            fragmentTransaction.hide(liveMenuFragment);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(SELECTED_ITEM, index);
        super.onSaveInstanceState(outState);

    }
}

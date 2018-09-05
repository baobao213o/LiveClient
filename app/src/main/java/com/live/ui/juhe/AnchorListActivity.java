package com.live.ui.juhe;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.live.R;
import com.live.adpter.AnchorAdapter;
import com.live.api.JuheService;
import com.live.base.BaseActivity;
import com.live.entity.Anchor;
import com.live.network.RetrofitManager;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.prototypez.savestate.core.annotation.AutoRestore;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AnchorListActivity extends BaseActivity {

    @BindView(R.id.rv_activity_anchor)
    RecyclerView rv_activity_anchor;
    @BindView(R.id.srl_activity_anchor)
    SwipeRefreshLayout srl_activity_anchor;
    @BindView(R.id.toolbar_activity_anchor)
    Toolbar toolbar_activity_anchor;


    private List<Anchor.ZhuboBean> list = new ArrayList<>();
    private AnchorAdapter adapter;

    @AutoRestore("addr")
    String addr;

    @AutoRestore("title")
    String title;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anchor);
        ButterKnife.bind(this);

        adapter = new AnchorAdapter(list);
        rv_activity_anchor.setAdapter(adapter);

        addr = getIntent().getStringExtra("addr");
        title = getIntent().getStringExtra("title");

        toolbar_activity_anchor.setTitle(title);
        setSupportActionBar(toolbar_activity_anchor);


        srl_activity_anchor.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestData();
            }
        });

        srl_activity_anchor.post(new Runnable() {
            @Override
            public void run() {
                srl_activity_anchor.setRefreshing(true);
                requestData();
            }
        });
    }


    private void requestData() {
        RetrofitManager.getInstance().getService(JuheService.class).getAnchor(addr).enqueue(new Callback<Anchor>() {
            @Override
            public void onResponse(Call<Anchor> call, Response<Anchor> response) {
                srl_activity_anchor.setRefreshing(false);
                Anchor temp = response.body();
                if (temp != null) {
                    list = temp.zhubo;
                    adapter.setNewData(list);
                }
            }

            @Override
            public void onFailure(Call<Anchor> call, Throwable t) {
                srl_activity_anchor.setRefreshing(false);
            }
        });
    }


}

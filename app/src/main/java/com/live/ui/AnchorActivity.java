package com.live.ui;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;

import com.live.R;
import com.live.adpter.AnchorAdapter;
import com.live.api.LiveService;
import com.live.entity.Anchor;
import com.live.network.RetrofitManager;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AnchorActivity extends AppCompatActivity {

    @BindView(R.id.rv_activity_anchor)
    RecyclerView rv_activity_anchor;
    @BindView(R.id.srl_activity_anchor)
    SwipeRefreshLayout srl_activity_anchor;

    private List<Anchor.ZhuboBean> list = new ArrayList<>();
    private AnchorAdapter adapter;

    private String addr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anchor);
        ButterKnife.bind(this);

        adapter = new AnchorAdapter(list);
        rv_activity_anchor.setAdapter(adapter);

        if (savedInstanceState == null) {
            addr = getIntent().getStringExtra("addr");
        } else {
            addr = savedInstanceState.getString("addr");
        }

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

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("addr", addr);
        super.onSaveInstanceState(outState);
    }


    private void requestData() {
        RetrofitManager.getInstance().getService(LiveService.class).getAnchor(addr).enqueue(new Callback<Anchor>() {
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

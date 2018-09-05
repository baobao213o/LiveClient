package com.live.ui.juhe;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.live.R;
import com.live.adpter.JuheMenuAdapter;
import com.live.api.JuheService;
import com.live.entity.JuheMenu;
import com.live.network.RetrofitManager;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LiveMenuFragment extends Fragment {

    @BindView(R.id.rv_fragment_live)
    RecyclerView rv_fragment_live;
    @BindView(R.id.srl_fragment_live)
    SwipeRefreshLayout srl_fragment_live;
    Unbinder unbinder;
    private List<JuheMenu.PingtaiBean> list = new ArrayList<>();
    private JuheMenuAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_live_menu, container, false);
        unbinder = ButterKnife.bind(this, view);
        adapter = new JuheMenuAdapter(list);
        rv_fragment_live.setAdapter(adapter);

        srl_fragment_live.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestData();
            }
        });

        srl_fragment_live.post(new Runnable() {
            @Override
            public void run() {
                srl_fragment_live.setRefreshing(true);
                requestData();
            }
        });

        return view;
    }

    private void requestData() {
        RetrofitManager.getInstance().getService(JuheService.class).getLiveMenu().enqueue(new Callback<JuheMenu>() {
            @Override
            public void onResponse(Call<JuheMenu> call, Response<JuheMenu> response) {
                srl_fragment_live.setRefreshing(false);
                JuheMenu temp = response.body();
                if (temp != null) {
                    list = temp.pingtai;
                    adapter.setNewData(list);
                }
            }

            @Override
            public void onFailure(Call<JuheMenu> call, Throwable t) {
                srl_fragment_live.setRefreshing(false);
            }
        });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}

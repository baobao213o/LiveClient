package com.live.ui.my;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.live.R;
import com.live.utils.SecureValidate;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class MyFragment extends Fragment {


    @BindView(R.id.tv_fragment_my_notice)
    TextView tvFragmentMyNotice;
    @BindView(R.id.tv_fragment_my_vername)
    TextView tvFragmentMyVername;
    Unbinder unbinder;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my, container, false);
        unbinder = ButterKnife.bind(this, view);
        tvFragmentMyVername.setText(String.format(getString(R.string.my_version_name), SecureValidate.getVerName()));
        return view;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.tv_fragment_my_notice)
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_fragment_my_notice:
                startActivity(new Intent(getActivity(), NoticeActivity.class));
                break;
            default:

                break;
        }

    }
}

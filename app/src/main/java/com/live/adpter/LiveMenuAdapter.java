package com.live.adpter;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.facebook.drawee.view.SimpleDraweeView;
import com.live.R;
import com.live.entity.LiveMenu;
import com.live.ui.AnchorActivity;

import java.util.List;

public class LiveMenuAdapter extends BaseQuickAdapter<LiveMenu.PingtaiBean, BaseViewHolder> {


    public LiveMenuAdapter(@Nullable List<LiveMenu.PingtaiBean> data) {
        super(R.layout.item_live_menu, data);
    }

    @Override
    protected void convert(BaseViewHolder holder, final LiveMenu.PingtaiBean item) {
        String url;
        try {
            url = item.xinimg;
        } catch (Exception e) {
            url = "";
        }

        ((SimpleDraweeView) holder.getView(R.id.iv_live_menu_avator)).setImageURI(url);
        ((TextView) holder.getView(R.id.tv_live_menu_name)).setText(item.title);
        ((TextView) holder.getView(R.id.tv_live_menu_count)).setText(item.Number);
        holder.getView(R.id.iv_live_menu_avator).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, AnchorActivity.class);
                intent.putExtra("addr", item.address);
                mContext.startActivity(intent);
            }
        });
    }


}

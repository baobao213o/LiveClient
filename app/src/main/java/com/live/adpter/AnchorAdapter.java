package com.live.adpter;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.facebook.drawee.view.SimpleDraweeView;
import com.live.R;
import com.live.entity.Anchor;
import com.live.ui.BDLiveActivity;

import java.util.List;

public class AnchorAdapter extends BaseQuickAdapter<Anchor.ZhuboBean, BaseViewHolder> {


    public AnchorAdapter(@Nullable List<Anchor.ZhuboBean> data) {
        super(R.layout.item_anchor, data);
    }

    @Override
    protected void convert(BaseViewHolder holder, final Anchor.ZhuboBean item) {
        String url;
        try {
            url = item.img;
        } catch (Exception e) {
            url = "";
        }

        ((SimpleDraweeView) holder.getView(R.id.iv_anchor_avator)).setImageURI(url);
        ((TextView) holder.getView(R.id.tv_anchor_name)).setText(item.title);
        holder.getView(R.id.iv_anchor_avator).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, BDLiveActivity.class);
                intent.putExtra("url", item.address);
                mContext.startActivity(intent);
            }
        });

    }


}

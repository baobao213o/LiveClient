package com.live.adpter;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.facebook.drawee.view.SimpleDraweeView;
import com.live.R;
import com.live.entity.Anchor;
import com.live.ui.juhe.BDLiveActivity;

import java.util.List;

import androidx.annotation.Nullable;

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
                Intent intent = ((Activity) mContext).getIntent();
                if (intent == null) {
                    intent = new Intent(mContext, BDLiveActivity.class);
                } else {
                    ComponentName componentName = new ComponentName(mContext, BDLiveActivity.class);
                    intent.setComponent(componentName);
                }
                intent.putExtra("url", item.address);
                mContext.startActivity(intent);
            }
        });
    }


}

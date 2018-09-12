package com.live.adpter;

import android.text.TextUtils;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.util.MultiTypeDelegate;
import com.live.R;
import com.live.entity.Notice;

import java.util.List;

public class NoticeAdapter extends BaseQuickAdapter<Notice, BaseViewHolder> {

    public NoticeAdapter(List<Notice> data) {
        super(data);
        setMultiTypeDelegate(new MultiTypeDelegate<Notice>() {
            @Override
            protected int getItemType(Notice entity) {
                return entity.itemType;
            }
        });
        getMultiTypeDelegate()
                .registerItemType(Notice.LEFT, R.layout.item_notice_left)
                .registerItemType(Notice.RIGHT, R.layout.item_notice_right);
    }

    @Override
    protected void convert(BaseViewHolder holder, Notice item) {
        switch (holder.getItemViewType()) {
            case Notice.LEFT:
                holder.setText(R.id.tv_item_notice_left_content, item.noticeContent);
                if (item.showDate && !TextUtils.isEmpty(item.noticeDate)) {
                    holder.getView(R.id.tv_item_notice_left_date).setVisibility(View.VISIBLE);
                    holder.setText(R.id.tv_item_notice_left_date, item.noticeDate);
                } else {
                    holder.getView(R.id.tv_item_notice_left_date).setVisibility(View.GONE);
                }
                break;
            case Notice.RIGHT:
                holder.setText(R.id.tv_item_notice_right_content, item.feedbackContent);
                break;
        }
    }
}
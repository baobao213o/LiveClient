package com.live.ui.my;

import android.os.Bundle;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.live.R;
import com.live.adpter.NoticeAdapter;
import com.live.api.MainService;
import com.live.base.BaseActivity;
import com.live.entity.Notice;
import com.live.network.CheckBaseServerInfoResult;
import com.live.network.RetrofitManager;
import com.live.utils.DeviceUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NoticeActivity extends BaseActivity {

    @BindView(R.id.btn_activity_notice_send)
    Button btnActivityNoticeSend;
    @BindView(R.id.et_activity_notice_send)
    EditText etActivityNoticeSend;
    @BindView(R.id.rv_activity_notice)
    RecyclerView rvActivityNotice;
    private ArrayList<Notice> notices = new ArrayList<>();
    private NoticeAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice);
        ButterKnife.bind(this);
        etActivityNoticeSend.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                switchState(s.length() > 0);
            }
        });
        adapter = new NoticeAdapter(notices);
        rvActivityNotice.setAdapter(adapter);
        requestData();
    }

    private void requestData() {
        RetrofitManager.getInstance().getService(MainService.class).getNotices().enqueue(new CheckBaseServerInfoResult<>(this, new CheckBaseServerInfoResult.HttpCallBack<List<Notice>>() {
            @Override
            public void onSucess(List<Notice> data) {
                String lastDate = null;
                for (int i = 0; i < data.size(); i++) {
                    Notice notice = data.get(i);
                    if (i == 0) {
                        notice.showDate = true;
                        lastDate = notice.noticeDate;
                        continue;
                    }
                    if (notice.noticeDate.equals(lastDate)) {
                        notice.showDate = false;
                        continue;
                    }
                    if (!notice.noticeDate.equals(lastDate)) {
                        notice.showDate = true;
                        lastDate = notice.noticeDate;
                    }
                }
                notices = (ArrayList<Notice>) data;
                adapter.setNewData(notices);
                rvActivityNotice.smoothScrollToPosition(adapter.getData().size() - 1);
            }

            @Override
            public void onFailure() {

            }
        }));
    }

    private void switchState(boolean flag) {
        if (flag) {
            btnActivityNoticeSend.setBackground(getDrawable(R.drawable.ic_send_available));
            btnActivityNoticeSend.setEnabled(true);
        } else {
            btnActivityNoticeSend.setBackground(getDrawable(R.drawable.ic_send));
            btnActivityNoticeSend.setEnabled(false);
        }
    }

    @OnClick(R.id.btn_activity_notice_send)
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_activity_notice_send:
                Notice notice = new Notice();
                notice.feedbackContent = etActivityNoticeSend.getText().toString().trim();
                notice.feedbackModel = DeviceUtil.getSystemModel();
                notice.feedbackOs = DeviceUtil.getSystemVersion();
                notice.itemType = Notice.RIGHT;
                adapter.addData(notice);
                rvActivityNotice.smoothScrollToPosition(adapter.getData().size() - 1);
                etActivityNoticeSend.setText("");


                RetrofitManager.getInstance().getService(MainService.class).postFeedback(notice).enqueue(new CheckBaseServerInfoResult<>(this, new CheckBaseServerInfoResult.HttpCallBack<Boolean>() {
                    @Override
                    public void onSucess(Boolean data) {
                        if (data) {
                            Notice notice = new Notice();
                            notice.noticeContent = "感谢反馈";
                            adapter.addData(notice);
                            rvActivityNotice.smoothScrollToPosition(adapter.getData().size() - 1);
                        } else {
                            Notice notice = new Notice();
                            notice.noticeContent = "沒看到 再发一遍";
                            adapter.addData(notice);
                            rvActivityNotice.smoothScrollToPosition(adapter.getData().size() - 1);
                        }

                    }

                    @Override
                    public void onFailure() {

                    }
                }));


                break;
            default:
                break;
        }
    }
}

package com.live.entity;

import com.google.gson.annotations.SerializedName;

public class Notice {

    public static final int LEFT = 1;
    public static final int RIGHT = 2;
    public int itemType = LEFT;

    @SerializedName("date")
    public String noticeDate;
    @SerializedName("title")
    public String noticeTitle;
    @SerializedName("content")
    public String noticeContent;
    public boolean showDate = true;

    //系统版本
    public String feedbackOs;
    //机型
    public String feedbackModel;
    public String feedbackContent;
}

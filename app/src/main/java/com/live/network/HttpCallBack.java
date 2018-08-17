package com.live.network;

import java.io.IOException;

import okhttp3.ResponseBody;

public interface HttpCallBack {

    void onSuccess(int requestCode, ResponseBody response);

    void onFailure(int requestCode, IOException e);


}

package com.live.api;

import com.live.Constant;
import com.live.entity.BaseServerResponse;
import com.live.entity.ServerInfo;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Streaming;

public interface ValidateService {

    @GET(Constant.VALIDATE_URL)
    Call<BaseServerResponse<ServerInfo>> getServerInfo(@Query("sign") String sign, @Query("pkgname") String pkgname, @Query("version") String version);

    @Streaming
    @GET(Constant.DOWN_URL)
    Call<ResponseBody> downloadApk();


}

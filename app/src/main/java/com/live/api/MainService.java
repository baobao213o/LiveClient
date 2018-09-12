package com.live.api;

import com.live.Constant;
import com.live.entity.BaseServerResponse;
import com.live.entity.Notice;
import com.live.entity.ServerInfo;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.Streaming;

public interface MainService {

    @GET(Constant.VALIDATE_URL)
    Call<BaseServerResponse<ServerInfo>> getServerInfo(@Query("sign") String sign, @Query("pkgname") String pkgname, @Query("version") String version);

    @Streaming
    @GET(Constant.DOWN_URL)
    Call<ResponseBody> downloadApk();

    @GET(Constant.NOTICE_URL)
    Call<BaseServerResponse<List<Notice>>> getNotices();

    @Headers({"Content-Type: application/json","Accept: application/json"})//需要添加头
    @POST(Constant.FEEDBACK_URL)
    Call<BaseServerResponse<Boolean>> postFeedback(@Body Notice notice);

}

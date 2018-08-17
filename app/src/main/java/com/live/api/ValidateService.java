package com.live.api;

import com.live.Constant;
import com.live.entity.BaseServerResponse;
import com.live.entity.ServerInfo;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ValidateService {

    @GET(Constant.VALIDATE_URL)
    Call<BaseServerResponse<ServerInfo>> getServerInfo(@Query("sign") String sign, @Query("pkgname") String pkgname);

}

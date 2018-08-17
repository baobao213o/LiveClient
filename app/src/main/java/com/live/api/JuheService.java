package com.live.api;

import com.live.entity.Anchor;
import com.live.entity.JuheMenu;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface JuheService {

    @GET("json.txt")
    Call<JuheMenu> getLiveMenu();

    @GET("{addr}")
    Call<Anchor> getAnchor(@Path("addr") String addr);

}

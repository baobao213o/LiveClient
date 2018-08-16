package com.live.api;

import com.live.entity.Anchor;
import com.live.entity.LiveMenu;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface LiveService {

    @GET("json.txt")
    Call<LiveMenu> getLiveMenu();

    @GET("{addr}")
    Call<Anchor> getAnchor(@Path("addr") String addr);

}

package com.live.network;


import android.content.Context;
import android.support.v4.util.ArrayMap;

import com.live.LiveApp;

import java.io.File;
import java.net.Proxy;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static org.apache.http.conn.ssl.SSLSocketFactory.STRICT_HOSTNAME_VERIFIER;

public class RetrofitManager {


    static {
        System.loadLibrary("const");
    }

    //    String server_url = "http://35.201.172.238:8080/live-nyannyannyan/";
    private static native String getRemoteUrl();

    //    String server_url = "http://192.168.191.1:8080/";
    private static native String getLocalDebugUrl();

    //    String server_url = "http://192.168.191.1:8080/live-nyannyannyan/";
    private static native String getLocalUrl();


    private OkHttpClient okHttpClient;

    private static volatile RetrofitManager instance;

    private Retrofit retrofit;

    private ArrayMap<String, Object> serviceCacheMap;

    public final static String BASE_URL = getRemoteUrl();
//    public final static String BASE_URL = getLocalDebugUrl();
//    public final static String BASE_URL = getLocalUrl();

    private RetrofitManager() {
        serviceCacheMap = new ArrayMap<>();
        if (retrofit == null) {
            init();
        }
    }

    public static RetrofitManager getInstance() {
        if (instance == null) {
            synchronized (RetrofitManager.class) {
                if (instance == null) {
                    instance = new RetrofitManager();
                }
            }
        }
        return instance;
    }

    private void init() {
        retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .client(initOkHttpClient())
                .baseUrl(BASE_URL)
                .build();
    }


    public void applyNewUrl(String url) {
        if (retrofit.baseUrl().toString().equals(url)) {
            return;
        }
        retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .client(initOkHttpClient())
                .baseUrl(url)
                .build();
    }

    public <T> T getService(Class<T> service) {
        T result = (T) serviceCacheMap.get(service.getName());
        if (result == null) {
            result = retrofit.create(service);
            serviceCacheMap.put(service.getName(), result);
        }
        return result;
    }

    private OkHttpClient initOkHttpClient() {
        if (okHttpClient == null) {
            okHttpClient = createOkhttp();
        }
        return okHttpClient;
    }

    private static OkHttpClient createOkhttp() {
        //缓存文件夹
        String path;
        Context context = LiveApp.getInstance();
        if (context.getExternalCacheDir() != null) {
            path = context.getExternalCacheDir().toString();
        } else {
            path = context.getCacheDir().toString();
        }
        File cacheFile = new File(path, "cache");
        //缓存大小为10M
        int cacheSize = 10 * 1024 * 1024;
        //创建缓存对象
        Cache cache = new Cache(cacheFile, cacheSize);

        return new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .hostnameVerifier(STRICT_HOSTNAME_VERIFIER)
                .addInterceptor(new LogInterceptor(LogInterceptor.BODY))
                .cache(cache)
                .proxy(Proxy.NO_PROXY)
                .build();
    }

    public void cancelAllRequest() {
        if (okHttpClient != null) {
            okHttpClient.dispatcher().cancelAll();
        }
    }
}


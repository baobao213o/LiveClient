package com.live.network


import androidx.collection.ArrayMap
import com.live.LiveApp
import okhttp3.Cache
import okhttp3.OkHttpClient
import org.apache.http.conn.ssl.SSLSocketFactory.STRICT_HOSTNAME_VERIFIER
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.net.Proxy
import java.util.concurrent.TimeUnit

class RetrofitManager
//    public final static String BASE_URL = getLocalDebugUrl();
//    public final static String BASE_URL = getLocalUrl();

private constructor() {


    private var okHttpClient: OkHttpClient? = null

    private var retrofit: Retrofit? = null

    private val serviceCacheMap: ArrayMap<String, Any> = ArrayMap()

    init {
        if (retrofit == null) {
            init()
        }
    }

    private fun init() {
        retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .client(initOkHttpClient())
                .baseUrl(BASE_URL)
                .build()
    }


    fun applyNewUrl(url: String) {
        if (retrofit?.baseUrl().toString() == url) {
            return
        }
        retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .client(initOkHttpClient())
                .baseUrl(url)
                .build()
    }

    fun <T> getService(service: Class<T>): T? {
        var result: T? = serviceCacheMap[service.name] as T
        if (result == null) {
            result = retrofit!!.create(service)
            serviceCacheMap[service.name] = result
        }
        return result
    }

    private fun initOkHttpClient(): OkHttpClient {
        if (okHttpClient == null) {
            okHttpClient = createOkhttp()
        }
        return okHttpClient as OkHttpClient
    }

    fun cancelAllRequest() {
        if (okHttpClient != null) {
            okHttpClient!!.dispatcher().cancelAll()
        }
    }

    companion object {


        init {
            System.loadLibrary("const")
        }

        //    String server_url = "http://35.201.172.238:8080/live-nyannyannyan/";
        private external fun getRemoteUrl(): String

        //    String server_url = "http://192.168.191.1:8080/";
        private external fun getLocalDebugUrl(): String

        //    String server_url = "http://192.168.191.1:8080/live-nyannyannyan/";
        private external fun getLocalUrl(): String


        private val remoteUrl = getRemoteUrl()

//        private val localDebugUrl = getLocalDebugUrl()

//        private val localUrl = getLocalUrl()

        @Volatile
        private var instance: RetrofitManager? = null

        val BASE_URL = remoteUrl

        fun getInstance(): RetrofitManager? {
            if (instance == null) {
                synchronized(RetrofitManager::class.java) {
                    if (instance == null) {
                        instance = RetrofitManager()
                    }
                }
            }
            return instance
        }

        private fun createOkhttp(): OkHttpClient {
            //缓存文件夹
            val path: String
            val context = LiveApp.instance
            path = if (context!!.externalCacheDir != null) {
                context.externalCacheDir!!.toString()
            } else {
                context.cacheDir.toString()
            }
            val cacheFile = File(path, "cache")
            //缓存大小为10M
            val cacheSize = 10 * 1024 * 1024
            //创建缓存对象
            val cache = Cache(cacheFile, cacheSize.toLong())

            return OkHttpClient.Builder()
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .writeTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(10, TimeUnit.SECONDS)
                    .hostnameVerifier(STRICT_HOSTNAME_VERIFIER)
                    .addInterceptor(LogInterceptor(LogInterceptor.BODY))
                    .cache(cache)
                    .proxy(Proxy.NO_PROXY)
                    .build()
        }
    }
}


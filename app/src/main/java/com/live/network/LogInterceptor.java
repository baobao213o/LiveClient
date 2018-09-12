package com.live.network;


import com.live.utils.CommonLogger;
import com.live.utils.IOUtil;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

import okhttp3.Connection;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.internal.http.HttpHeaders;
import okio.Buffer;

public class LogInterceptor implements Interceptor {

    public final static int NONE = 0;       //不打印log
    public final static int BASIC = 1;      //只打印 请求首行 和 响应首行
    public final static int HEADERS = 2;    //打印请求和响应的所有 Header
    public final static int BODY = 3;       //所有数据全部打印


    private int printLevel;
    private static final Charset UTF8 = Charset.forName("UTF-8");


    public LogInterceptor(int level) {
        printLevel = BODY;
    }


    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        if (printLevel == NONE) {
            return chain.proceed(request);
        }

        //请求日志拦截
        logForRequest(request, chain.connection());

        //执行请求，计算请求时间
        long startNs = System.nanoTime();
        Response response;
        try {
            response = chain.proceed(request);
        } catch (Exception e) {
            log("<-- HTTP FAILED: " + e);
            throw e;
        }
        long tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs);
        //响应日志拦截
        return logForResponse(response, tookMs, chain);
    }


    private void logForRequest(Request request, Connection connection) throws IOException {
        boolean logBody = (printLevel == BODY);
        boolean logHeaders = (printLevel == BODY || printLevel == HEADERS);
        RequestBody requestBody = request.body();
        boolean hasRequestBody = requestBody != null;
        Protocol protocol = connection != null ? connection.protocol() : Protocol.HTTP_1_1;

        try {
            String requestStartMessage = "--> " + request.method() + ' ' + request.url() + ' ' + protocol;
            log(requestStartMessage);

            if (logHeaders) {
                if (hasRequestBody) {
                    // Request body headers are only present when installed as a network interceptor. Force
                    // them to be included (when available) so there values are known.
                    if (requestBody.contentType() != null) {
                        log("\tContent-Type: " + requestBody.contentType());
                    }
                    if (requestBody.contentLength() != -1) {
                        log("\tContent-Length: " + requestBody.contentLength());
                    }
                }
                Headers headers = request.headers();
                for (int i = 0, count = headers.size(); i < count; i++) {
                    String name = headers.name(i);
                    // Skip headers from the request body as they are explicitly logged above.
                    if (!"Content-Type".equalsIgnoreCase(name) && !"Content-Length".equalsIgnoreCase(name)) {
                        log("\t" + name + ": " + headers.value(i));
                    }
                }
                log(" ");
                if (logBody && hasRequestBody) {
                    if (isPlaintext(requestBody.contentType())) {
                        bodyToString(request);
                    } else {
                        log("\tbody: maybe [binary body], omitted!");
                    }
                }
            }
        } catch (Exception e) {
            CommonLogger.printStackTrace(e);
        } finally {
            log("--> END " + request.method());
        }
    }


    private void log(String message) {
        CommonLogger.e(message);
    }

    private Response logForResponse(Response response, long tookMs, Chain chain) {
        Response.Builder builder = response.newBuilder();
        Response clone = builder.build();
        ResponseBody responseBody = clone.body();
        boolean logBody = (printLevel == BODY);
        boolean logHeaders = (printLevel == BODY || printLevel == HEADERS);

        try {
            log("<-- " + clone.code() + ' ' + clone.message() + ' ' + clone.request().url() + " (" + tookMs + "ms）");
            if (logHeaders) {
                Headers headers = clone.headers();
                for (int i = 0, count = headers.size(); i < count; i++) {
                    log("\t" + headers.name(i) + ": " + headers.value(i));
                }
                log(" ");
                if (logBody && HttpHeaders.hasBody(clone)) {
                    if (responseBody == null) return response;

                    if (isPlaintext(responseBody.contentType())) {
                        byte[] bytes = IOUtil.toByteArray(responseBody.byteStream());
                        MediaType contentType = responseBody.contentType();
                        String body = new String(bytes, getCharset(contentType));
                        log("\tbody:" + body);
                        responseBody = ResponseBody.create(responseBody.contentType(), bytes);

                        return response.newBuilder().body(responseBody).build();
                    } else {
                        log("\tbody: maybe [binary body], omitted!");
                    }
                }
            }
        } catch (Exception e) {
            CommonLogger.printStackTrace(e);
        } finally {
            log("<-- END HTTP");
        }
        return response;
    }


    private static Charset getCharset(MediaType contentType) {
        Charset charset = contentType != null ? contentType.charset() : UTF8;
        if (charset == null) charset = UTF8;
        return charset;
    }


    private static boolean isPlaintext(MediaType mediaType) {
        if (mediaType == null) return false;
        if (mediaType.type() != null && mediaType.type().equals("text")) {
            return true;
        }
        String subtype = mediaType.subtype();
        if (subtype != null) {
            subtype = subtype.toLowerCase();
            if (subtype.contains("x-www-form-urlencoded") || subtype.contains("json") || subtype.contains("xml") || subtype.contains("html")) //
                return true;
        }
        return false;
    }

    private void bodyToString(Request request) {
        try {
            Request copy = request.newBuilder().build();
            RequestBody body = copy.body();
            if (body == null) return;
            Buffer buffer = new Buffer();
            body.writeTo(buffer);
            Charset charset = getCharset(body.contentType());
            log("\tbody:" + buffer.readString(charset));
        } catch (Exception e) {
            CommonLogger.printStackTrace(e);
        }
    }
}

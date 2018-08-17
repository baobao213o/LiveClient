package com.live.entity;

import java.io.Serializable;

public class BaseServerResponse<T> implements Serializable{

    //状态码
    //200	OK	请求成功
    public static final String STATUS_OK = "200";
    //201	CREATED	创建成功
    public static final String STATUS_CREATED = "201";
    //202	ACCEPTED	更新成功
    public static final String STATUS_ACCEPTED = "202";
    //400	BAD REQUEST	请求的地址不存在或者包含不支持的参数
    public static final String STATUS_BAD_REQUEST = "400";
    //401	UNAUTHORIZED	未授权
    public static final String STATUS_UNAUTHORIZED = "401";
    //403	FORBIDDEN	被禁止访问
    public static final String STATUS_FORBIDDEN = "403";
    //404	NOT FOUND	请求的资源不存在
    public static final String STATUS_NOT_FOUND = "404";
    //500	INTERNAL SERVER ERROR	内部错误
    public static final String STATUS_INTERNAL_SERVER_ERROR = "500";


    public String code;
    public String msg;
    public T data;

    public static boolean isSuccess(BaseServerResponse response) {
        return response.code.equals(STATUS_OK);
    }

}

package com.bill.zhihu.api.service;

import com.bill.zhihu.api.bean.LoginResponse;
import com.bill.zhihu.api.utils.XHeaders;
import com.squareup.okhttp.ResponseBody;

import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Headers;
import retrofit.http.POST;
import rx.Observable;

/**
 *
 * 登陆 api service
 *
 * Created by bill_lv on 2016/2/2.
 */
public interface LoginApiService{

    @POST("/sign_in")
    @Headers({
            XHeaders.ACCEPT_ENCODE,
            XHeaders.GOOGLE_UA,
            XHeaders.API_HOST,
            "Content-Type: application/x-www-form-urlencoded; charset=UTF-8",
            "Connection: Keep-Alive",
    })
    Observable<LoginResponse> signIn(@Body String fields);

    @GET("/captcha")
    @Headers({
            XHeaders.ACCEPT_ENCODE,
            "Authorization: oauth 8d5227e0aaaa4797a763ac64e0c3b8",
            XHeaders.ZHIHU_UA,
            XHeaders.X_API_VERSION,
            XHeaders.X_APP_VERSION,
//            XHeaders.X_APP_ZA,
            "Host: api.zhihu.com",
            "Connection: Keep-Alive",
    })
    Observable<ResponseBody> captcha();

}
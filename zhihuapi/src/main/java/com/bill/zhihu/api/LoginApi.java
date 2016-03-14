package com.bill.zhihu.api;

import android.content.Context;
import android.content.SharedPreferences;

import com.bill.zhihu.api.bean.LoginResponse;
import com.bill.zhihu.api.cookie.ZhihuCookieManager;
import com.bill.zhihu.api.service.API;
import com.bill.zhihu.api.service.LoginApiService;
import com.bill.zhihu.api.utils.AuthStore;
import com.squareup.okhttp.ResponseBody;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;

/**
 * 登陆 API，包含设置不需要验证码和登陆两个接口。
 * <p/>
 * 登陆前务必使用 captcha 方法设置不需要验证码，并且获取 cookie，否则将登陆失败
 * <p/>
 * Created by bill_lv on 2016/2/16.
 */
public class LoginApi implements API {
    private static final String PEC = "ecbefbf6b17e47ecb9035107866380";
    private static final String CLIENT_ID = "8d5227e0aaaa4797a763ac64e0c3b8";

    private LoginApiService service;

    public LoginApi(LoginApiService service) {
        this.service = service;
    }

    /**
     * 登陆接口，反编译了 app 找到了 post body 中加密的字段算法。
     *
     * @param username
     * @param password
     * @return
     */
    public Observable<Boolean> login(String username, String password) {
        String time = System.currentTimeMillis() / 1000L + "";

        String field = "client_id=" + CLIENT_ID +
                "&grant_type=password&password=" + password + "&scope" +
                "&signature=" + signature(time) +
                "&source=com.zhihu.android&timestamp=" + time + "&username=" + username + "&uuid";

        service.captcha();
        return service.signIn(field).map(new Func1<LoginResponse, Boolean>() {
            @Override
            public Boolean call(LoginResponse signInResponse) {
                AuthStore.setAccessToken(signInResponse.accessToken);
                AuthStore.setTokenType(signInResponse.tokenType);
                AuthStore.setUID(signInResponse.UID);
                AuthStore.setUnlockTicket(signInResponse.unlockTicket);

                updateLoginState(true);

                return true;
            }
        });
    }

    // 加密字符串
    private String signature(String timestamp) {
        String src = "password" + CLIENT_ID + "com.zhihu.android" + timestamp;

        SecretKeySpec keySpec = new SecretKeySpec(PEC.getBytes(), "HmacSHA1");

        try {
            Mac localMac = Mac.getInstance("HmacSHA1");
            localMac.init(keySpec);
            byte[] b = localMac.doFinal(src.getBytes());
            StringBuffer sb = new StringBuffer();

            for (int i = 0; i < b.length; i++) {
                sb.append(String.format("%02x", new Object[]{Byte.valueOf(b[i])}));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 设置登录不需要验证码
     *
     * @return
     */
    public Observable<Void> captcha() {
        return service.captcha().map(new Func1<ResponseBody, Void>() {
            @Override
            public Void call(ResponseBody responseBody) {
                return null;
            }
        });
    }

    /**
     * 查看登陆状态
     *
     * @return true 已登录, false 未登录
     */
    public Observable<Boolean> haveLogin() {
        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                subscriber.onNext(getLoginState());
                subscriber.onCompleted();
            }
        });
    }

    private void updateLoginState(boolean haveLogin) {
        SharedPreferences sp = ZhihuApi.getContext().getSharedPreferences("login_state", Context.MODE_PRIVATE);
        sp.edit().putBoolean("login_state", haveLogin).commit();
    }

    private boolean getLoginState() {
        SharedPreferences sp = ZhihuApi.getContext().getSharedPreferences("login_state", Context.MODE_PRIVATE);
        return sp.getBoolean("login_state", false);
    }
}
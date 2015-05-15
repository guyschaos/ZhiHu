package com.bill.zhihu.api.cmd;

import android.graphics.Bitmap;

import com.android.volley.AuthFailureError;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.bill.jeson.Jeson;
import com.bill.zhihu.R;
import com.bill.zhihu.ZhihuApp;
import com.bill.zhihu.api.ZhihuApi;
import com.bill.zhihu.api.net.ZhihuStringRequest;
import com.bill.zhihu.api.utils.ToastUtil;
import com.bill.zhihu.api.utils.ZhihuLog;
import com.bill.zhihu.api.utils.ZhihuURL;
import com.bill.zhihu.login.LoginRequestCode;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * 登陆
 * 
 * @author Bill Lv
 *
 */
public class CmdLogin extends Command {

    public static final int LOGIN_SUCCESS = 0;
    public static final int LOGIN_FAILED = 1;

    // 请输入验证码
    private static final int ERRCODE_INPUT_CAPTCHA = 269;
    // 验证码错误
    private static final int ERRCODE_CAPTCHA_ERROR = 270;
    // 密码长度错误
    private static final int ERRCODE_PWD_LENGTH_ERROR = 4000;
    // 账号或密码错误
    private static final int ERRCODE_PWD_ACCOUNT_ERROR = 4038;

    private CallbackListener listener;
    private String account;
    private String pwd;
    private String captcha;
    private static boolean haveCaptcha;

    private ZhihuStringRequest request;

    public CmdLogin(String account, String pwd, String captcha) {
        this.account = account;
        this.pwd = pwd;
        this.captcha = captcha;
    }

    @Override
    public void exec() {

        request = new ZhihuStringRequest(Method.POST, ZhihuURL.LOGIN,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        ZhihuLog.d(TAG, "login response " + response);
                        try {
                            JSONObject responseJson = new JSONObject(response);
                            int loginStatus = responseJson.getInt("r");
                            // 由于登陆成功和失败返回的json对象结构不一样，所以要先判断是否登陆成功
                            switch (loginStatus) {
                            case LOGIN_SUCCESS:
                                listener.callback(LOGIN_SUCCESS, null);
                                ToastUtil.showShortToast(ZhihuApp.getContext()
                                        .getResources()
                                        .getString(R.string.login_success));
                                break;
                            case LOGIN_FAILED:
                                LoginRequestCode code = Jeson.createBean(
                                        LoginRequestCode.class, response);
                                switch (code.getErrorCode()) {
                                case ERRCODE_INPUT_CAPTCHA:
                                    haveCaptcha = true;
                                    break;
                                case ERRCODE_CAPTCHA_ERROR:
                                    break;
                                case ERRCODE_PWD_LENGTH_ERROR:
                                    break;
                                case ERRCODE_PWD_ACCOUNT_ERROR:
                                    break;
                                default:
                                    break;
                                }
                                CmdImgCaptcha captcha = new CmdImgCaptcha();
                                captcha.setOnCmdCallBack(new CmdImgCaptcha.CallbackListener() {

                                    @Override
                                    public void callback(Bitmap captchaImg) {
                                        listener.callback(LOGIN_FAILED,
                                                captchaImg);
                                    }
                                });
                                ZhihuApi.execCmd(captcha);

                                ToastUtil
                                        .showShortToast(code.getMsg().getMsg());
                                break;
                            default:
                                break;
                            }

                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

                    }

                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        ZhihuLog.d(TAG, error);
                    }

                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("_xsrf", xsrf);
                params.put("email", account);
                params.put("password", pwd);
                params.put("rememberme", "y");
                if (haveCaptcha && captcha != null) {
                    params.put("captcha", captcha);
                }
                for (String value : params.values()) {
                    ZhihuLog.d(TAG, value);
                }
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                // TODO Auto-generated method stub
                Map<String, String> headers = super.getHeaders();
                headers.put("X-Requested-With", "XMLHttpRequest");
                headers.put("Referer", "http://www.zhihu.com/#signin");
                headers.put("Host", "www.zhihu.com");
                headers.put("Accept", "*/*");
                return headers;
            }

        };
        volley.addQueue(request);

    }

    @Override
    public void setOnCmdCallBack(CommandCallback callback) {
        this.listener = (CallbackListener) callback;
    }

    public interface CallbackListener extends CommandCallback {
        public void callback(int code, Bitmap captch);
    }

    @Override
    public void cancel() {
        request.cancel();
    }

}
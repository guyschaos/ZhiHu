package com.bill.zhihu.api.utils;

import android.util.Log;

/**
 * 日志工具
 * 
 * @author Bill Lv
 *
 */
public class ZhihuLog {

    public static boolean Debug = true;
    /*
     * 由于android的logcat对msg长度的限制（超出的部分将不会被打印出来），各个版本也不一样
     * 基本在4600左右，这里设置了一个略微小一点的值，对msg进行拆分
     */
    private static final int LOG_MSG_MAX_LEN = 4000;

    public static void d(String TAG, Object msg) {
        if (Debug) {
            String logMsg = msg.toString();
            int logLength = logMsg.length();
            if (logLength > LOG_MSG_MAX_LEN) {
                for (int i = 0; i < logLength / LOG_MSG_MAX_LEN; i++) {
                    Log.d(TAG,
                            logMsg.substring(i * LOG_MSG_MAX_LEN, (i + 1)
                                    * LOG_MSG_MAX_LEN));
                }
            } else {
                Log.d(TAG, logMsg);
            }
        }
    }

    public static void dValue(String TAG, String valueName, Object value) {
        d(TAG, valueName + " " + value.toString());
    }
}

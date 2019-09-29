package com.sunday.common.utils;

import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * 日志打印输出
 * Created by Ace on 2016/6/22.
 */
public class YLog {
    private static final String TAG = "YLog";
    private static final String PLAY_TAG = "PlayerLog";

    public static final int VERBOSE = Log.VERBOSE;

    public static final int DEBUG = Log.DEBUG;

    public static final int INFO = Log.INFO;

    public static final int WARN = Log.WARN;

    public static final int ERROR = Log.ERROR;

    private static int level = VERBOSE;

    /**
     * 设置日志级别，只有高于该级别的日志才会被打印，建议在发布relsease版本时候将
     * 日志级别设置为INFO
     *
     * @param lv 日志级别
     */
    public static void setLevel(int lv) {
        level = lv;
    }

    /**
     * 打印常规数据，如网络返回结果等数据，级别最低
     *
     * @param msg
     */
    public static void v(String msg) {
        if (level <= VERBOSE) {
            Log.v(TAG, msg);
        }
    }

    /**
     * 打印调试数据，用于调试
     *
     * @param msg
     */
    public static void d(String msg) {
        if (level <= DEBUG) {
            Log.d(TAG, msg);
        }
    }

    /**
     * 打印消息通知类信息，release版本可查看
     *
     * @param msg
     */
    public static void i(String msg) {
        if (level <= INFO) {
            Log.i(TAG, msg);
        }

    }

    public static void w(String msg) {
        if (level <= WARN) {
            Log.w(TAG, msg);
        }
    }

    public static void e(String msg) {
        if (level <= ERROR) {
            Log.e(TAG, msg);
        }
    }

    public static void e(Throwable t) {
        Log.e(TAG, Log.getStackTraceString(t));
    }

    /**
     * 播放器状态日志
     *
     * @param msg
     */
    public static void p(String msg) {
        Log.i(PLAY_TAG, msg);
    }

}

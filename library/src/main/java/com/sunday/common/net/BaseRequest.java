package com.sunday.common.net;

import android.content.Context;
import okhttp3.Callback;

import java.util.HashMap;
import java.util.List;

/**
 * 请求管理基础类，定义请求的操作行为，以及默认的实现
 * Created by Ace on 2016/6/17.
 */
public class BaseRequest {
    public static final String TAG = "BaseRequest";
    public static final int CODE_SUCCESS = 1;

    /**
     * 发起一个get请求
     *
     * @param context
     * @param url     请求url
     * @param _class  解析对象目标类
     * @param map     请求参数
     * @param handler 用于给client处理请求结果的回调对象
     */
    protected void get(Context context, String url, Class<?> _class, HashMap<String, String> map,
                       CallBackY handler) {
        if (handler == null) {
            handler = new CallBackY(context);
        }
    }

    /**
     * 发起一个get请求，自己处理回调结果
     *
     * @param url    请求url
     * @param map    请求参数
     */
    protected void get(String url, HashMap<String, String> map, Callback callback) {
    }

    /**
     * 发起一个get请求，不需要处理回调结果
     *
     * @param url    请求url
     * @param map    请求参数
     */
    protected void get(String url, HashMap<String, String> map) {
    }

    /**
     * 发起一个post请求
     *
     * @param context
     * @param url     请求url
     * @param _class  解析对象目标类
     * @param map     请求参数
     * @param handler 用于给client处理请求结果的回调对象
     */
    protected void post(Context context, String url, Class<?> _class, HashMap<String, String> map,
                        CallBackY handler) {
        if (handler == null) {
            handler = new CallBackY(context);
        }
    }

    /**
     * 发起一个post请求，处理回调结果
     *
     * @param url    请求url
     * @param map    请求参数
     */
    protected void post(String url, HashMap<String, String> map, Callback callback) {

    }

    /**
     * 发起一个post请求，不需要处理回调结果
     *
     * @param url    请求url
     * @param map    请求参数
     */
    protected void post(String url, HashMap<String, String> map) {

    }

    /**
     * 发起一个带文件上传的post请求
     *
     * @param context
     * @param url      请求url
     * @param _class   解析对象目标类
     * @param map      请求参数
     * @param fileList 请求文件列表
     * @param handler  用于给client处理请求结果的回调对象
     */
    protected void postFile(Context context, String url, Class<?> _class, HashMap<String, String> map,
                            List<YFile> fileList, CallBackY handler) {
        if (handler == null) {
            handler = new CallBackY(context);
        }
    }
}

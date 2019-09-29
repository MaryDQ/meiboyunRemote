package com.sunday.common.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import com.blankj.utilcode.util.NetworkUtils;

/**
 * 描述：网络变化的BroadCast
 * 作者: mlx
 * 创建时间： 2019/7/4
 */
public class NetWorkBroadCast extends BroadcastReceiver {

    public static final int NET_CONNECT = 0;
    public static final int NET_DISCONNECT = 1;

    private Handler mHandler;

    public NetWorkBroadCast(Handler handler) {
        mHandler=handler;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        //判断网络状态
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (NetworkUtils.isWifiConnected()) {
                    mHandler.sendEmptyMessage(NET_CONNECT);
                } else {
                    mHandler.sendEmptyMessage(NET_DISCONNECT);
                }
            }
        }).start();

    }
}

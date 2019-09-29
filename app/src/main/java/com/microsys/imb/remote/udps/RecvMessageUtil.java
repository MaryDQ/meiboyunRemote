package com.microsys.imb.remote.udps;

import android.text.TextUtils;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.microsys.imb.remote.bean.*;
import com.microsys.imb.remote.constants.CheckedCenterConstants;
import com.microsys.imb.remote.constants.ReceivedConstants;
import com.microsys.imb.remote.constants.RequestModel;
import com.microsys.imb.remote.event.*;
import com.microsys.imb.remote.interfaces.BindCenterListener;
import com.microsys.imb.remote.interfaces.CheckNumListener;
import com.microsys.imb.remote.interfaces.RecvCenterListListener;
import com.microsys.imb.remote.utils.ChangeCenterUtils;
import com.sunday.common.logger.Logger;
import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 描述：udp消息处理分发类
 * 作者: mlx
 * 创建时间： 2019/7/5
 */
public class RecvMessageUtil {

    private static List<RecvCenterListListener> mRecvCenterListenerList = new ArrayList<>();
    private static List<BindCenterListener> mBindCenterListenerList = new ArrayList<>();
    private static List<CheckNumListener> mCheckNumListenerList = new ArrayList<>();

    private static final String TYPE_CONNECT = "\"type\":\"connect\"";
    private static final String TYPE_BIND = "\"type\":\"bind\"";
    private static final String TYPE_CHECK = "\"type\":\"check\"";
    private static final String TYPE_GET_APP_INFO = "\"type\":\"get_app_info\"";
    private static final String TYPE_GET_LIVE_INFO = "\"type\":\"get_live_info\"";
    private static final String TYPE_START_LIVE = "\"type\":\"start_live\"";
    private static final String TYPE_STOP_LIVE = "\"type\":\"stop_live\"";
    private static final String TYPE_GET_COMM_STATUS = "\"type\":\"comm_status\"";
    private static final String TYPE_COMM_HANG_UP = "\"type\":\"comm_hangup\"";
    private static final String TYPE_COMM_MUTE = "\"type\":\"comm_mute\"";
    private static final String TYPE_COMM_PTT = "\"type\":\"comm_ptt\"";
    private static final String TYPE_GET_COMM_INFO = "get_comm_info";
//    private static final String TYPE_CONNECT="";

    /**
     * 取到type，然后利用EventBus进行分发
     *
     * @param message 收到的消息
     */
    public static void dealMessageAndSend(String message) {
        Logger.d(message);



        if (message.contains(TYPE_CONNECT)) {
            ConnectResponseBean bean = new Gson().fromJson(message, ConnectResponseBean.class);
            if (Integer.valueOf(bean.getId()) >= 20_000L) {
                doStartHeartBeatThings(bean);
            } else if (Integer.valueOf(bean.getId()) > 9999) {
                doHeartBeatThings(bean);
            } else {
                for (RecvCenterListListener listener :
                        mRecvCenterListenerList) {
                    listener.onCenterListReceived(message);
                }
                //doNothing
            }
        } else if (message.contains(TYPE_BIND)) {
            for (BindCenterListener listener :
                    mBindCenterListenerList) {
                listener.onBindCenterResult(message);
            }
        } else if (message.contains(TYPE_CHECK)) {
            for (CheckNumListener listener :
                    mCheckNumListenerList) {
                listener.onCheckResult(message);
            }
        } else if (message.contains(TYPE_GET_APP_INFO)) {

            TransAppInfoEvent event = new TransAppInfoEvent(message);
            ReceivedConstants.Companion.setTransDeviceListEvent(event);
            EventBus.getDefault().post(event);
        } else if (message.contains(TYPE_GET_LIVE_INFO)) {
            GetLiveInfoRespBean bean = new Gson().fromJson(message, GetLiveInfoRespBean.class);
            ReceivedConstants.Companion.setLiveInfoBean(bean);
            EventBus.getDefault().post(new UpdateMeiboyunLiveInfo(true, bean));
        } else if (message.contains(TYPE_STOP_LIVE) || message.contains(TYPE_START_LIVE)) {
            if (!message.contains("suc")) {
                return;
            }
            EventBus.getDefault().post(new ControlLiveInfoEvent(true, message));
        } else if (message.contains(TYPE_GET_COMM_STATUS)) {
            final GetCommStatusRespBean bean = transferData(message, GetCommStatusRespBean.class);
            if (null != CheckedCenterConstants.Companion.getCurSelectCenter() && bean.getDevice_id().equals(CheckedCenterConstants.Companion.getCurSelectCenter().getDevice_id())) {
                ReceivedConstants.Companion.setCommStatusBean(bean);
                EventBus.getDefault().post(new UpdateCommStatusBeanInfo(true, bean));
            } else {
                //doNothing
            }

        } else if (message.contains(TYPE_COMM_HANG_UP)) {
            if (!message.contains("suc")) {
                return;
            }
            CommHangUpRespBean bean = transferData(message, CommHangUpRespBean.class);
            ReceivedConstants.Companion.setCommHangUpBean(bean);
            EventBus.getDefault().post(new CommHangUpResultEvent(true, bean));
        } else if (message.contains(TYPE_COMM_MUTE)) {
            if (!message.contains("suc")) {
                return;
            }
            CommMuteRespBean bean = transferData(message, CommMuteRespBean.class);
            ReceivedConstants.Companion.setCommMuteBean(bean);
            EventBus.getDefault().post(new CommMuteResultEvent(true, bean));
        } else if (message.contains(TYPE_COMM_PTT)) {
            if (!message.contains("suc")) {
                return;
            }
            CommPttRespBean bean = transferData(message, CommPttRespBean.class);
            ReceivedConstants.Companion.setCommPttBean(bean);
            EventBus.getDefault().post(new CommPttResultEvent(true, bean));
        } else if (message.contains(TYPE_GET_COMM_INFO)) {

            if (!message.contains("suc")) {
                return;
            }
            GetCommInfoRespBean bean = transferData(message, GetCommInfoRespBean.class);
            ReceivedConstants.Companion.setCommInfoBean(bean);
            EventBus.getDefault().post(new CommInfoResultEvent(true, bean));
        } else {
            //doNothing
        }
    }

    /**
     * 判断返回的消息类型，如果是connect，则不作处理
     * 如果是其他类型，再判断本地有无已经选中的center，如果有则判断id是否相同，相同的话不做拦截，不相同的话，拦截
     * 如果是没有选中的center，则不做拦截
     *
     * @param message 收到的消息
     * @return true是需要拦截，false不需要
     */
    private static boolean judgeNeedReturn(String message) {
        String recDeviceId = "";
        try {
            JSONObject jsonObject = new JSONObject(message);
            recDeviceId = jsonObject.getString("device_id");
        } catch (Exception e) {
            e.printStackTrace();
            return false;

        }
        if (TextUtils.isEmpty(recDeviceId)) {
            return false;
        }

        if (message.contains(TYPE_CONNECT)) {
            return false;
        } else {
            return null == CheckedCenterConstants.Companion.getCurSelectCenter() || !recDeviceId.equals(CheckedCenterConstants.Companion.getCurSelectCenter().getDevice_id());
        }
    }

    /**
     * 处理启动时3S返回的心跳所要做的事
     *
     * @param bean 心跳返回的数据bean
     */
    private static void doStartHeartBeatThings(ConnectResponseBean bean) {
        //如果返回来的bean是当前已经选中的bean，则更新
        if (Integer.valueOf(bean.getId()) > 29_000) {
            RequestModel.Companion.setStartHeartRequestId(20_000);
        }
        if (null == CheckedCenterConstants.Companion.getCurSelectCenter()) {
            //则更新剩下的列表当中的bean
            EventBus.getDefault().post(new SetOtherCenterBeanInfoEvent(true, bean));
        } else {
            if (bean.getDevice_id().equals(CheckedCenterConstants.Companion.getCurSelectCenter().getDevice_id())) {
                EventBus.getDefault().post(new DeviceConnectStatusEvent(true));
                ChangeCenterUtils.Companion.changeCurSelectCenter(bean);
            } else {
                //则更新剩下的列表当中的bean
                EventBus.getDefault().post(new SetOtherCenterBeanInfoEvent(true, bean));
            }

        }

    }

    /**
     * 处理心跳返回的数据
     *
     * @param bean 心跳返回的数据bean
     */
    private static void doHeartBeatThings(ConnectResponseBean bean) {
        //如果配对过的云上会面没有被删除，则进行判断，更新对应的云上会面的IP，并且重置心跳包ID
        //如果删除了，则停止
        if (null != CheckedCenterConstants.Companion.getCurSelectCenter() && !"123456".equals(CheckedCenterConstants.Companion.getCurSelectCenter().getDevice_ip())) {
            if (bean.getDevice_id().equals(CheckedCenterConstants.Companion.getCurSelectCenter().getDevice_id())) {
                if (TextUtils.isEmpty(bean.getAccount_name())) {
                    bean.setAccount_name("");
                }

                ChangeCenterUtils.Companion.changeCurSelectCenter(bean);
            } else {
                //doNothing
//                MyApplication.getSInstance().stopService(new Intent(MyApplication.getSInstance(), UdpHeartBeatService.class));
            }
        } else {
            //doNothing
//            MyApplication.getSInstance().stopService(new Intent(MyApplication.getSInstance(), UdpHeartBeatService.class));
        }
    }

    private static <T> T transferData(String message, Class<T> _class) {
        T obj = null;
        try {
            obj = new Gson().fromJson(message, _class);
        } catch (JsonSyntaxException e) {
            Logger.e("json转化出错:" + message);
            e.printStackTrace();
        }
        return obj;
    }

    /**
     * 移除监听获得联络中心列表的监听器
     */
    public static void removeRecvCenterListeners() {
        for (RecvCenterListListener listener :
                mRecvCenterListenerList) {
            listener = null;
        }
        mRecvCenterListenerList.clear();
    }

    /**
     * 移除监听请求绑定回调的监听器
     */
    public static void removeBindCenterListeners() {
        for (BindCenterListener listener :
                mBindCenterListenerList) {
            listener = null;
        }
        mBindCenterListenerList.clear();
    }

    /**
     * 移除监听验证码比对结果的监听
     */
    public static void removeCheckNumListeners() {
        for (CheckNumListener listener :
                mCheckNumListenerList) {
            listener = null;
        }
        mCheckNumListenerList.clear();
    }

    public static void addRecvCenterListener(RecvCenterListListener listener) {
        if (null == listener) {
            return;
        }
        removeRecvCenterListeners();
        mRecvCenterListenerList.add(listener);
    }

    public static void removeAllListeners() {
        removeRecvCenterListeners();
        removeBindCenterListeners();
        removeCheckNumListeners();
    }

    public static void addBindCenterListener(BindCenterListener listener) {
        if (null == listener) {
            return;
        }
        removeBindCenterListeners();
        mBindCenterListenerList.add(listener);
    }

    public static void addCheckNumListener(CheckNumListener listener) {
        if (null == listener) {
            return;
        }
        removeCheckNumListeners();
        mCheckNumListenerList.add(listener);
    }
}

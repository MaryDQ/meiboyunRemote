package com.microsys.imb.remote.udps.socket;

import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import com.blankj.utilcode.util.NetworkUtils;
import com.microsys.imb.remote.constants.RequestModel;
import com.microsys.imb.remote.udps.RecvMessageUtil;
import com.microsys.imb.remote.udps.bean.Users;
import com.microsys.imb.remote.udps.util.DeviceUtil;
import com.microsys.imb.remote.udps.util.WifiUtil;
import com.sunday.common.logger.Logger;

import java.io.IOException;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * Created by melo on 2017/9/20.
 */

public class UDPSocket {

    private static final String TAG = "UDPSocket";
    // 单个CPU线程池大小
    private static final int POOL_SIZE = 5;
    private static final int BUFFER_LENGTH = 1024;
    private static final long TIME_OUT = 120 * 1000;
    private static final long HEARTBEAT_MESSAGE_DURATION = 30 * 1000;
    private static int SERVER_PORT = 10_086;
    private static int RECEIVE_PORT = 6000;
    private static volatile String BROADCAST_IP = "255.255.255.255";
    private static UDPSocket mUDPSocket;
    private byte[] receiveByte = new byte[BUFFER_LENGTH];
    private boolean isThreadRunning = false;
    private Context mContext;
    private DatagramSocket client;
    private DatagramPacket receivePacket;
    private long lastReceiveTime = 0;
    private ExecutorService mThreadPool;
    private Thread clientThread;
    private HeartbeatTimer timer;
    private Users localUser;
    private Users remoteUser;

    private UDPSocket(Context context) {

        this.mContext = context;

        int cpuNumbers = Runtime.getRuntime().availableProcessors();
        // 根据CPU数目初始化线程池
        mThreadPool = Executors.newFixedThreadPool(cpuNumbers * POOL_SIZE);
        // 记录创建对象时的时间
        lastReceiveTime = System.currentTimeMillis();

//        createUser();
    }

    public static void clearUdpSocket() {
        BROADCAST_IP = "";
        SERVER_PORT = 10_086;
    }

    public static int getRECEIVE_PORT() {
        return RECEIVE_PORT;
    }

    public static void setBroadcastIp(String broadcastIp) {
        BROADCAST_IP = broadcastIp;
    }

    public static String getBroadcastIp() {
        return BROADCAST_IP == null ? "" : BROADCAST_IP;
    }

    public static void setServerPort(int serverPort) {
        SERVER_PORT = serverPort;
    }

    public static int getServerPort() {
        return SERVER_PORT;
    }

    public static UDPSocket getmUDPSocket(Context context) {
        if (mUDPSocket == null) {
            if (null != context) {
                mUDPSocket = new UDPSocket(context);
            }
        }
        return mUDPSocket;
    }

    public static void destroyUDPSocket() {
        mUDPSocket = null;
    }

    /**
     * 创建本地用户信息
     */
    private void createUser() {
        if (localUser == null) {
            localUser = new Users();
        }
        if (remoteUser == null) {
            remoteUser = new Users();
        }

        localUser.setImei(DeviceUtil.getDeviceId(mContext));
        localUser.setSoftVersion(DeviceUtil.getPackageVersionCode(mContext));

        if (WifiUtil.getInstance(mContext).isWifiApEnabled()) {// 判断当前是否是开启热点方
            localUser.setIp("192.168.43.1");
        } else {// 当前是开启 wifi 方
            localUser.setIp(WifiUtil.getInstance(mContext).getLocalIPAddress());
            remoteUser.setIp(WifiUtil.getInstance(mContext).getServerIPAddress());
        }
    }


    public void startUDPSocket(int port) {
        if (client != null) {
            return;
        }
        try {
            // 表明这个 Socket 在设置的端口上监听数据。
            client = new DatagramSocket(port);

            if (receivePacket == null) {
                // 创建接受数据的 packet
                receivePacket = new DatagramPacket(receiveByte, BUFFER_LENGTH);
            }

            startSocketThread();
        } catch (SocketException e) {
            e.printStackTrace();
            RECEIVE_PORT += 1;
            startUDPSocket(RECEIVE_PORT);
        }
    }

    /**
     * 开启发送数据的线程
     */
    private void startSocketThread() {
        clientThread = new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "clientThread is running...");
                receiveMessage();
            }
        });
        isThreadRunning = true;
        clientThread.start();

//        startHeartbeatTimer();
    }

    /**
     * 处理接受到的消息
     */
    private void receiveMessage() {
        while (isThreadRunning) {
            try {
                if (client != null) {
                    client.receive(receivePacket);
                }
                lastReceiveTime = System.currentTimeMillis();
                Log.d(TAG, "receive packet success...");
            } catch (IOException e) {
                Log.e(TAG, "UDP数据包接收失败！线程停止");
                stopUDPSocket();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startSocketThread();
                    }
                }, 3000L);
                e.printStackTrace();
                return;
            }

            if (receivePacket == null || receivePacket.getLength() == 0) {
                Log.e(TAG, "无法接收UDP数据或者接收到的UDP数据为空");
                continue;
            }

            String strReceive = new String(receivePacket.getData(), 0, receivePacket.getLength());

            //解析接收到的 json 信息
            if (!TextUtils.isEmpty(strReceive) && strReceive.contains("device_id")) {
                RecvMessageUtil.dealMessageAndSend(strReceive.trim());
            }


            // 每次接收完UDP数据后，重置长度。否则可能会导致下次收到数据包被截断。
            if (receivePacket != null) {
                receivePacket.setLength(BUFFER_LENGTH);
            }
        }
    }

    public void stopUDPSocket() {
        isThreadRunning = false;
        receivePacket = null;
        if (clientThread != null) {
            clientThread.interrupt();
        }
        if (client != null) {
            client.close();
            client = null;
        }
        if (timer != null) {
            timer.exit();
        }
    }

    /**
     * 启动心跳，timer 间隔十秒
     */
    private void startHeartbeatTimer() {
    }


    /**
     * 发送心跳包
     *
     * @param message
     */
    public void sendMessage(final String message) {
        mThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Logger.e(message);

                    InetAddress targetAddress = InetAddress.getByName(BROADCAST_IP);

                    DatagramPacket packet = new DatagramPacket(message.getBytes(), message.length(), targetAddress, SERVER_PORT);

                    if (null != client) {
                        client.send(packet);
                    } else {
                        client = new DatagramSocket(SERVER_PORT);
                        client.send(packet);
                    }


                    // 数据发送事件
                    Log.d(TAG, "数据发送成功");

                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    public void sendMessage(final String message, final String ip, final int port) {
        mThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Logger.e(message);

                    InetAddress targetAddress = InetAddress.getByName(ip);

                    DatagramPacket packet = new DatagramPacket(message.getBytes(), message.length(), targetAddress, port);

                    if (null != client) {
                        client.send(packet);
                    } else {
                        client = new DatagramSocket(RECEIVE_PORT);
                        client.send(packet);
                    }


                    // 数据发送事件
                    Log.d(TAG, "数据发送成功");

                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
    }


    public void sendBroadcastConnectMessage() {
        sendMessage(RequestModel.Companion.getConnectRequestJson(NetworkUtils.getIPAddress(true), String.valueOf(RECEIVE_PORT)), "255.255.255.255", SERVER_PORT);
    }

}

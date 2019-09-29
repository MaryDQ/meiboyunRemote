package com.sunday.common.utils;

public enum  NetInfoEnum {
    /**
     * 网络连接
     */
    NET_CONNECT(0)
    ,
    /**
     * 网络未连接
     */
    NET_DISCONNECT(1);

    private int value;

    NetInfoEnum(int value) {
        this.value=value;
    }


    public int getValue() {
        return value;
    }
}

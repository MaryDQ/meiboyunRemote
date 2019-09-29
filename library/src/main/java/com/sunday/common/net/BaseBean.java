package com.sunday.common.net;

/**
 * model基类,数据对象需要继承此类
 * Created by Ace on 2016/6/16.
 */
public class BaseBean {
    /**
     * 请求结果状态码
     * 1：成功
     * 0：失败
     */
    protected int rt;
    /**
     * 成功或错误描述
     */
    protected String des;

    public int getRt() {
        return rt;
    }

    public void setRt(int rt) {
        this.rt = rt;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }
}

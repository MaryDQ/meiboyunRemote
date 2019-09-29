package com.sunday.common.model;

import java.io.Serializable;

/**
 * Created by ${张科} on 2016/1/23.
 */
public class MobiOrderResults implements Serializable{
    private Long id;//订单id
    private int expertId;//专家(医生)id
    private Long memberId;//用户id
    private String expertName;//专家名称
    private String memberName;//用户名称
    private String orderNo;//编号
    private String method;//预约方式
    private String time;//具体时间段
    private String address;//地址
    private Double totalFee;//总价
    private Double earnest;
    private Double finalPayment;
    private Integer status;//状态 0待付款 1待咨询 2待评价 3完成
    private Integer count;//文字预约条数
    private int isPayment;//是否支付 0 为未支付  1 为支付
    private String subAccountSid;
    private String subToken;
    private String voipAccount;//医生视频通话账号
    private String voipPwd;
    private int isReset;
    private int airClinicId;
    private boolean allowCall;//是否允许通话

    public boolean isAllowCall() {
        return allowCall;
    }

    public void setAllowCall(boolean allowCall) {
        this.allowCall = allowCall;
    }

    public MobiOrderResults(int expertId) {
        this.expertId = expertId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }



    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public String getExpertName() {
        return expertName;
    }

    public void setExpertName(String expertName) {
        this.expertName = expertName;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Double getTotalFee() {
        return totalFee;
    }

    public void setTotalFee(Double totalFee) {
        this.totalFee = totalFee;
    }

    public Double getEarnest() {
        return earnest;
    }

    public void setEarnest(Double earnest) {
        this.earnest = earnest;
    }

    public Double getFinalPayment() {
        return finalPayment;
    }

    public void setFinalPayment(Double finalPayment) {
        this.finalPayment = finalPayment;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public int getExpertId() {
        return expertId;
    }

    public void setExpertId(int expertId) {
        this.expertId = expertId;
    }

    public String getVoipAccount() {
        return voipAccount;
    }

    public void setVoipAccount(String voipAccount) {
        this.voipAccount = voipAccount;
    }

    public int getIsPayment() {
        return isPayment;
    }

    public void setIsPayment(int isPayment) {
        this.isPayment = isPayment;
    }

    public String getSubAccountSid() {
        return subAccountSid;
    }

    public void setSubAccountSid(String subAccountSid) {
        this.subAccountSid = subAccountSid;
    }

    public String getSubToken() {
        return subToken;
    }

    public void setSubToken(String subToken) {
        this.subToken = subToken;
    }

    public String getVoipPwd() {
        return voipPwd;
    }

    public void setVoipPwd(String voipPwd) {
        this.voipPwd = voipPwd;
    }

    public int getIsReset() {
        return isReset;
    }

    public void setIsReset(int isReset) {
        this.isReset = isReset;
    }

    public int getAirClinicId() {
        return airClinicId;
    }

    public void setAirClinicId(int airClinicId) {
        this.airClinicId = airClinicId;
    }
}

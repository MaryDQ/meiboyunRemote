package com.microsys.imb.remote.bean

data class CommCallOutRespBean(
    /**
     * call_type 呼叫类型（0：语音通话 1：视频通话 2：语音会议 3：视频会议）
     */
    val call_type: String,
    val device_id: String,
    val device_ip: String,
    val device_port: String,
    val id: String,
    val reason: String,
    val result: String,
    val type: String
)
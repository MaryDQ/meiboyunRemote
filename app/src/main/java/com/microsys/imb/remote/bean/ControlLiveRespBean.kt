package com.microsys.imb.remote.bean

data class ControlLiveRespBean(
    val device_id: String,
    val device_ip: String,
    val device_port: String,
    val id: String,
    /**
     * live_status 直播状态（1：正在直播 0 ：预约直播 -1：结束直播）
     */
    val live_status: String,
    /**
     * play_url  可播放地址流
     */
    val play_url: String,
    val reason: String,
    val result: String,
    val type: String
)
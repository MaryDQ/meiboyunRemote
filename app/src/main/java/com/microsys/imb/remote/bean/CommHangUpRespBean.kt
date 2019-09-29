package com.microsys.imb.remote.bean

data class CommHangUpRespBean(
    val device_id: String,
    val device_ip: String,
    val device_port: String,
    val id: String,
    val reason: String,
    val result: String,
    val type: String
)
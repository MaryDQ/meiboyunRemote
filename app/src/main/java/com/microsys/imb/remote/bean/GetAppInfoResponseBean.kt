package com.microsys.imb.remote.bean

data class GetAppInfoResponseBean(
	val device_id: String,
	val device_ip: String,
	val device_port: String,
	val id: String,
	val info: Info,
	val reason: String,
	val result: String,
	val type: String,
	val account_name: String,
	val account_num: String

)

data class Info(
    val communication: Communication,
    val gis: Gis,
    val live: Live
)

data class Communication(
    val isExist: String,
    val isOnline: String
)

data class Live(
    val isExist: String,
    val isOnline: String
)

data class Gis(
    val isExist: String,
    val isOnline: String
)
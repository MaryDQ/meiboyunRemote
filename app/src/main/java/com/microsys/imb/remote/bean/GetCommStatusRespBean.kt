package com.microsys.imb.remote.bean


data class GetCommStatusRespBean(
	val comm_direct: CommDirect,
	val comm_mute_status: String,
	val comm_ptt_status: String,
	var comm_time: String,
	val comm_status: String,
	val device_id: String,
	val device_ip: String,
	val device_port: String,
	val id: String,
	val reason: String,
	val result: String,
	val type: String,
	val call_type: String
)



package com.microsys.imb.remote.bean

data class GetCommInfoRespBean(
	val addr_info: AddrInfo,
	/**
	 * comm_direc 主被叫 （call_in 后面跟的是呼入号码，该号码是被叫  call_out 后面跟的是呼出号码，该号码是主叫）
	 */
	val comm_direct: CommDirect,
	/**
	 * comm_status 通话状态  （2：通话中 1：呼出等待中 0 ：呼入震铃中 -1：结束通话 ）
	 */
	val comm_status: String,
	/**
	 * comm_time 已经建立呼叫的时间 单位s秒
	 */
	val comm_time: String,
	val device_id: String,
	val device_ip: String,
	val device_port: String,
	val id: String,
	val reason: String,
	val result: String,
	val type: String,
	val comm_name: String,
	val comm_tel: String
)

data class AddrInfo(
	val group_info: List<GroupInfo>,
	val local_user: List<LocalUser>,
	val organization_info: List<OrganizationInfo>,
	val user_info: List<UserInfo>
)

data class GroupInfo(
	val bc_tel: String,
	val did: String,
	val gid: String,
	val group_type: String,
	val name: String,
	val user_list: String,
	val vgcs_tel: String
)

data class OrganizationInfo(
	val did: String,
	val name: String,
	val oid: String,
	val parent_oid: String,
	val user_list: String
)

data class LocalUser(
	val did: String,
	val name: String,
	val st: String,
	val tel: String,
	val uid: String
)

data class UserInfo(
	val did: String,
	val name: String,
	val st: String,
	val tel: String,
	val uid: String
)

data class CommDirect(
	val call_in: List<String>,
	val call_out: List<String>
)

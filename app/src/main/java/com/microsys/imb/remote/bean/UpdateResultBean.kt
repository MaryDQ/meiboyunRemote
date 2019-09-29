package com.microsys.imb.remote.bean

data class UpdateResultBean(
	val `data`: Data,
	val code: Int,
	val msg: Any
)

data class Data(
	val appName: String,
	val content: String,
	val createTime: String,
	val fileKey: String,
	val id: String,
	val isDelete: Int,
	val status: Int,
	val type: String,
	val updateTime: String,
	val url: String,
	val version: String
)
package com.microsys.imb.remote.bean

import java.math.BigInteger

data class ConnectResponseBean(
	var device_id: String,
	var device_ip: String = "",
	var device_port: String,
	var id: String = "0",
	var type: String,
	var account_name: String
) {
	override fun hashCode(): Int {
		var hashCode = transferStringToInt(this.device_id)
		return hashCode.toInt()
	}

	override fun equals(other: Any?): Boolean {
		if (other is ConnectResponseBean) {
			if (this.device_id == other.device_id) {
				return true
			}
			return false
		} else {
			return false
		}
	}

	fun transferStringToInt(content: String): BigInteger {
		var id = ""
		var charArray = content.toCharArray()
		for (c in charArray) {
			id += c.toInt()
		}

		return id.toBigInteger()
	}
}
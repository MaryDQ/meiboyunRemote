package com.microsys.imb.remote.constants

class HttpConstants {
	companion object {
		val baseUrl = "https://ai.imbcloud.cn/"

		fun getCheckVersionUrl(): String = baseUrl + "access/user/login/checkVersions"
	}
}
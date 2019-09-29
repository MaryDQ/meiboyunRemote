package com.microsys.imb.remote.constants

import com.microsys.imb.remote.bean.ConnectResponseBean

class CheckedCenterConstants {
	companion object {
		var checkedSetList = hashSetOf<ConnectResponseBean>()
		var checkedArrayList = mutableListOf<ConnectResponseBean>()
		var checkedMapList = hashMapOf<String, ConnectResponseBean>()
		var curSelectCenter: ConnectResponseBean? = null
		var curAccountNum: String? = ""
	}
}
package com.microsys.imb.remote.utils

import android.text.TextUtils
import com.google.gson.Gson
import com.microsys.imb.remote.bean.ConnectResponseBean
import com.microsys.imb.remote.constants.CheckedCenterConstants
import com.microsys.imb.remote.constants.RequestModel
import com.microsys.imb.remote.event.StartToGetAppInfoEvent
import com.microsys.imb.remote.udps.socket.UDPSocket
import com.microsys.imb.remote.ui.fragment.CHECKED_CENTER_LIST
import com.microsys.imb.remote.widgets.dialog.CUR_SELECT_CENTER
import com.sunday.common.utils.SharedPerencesUtil
import org.greenrobot.eventbus.EventBus

class ChangeCenterUtils {
	companion object {
		fun changeCurSelectCenter(bean: ConnectResponseBean) {
			CheckedCenterConstants.curSelectCenter!!.device_ip = bean.device_ip
			if (TextUtils.isEmpty(bean.account_name)) {
				CheckedCenterConstants.curSelectCenter!!.account_name = ""
			} else {
				CheckedCenterConstants.curSelectCenter!!.account_name = bean.account_name
			}
			CheckedCenterConstants.checkedMapList[bean.device_id] = bean
			CheckedCenterConstants.curAccountNum = ""
			UDPSocket.setBroadcastIp(bean.device_ip)
			EventBus.getDefault().post(StartToGetAppInfoEvent(true))
			RequestModel.heartRequestId = 10000
		}

		fun saveCenterInfo() {
			val list = ArrayList<ConnectResponseBean>()
			for (item in CheckedCenterConstants.checkedArrayList) {
				val bean: ConnectResponseBean =
					ConnectResponseBean(item.device_id, "", item.device_port, "", item.type, "")
				list.add(bean)
			}

			var tempCurSelectBean: ConnectResponseBean? = null
			if (null != CheckedCenterConstants.curSelectCenter) {
				tempCurSelectBean = ConnectResponseBean(
					CheckedCenterConstants.curSelectCenter!!.device_id,
					"",
					CheckedCenterConstants.curSelectCenter!!.device_port,
					"",
					CheckedCenterConstants.curSelectCenter!!.type,
					""
				)
			} else {
				//doNothing
			}
			SharedPerencesUtil.putString(CHECKED_CENTER_LIST, Gson().toJson(list))
			SharedPerencesUtil.putString(CUR_SELECT_CENTER, Gson().toJson(tempCurSelectBean))
		}
	}
}
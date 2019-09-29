package com.microsys.imb.remote.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.blankj.utilcode.util.NetworkUtils
import com.microsys.imb.remote.app.MyApplication
import com.microsys.imb.remote.constants.CheckedCenterConstants
import com.microsys.imb.remote.constants.EnterSystemTimeConstants
import com.microsys.imb.remote.constants.RequestModel
import com.microsys.imb.remote.constants.StartHeartServiceConstants
import com.microsys.imb.remote.event.DeviceConnectStatusEvent
import com.microsys.imb.remote.udps.socket.UDPSocket
import org.greenrobot.eventbus.EventBus
import java.util.*

class StartHeartService : Service() {
	override fun onBind(intent: Intent?): IBinder {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}


	var timer: Timer? = Timer()


	override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

		timer?.scheduleAtFixedRate(object : TimerTask() {
			override fun run() {
				if (StartHeartServiceConstants.stopService) {
					return
				}
				StartHeartServiceConstants.isRunning = true
				if (System.currentTimeMillis() - EnterSystemTimeConstants.enterHomeTime!! >= 9_000L) {
					if (null != CheckedCenterConstants.curSelectCenter && CheckedCenterConstants.curSelectCenter!!.device_id.isNotEmpty() && CheckedCenterConstants.curSelectCenter!!.device_ip.isEmpty()) {

						EventBus.getDefault().post(DeviceConnectStatusEvent(false))
					} else {
						//doNothing
					}
				} else {
					//doNothing
				}
				UDPSocket.getmUDPSocket(MyApplication.sInstance)
					.sendMessage(
						RequestModel.getStartHeartConnectRequestJson(
							NetworkUtils.getIPAddress(true),
							UDPSocket.getRECEIVE_PORT().toString()
						),
						"255.255.255.255",
						UDPSocket.getServerPort()
					)
			}
		}, 1_000L, 3_000L)

		return super.onStartCommand(intent, flags, startId)
	}

	override fun onDestroy() {
		timer?.cancel()
		timer = null
		super.onDestroy()
	}

}
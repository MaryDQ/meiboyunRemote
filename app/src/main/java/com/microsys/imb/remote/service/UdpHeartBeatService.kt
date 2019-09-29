package com.microsys.imb.remote.service

import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import com.blankj.utilcode.util.NetworkUtils
import com.microsys.imb.remote.app.MyApplication
import com.microsys.imb.remote.constants.RequestModel
import com.microsys.imb.remote.event.DeviceConnectStatusEvent
import com.microsys.imb.remote.udps.socket.UDPSocket
import com.sunday.common.logger.Logger
import org.greenrobot.eventbus.EventBus

class UdpHeartBeatService : Service() {

	private var mHandler: Handler = Handler(Looper.myLooper())


	override fun onBind(intent: Intent): IBinder {
		TODO("Return the communication channel to the service.")
	}


	var runnable: Runnable? = null

	private var heartBeatPeriod = 10_000L




	override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {


		runnable = Runnable {
			Logger.w("heartHeartHeartHeartHeartHeartHeartHeartHeartHeartHeart")

			if (RequestModel.heartRequestId >= 10_003) {
				//已经失败了
				heartBeatPeriod = 3000L
				EventBus.getDefault().post(DeviceConnectStatusEvent(false))
			} else {
				//doNothing
				heartBeatPeriod = 10_000L
				EventBus.getDefault().post(DeviceConnectStatusEvent(true))
			}

			UDPSocket.getmUDPSocket(MyApplication.sInstance)
				.sendMessage(
					RequestModel.getHeartConnectRequestJson(
						NetworkUtils.getIPAddress(true),
						UDPSocket.getRECEIVE_PORT().toString()
					),
					"255.255.255.255",
					UDPSocket.getServerPort()
				)

			mHandler.postDelayed(runnable, heartBeatPeriod)
		}

		mHandler.postDelayed(runnable, heartBeatPeriod)


		return super.onStartCommand(intent, flags, startId)
	}
}
package com.microsys.imb.remote.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.blankj.utilcode.util.NetworkUtils
import com.microsys.imb.remote.R
import com.microsys.imb.remote.constants.DialogConstants
import com.microsys.imb.remote.constants.EnterSystemTimeConstants
import com.microsys.imb.remote.event.BackPressedEvent
import com.microsys.imb.remote.event.DismissCurDialogEvent
import com.microsys.imb.remote.udps.RecvMessageUtil
import com.microsys.imb.remote.udps.socket.UDPSocket
import com.microsys.imb.remote.ui.fragment.HomeFragment
import com.microsys.imb.remote.ui.fragment.SelectFromPersonsFragment
import com.sunday.common.utils.ToastUtils
import org.greenrobot.eventbus.EventBus

class MainActivity : AppCompatActivity() {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)

		initData()
	}


	/**
	 * 加载一些数据
	 */
	private fun initData() {
		EnterSystemTimeConstants.enterHomeTime = System.currentTimeMillis()
		if (!NetworkUtils.isWifiConnected()) {
			ToastUtils.show("请检查是否连接了同一网络")
		}
		UDPSocket.getmUDPSocket(this).startUDPSocket(UDPSocket.getRECEIVE_PORT())
	}

	override fun onDestroy() {
		super.onDestroy()
		RecvMessageUtil.removeAllListeners()
		UDPSocket.destroyUDPSocket()
	}

	override fun onBackPressed() {
		when {
			SelectFromPersonsFragment.isAlive -> EventBus.getDefault().post(BackPressedEvent(true))

			HomeFragment.isAlive -> {
				if (!DialogConstants.isDialogShowing) {
					toHomeLauncher()
				} else {
					EventBus.getDefault().post(DismissCurDialogEvent(dismiss = true))
				}
			}

			else -> super.onBackPressed()
		}
	}

	/**
	 * 模拟按下系统的Home键
	 */
	private fun toHomeLauncher() {
		val home = Intent(Intent.ACTION_MAIN)
		home.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
		home.addCategory(Intent.CATEGORY_HOME)
		startActivity(home)
	}
}

package com.microsys.imb.remote.widgets.dialog

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.lxj.xpopup.core.CenterPopupView
import com.microsys.imb.remote.R
import com.microsys.imb.remote.bean.BindResponseBean
import com.microsys.imb.remote.bean.CheckResponseBean
import com.microsys.imb.remote.bean.ConnectResponseBean
import com.microsys.imb.remote.constants.CheckedCenterConstants
import com.microsys.imb.remote.constants.CheckedCenterConstants.Companion.curSelectCenter
import com.microsys.imb.remote.constants.DialogConstants
import com.microsys.imb.remote.constants.RequestModel
import com.microsys.imb.remote.event.StartToGetAppInfoEvent
import com.microsys.imb.remote.interfaces.BindCenterListener
import com.microsys.imb.remote.interfaces.CheckNumListener
import com.microsys.imb.remote.interfaces.RecvCenterListListener
import com.microsys.imb.remote.udps.RecvMessageUtil
import com.microsys.imb.remote.udps.socket.UDPSocket
import com.microsys.imb.remote.ui.adapter.recycler.AbstractSimpleAdapter
import com.microsys.imb.remote.ui.adapter.recycler.ViewHolder
import com.sunday.common.utils.InputMethodUtils
import com.sunday.common.utils.SharedPerencesUtil
import com.sunday.common.utils.ToastUtils
import com.sunday.common.widgets.IPEditText
import com.yuyashuai.frameanimation.FrameAnimation
import kotlinx.android.synthetic.main.dialog_search_device.view.*
import org.greenrobot.eventbus.EventBus

const val CUR_SELECT_CENTER = "CUR_SELECT_CENTER"

class SearchDeviceDialogView(context: Context) : CenterPopupView(context), RecvCenterListListener, BindCenterListener,
	CheckNumListener {
	/**
	 * 验证码比对结果
	 */
	override fun onCheckResult(json: String) {
		try {
			val checkNumBean = Gson().fromJson<CheckResponseBean>(json, CheckResponseBean::class.java)

			if (checkNumBean.device_id != CheckedCenterConstants.curSelectCenter?.device_id) {
				return
			} else {
				var added = false
				for (item in CheckedCenterConstants.checkedSetList) {
					if (item.device_id == checkNumBean.device_id) {
						added = true
						break
					}
				}
				if (added) {
					Handler(Looper.getMainLooper()).post { ToastUtils.show("该云上会面已添加") }
					return
				} else {
					//doNothing
				}
				//doNothing
			}

			(context as Activity).runOnUiThread {
				if (checkNumBean.result.contains("suc")) {

					if (null != CheckedCenterConstants.curSelectCenter && CheckedCenterConstants.curSelectCenter!!.device_id == checkNumBean.device_id) {
						CheckedCenterConstants.curSelectCenter?.device_ip = checkNumBean.device_ip
						CheckedCenterConstants.checkedArrayList.add(curSelectCenter!!)
						CheckedCenterConstants.checkedMapList.put(curSelectCenter!!.device_id, curSelectCenter!!)
					}
					UDPSocket.setBroadcastIp(checkNumBean.device_ip)
					UDPSocket.setServerPort(checkNumBean.device_port.toInt())
					EventBus.getDefault().post(StartToGetAppInfoEvent(true))
					dismiss()
				} else {
					findViewById<View>(R.id.tvCompareFailed).visibility = View.VISIBLE
				}
			}
		} catch (e: Exception) {
			e.printStackTrace()
		}

	}

	/**
	 * 请求绑定回调，开启验证码比较
	 */
	override fun onBindCenterResult(json: String) {
		val bindCenterBean = Gson().fromJson<BindResponseBean>(json, BindResponseBean::class.java)
		curTempSelectBean = Gson().fromJson<BindResponseBean>(json, BindResponseBean::class.java)
		Handler(Looper.getMainLooper()).post {
			curIndex = 2
			findViewById<View>(R.id.tvBack).visibility = View.VISIBLE
			ctlSearching?.visibility = View.GONE
			ctlRcvDeviceList?.visibility = View.GONE
			rcvDeviceList?.visibility = View.GONE
			ctlDeviceDetail?.visibility = View.VISIBLE
			ipEditText?.requestFocus()
			InputMethodUtils.openSoftKeyboard(context, ipEditText?.getmEtPwd()!!)
		}

	}

	var curTempSelectBean: BindResponseBean? = null


	/**
	 * 搜索到可用的联络中心列表，并且展示
	 */
	override fun onCenterListReceived(json: String) {
		val bean: ConnectResponseBean? = Gson().fromJson<ConnectResponseBean>(json, ConnectResponseBean::class.java)
		if (null != bean?.device_ip && bean.device_ip != "255.255.255.255") {

			for (item in adapterDeviceList?.getmDatas()!!) {
				val tempBean = item as ConnectResponseBean
				if (bean.device_id == tempBean.device_id) {
					return
				}
			}
			(context as Activity).runOnUiThread {
				adapterDeviceList?.addData(0, bean)
				animListener?.onAnimationEnd()

			}
		}
	}

	override fun getImplLayoutId(): Int {
		return R.layout.dialog_search_device
	}


	override fun onCreate() {
		super.onCreate()
		initData()
		RecvMessageUtil.addRecvCenterListener(this)
		RecvMessageUtil.addBindCenterListener(this)
		RecvMessageUtil.addCheckNumListener(this)
	}

	override fun onDismiss() {
		InputMethodUtils.hide(context)
		RecvMessageUtil.removeAllListeners()
		DialogConstants.isDialogShowing = false
		super.onDismiss()
	}

	private var curSelectDeviceIndex = 0

	private var adapterDeviceList: AbstractSimpleAdapter<ConnectResponseBean>? = null
	private var animListener: FrameAnimation.FrameAnimationListener? = null
	var curIndex = 0

	private var rcvDeviceList: RecyclerView? = null
	var ctlSearching: View? = null
	var ctlRcvDeviceList: View? = null
	private var ctlDeviceDetail: View? = null
	private var ipEditText: IPEditText? = null
	private var btnCancel: Button? = null
	private var tvDeviceDetailName: TextView? = null


	private fun initData() {

		rcvDeviceList = findViewById(R.id.rcvDeviceList)

		ctlSearching = findViewById(R.id.ctlSearching)

		ctlRcvDeviceList = findViewById(R.id.ctlDeviceList)

		ctlDeviceDetail = findViewById(R.id.ctlDeviceDetail)

		btnCancel = findViewById(R.id.btnCancel)

		tvDeviceDetailName = findViewById(R.id.tvDeviceDetailName)

		DialogConstants.isDialogShowing = true

		Handler().postDelayed({
			UDPSocket.getmUDPSocket(context).sendBroadcastConnectMessage()
		}, 800L)

		animListener = object : FrameAnimation.FrameAnimationListener {
			override fun onAnimationEnd() {
				Handler(Looper.getMainLooper()).post {
					curIndex = 1
					ctlSearching?.visibility = View.GONE
					ctlRcvDeviceList?.visibility = View.VISIBLE
					rcvDeviceList?.visibility = View.VISIBLE
				}
			}

			override fun onAnimationStart() {
				TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
			}

			override fun onProgress(progress: Float, frameIndex: Int, totalFrames: Int) {
				TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
			}

		}

		val deviceList = ArrayList<ConnectResponseBean>()

		adapterDeviceList =
			object : AbstractSimpleAdapter<ConnectResponseBean>(context, deviceList, R.layout.item_device_info) {
				override fun onBindViewHolder(holder: ViewHolder?, data: ConnectResponseBean?, curPosition: Int) {

					holder?.getView<ImageView>(R.id.ivDeviceIcon)?.apply {
						when (data?.device_id) {
							"联络中心01" -> {
								setImageResource(R.mipmap.icon_far_phone_large)
							}
							"联络中心02" -> {
								setImageResource(R.mipmap.icon_far_phone_large)
							}
							"联络中心03" -> {
								setImageResource(R.mipmap.icon_far_phone_large)
							}
						}
					}

					holder?.getView<TextView>(R.id.tvDeviceName)?.text = data?.account_name
				}
			}


		ipEditText = findViewById(R.id.ipEditText)

		adapterDeviceList?.setOnItemClickListener { o, position, _ ->
			val tempData = o as ConnectResponseBean
			val data = ConnectResponseBean(
				tempData.device_id,
				tempData.device_ip,
				tempData.device_port,
				tempData.id,
				tempData.type,
				tempData.account_name
			)
			curSelectDeviceIndex = position

			SharedPerencesUtil.putString(CUR_SELECT_CENTER, Gson().toJson(data))
			tvDeviceDetailName!!.text = data.account_name
			UDPSocket.getmUDPSocket(context)
				.sendMessage(RequestModel.getBindRequestJson(), data.device_ip, data.device_port.toInt())
			CheckedCenterConstants.curSelectCenter = data
			CheckedCenterConstants.curSelectCenter!!.device_ip = "123456"
		}

		rcvDeviceList?.layoutManager = object : LinearLayoutManager(context) {}
		rcvDeviceList?.adapter = adapterDeviceList

		findViewById<View>(R.id.ivClose).setOnClickListener {
			dismiss()
		}

		findViewById<View>(R.id.tvBack).setOnClickListener {
			when (curIndex) {
				2 -> {
					curIndex = 1
					ctlSearching?.visibility = View.GONE
					ctlRcvDeviceList?.visibility = View.VISIBLE
					rcvDeviceList?.visibility = View.VISIBLE
					ctlDeviceDetail?.visibility = View.GONE
					findViewById<View>(R.id.tvBack).visibility = View.GONE
					InputMethodUtils.hide(context)
					ipEditText?.setTextClear()
				}
			}
		}

		//输入密码进行匹配
		ipEditText?.setIpEditTextListener {
			if (null != curTempSelectBean) {
				UDPSocket.getmUDPSocket(context).sendMessage(
					RequestModel.getCheckRequestJson(
						ipEditText!!.text
					), curTempSelectBean!!.device_ip
					, curTempSelectBean!!.device_port.toInt()
				)
			}

//			Handler(Looper.getMainLooper()).post {
//				ToastUtils.show("目的地IP：${curTempSelectBean!!.device_ip},目的地port:${curTempSelectBean!!.device_port!!.toInt()}")
//			}
		}

		btnCancel?.setOnClickListener {
			val curTag = btnCancel?.tag
			if ("cancel" == curTag) {
				favSearchDeviceLoading.stopAnimation()
				ivSearchDeviceLoading.setBackgroundResource(R.mipmap.icon_search_device04)
				ivSearchDeviceLoading.visibility = View.VISIBLE
				btnCancel?.tag = "clickToStart"
				btnCancel?.text = "wifi已经连上了"
				tvSearchingTip.text = "请确定连上同一WIFI路由器"
			} else if ("clickToStart" == curTag) {
				ivSearchDeviceLoading.visibility = View.GONE
				btnCancel?.tag = "cancel"
				playAnimation()
				btnCancel?.text = "取消"
				tvSearchingTip.text = "正在搜索中..."
				UDPSocket.getmUDPSocket(context).sendBroadcastConnectMessage()
			}
		}
		initFrameAnimationView()
	}

	private fun initFrameAnimationView() {
		favSearchDeviceLoading.setFrameInterval(166)
		favSearchDeviceLoading.setRepeatMode(FrameAnimation.RepeatMode.REVERSE_INFINITE)
		favSearchDeviceLoading.setScaleType(FrameAnimation.ScaleType.CENTER)
		playAnimation()
	}

	private fun playAnimation() {
		favSearchDeviceLoading.playAnimationFromAssets("searchDevice")
	}


}
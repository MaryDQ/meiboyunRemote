package com.microsys.imb.remote.ui.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.microsys.imb.remote.R
import com.microsys.imb.remote.app.MyApplication
import com.microsys.imb.remote.base.BaseNavigationFragment
import com.microsys.imb.remote.bean.GetCommStatusRespBean
import com.microsys.imb.remote.constants.CheckedCenterConstants
import com.microsys.imb.remote.constants.ReceivedConstants
import com.microsys.imb.remote.constants.RequestModel
import com.microsys.imb.remote.event.*
import com.microsys.imb.remote.udps.socket.UDPSocket
import com.microsys.imb.remote.ui.activity.MainActivity
import com.microsys.imb.remote.ui.adapter.recycler.AbstractSimpleAdapter
import com.microsys.imb.remote.ui.adapter.recycler.ViewHolder
import com.microsys.imb.remote.utils.DialogUtils
import com.sunday.common.utils.StatusBarUtils
import com.sunday.common.utils.ToastUtils
import kotlinx.android.synthetic.main.fragment_far_phone.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList


/**
 *通话类型，0：语音通话 1：视频通话 2：语音会议 3：视频会议
 */

const val TYPE_AUDIO_SINGLE_AUDIO_CALL = "0"
const val TYPE_AUDIO_SINGLE_VIDEO_CALL = "1"
const val TYPE_AUDIO_MEETING_AUDIO_CALL = "2"
const val TYPE_AUDIO_MEETING_VIDEO_CALL = "3"

/**
 *通话状态，2：通话中 1：呼出等待中 0 ：呼入震铃中 -1：结束通话
 */
const val COMM_STATUS_CALL_PLAYING = "2"
const val COMM_STATUS_CALL_OUT_WAITING = "1"
const val COMM_STATUS_CALL_IN_WAITING = "0"
const val COMM_STATUS_CALL_OVER = "-1"


/**
 * 远程会议界面
 * @author mlx
 * @date 2019.07.08
 */
class FarPhoneFragment : BaseNavigationFragment() {

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_far_phone, container, false)
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		initData()
	}

	private fun initData() {
		StatusBarUtils.initSystemBarColor(this.activity as Activity?, false, Color.WHITE)
		tvProductName.text = "远程会议"
		ivProductPic.setImageResource(R.mipmap.icon_far_phone_large)
		btnBottomFunc.text = "发起新呼叫"
		UDPSocket.getmUDPSocket(context).sendMessage(RequestModel.getCommInfoRequestJson())


		initProductInfo()

		initRcvPhoneDetail()
		initClick()
		initTimer()
		notifyAdapterAndJudgeShowNoCall()
	}

	/**
	 * 刷新通话状态列表，并且判断当前有无通话，如果有，则隐藏提示，反之显示提示
	 */
	private fun notifyAdapterAndJudgeShowNoCall() {
		phoneDetailAdapter?.notifyDataSetChanged()
		if (phoneDetailAdapter?.itemCount == 0 || null == phoneDetailAdapter?.itemCount) {
			if (null == tvNoMoreInfo) {
				return
			}
			tvNoMoreInfo.visibility = View.VISIBLE
		} else {
			if (null == tvNoMoreInfo) {
				return
			}
			tvNoMoreInfo.visibility = View.GONE
		}
	}

	/**
	 * drawable离线和在线的图片
	 */
	var drawableStatusOnline: Drawable? = null
	var drawableStatusOffLine: Drawable? = null
	/**
	 * 加载远程会议的基本信息
	 */
	private fun initProductInfo() {
		tvProductNickName.text = CheckedCenterConstants.curSelectCenter?.account_name
		tvOnlineNumber.text = CheckedCenterConstants.curAccountNum

		drawableStatusOnline = ContextCompat.getDrawable(MyApplication.sInstance, R.mipmap.icon_point_online)
		drawableStatusOnline?.setBounds(0, 0, 12, 12)

		drawableStatusOffLine = ContextCompat.getDrawable(MyApplication.sInstance, R.mipmap.icon_point_offline)
		drawableStatusOffLine?.setBounds(0, 0, 12, 12)

		tvStatus.text = "离线"
		tvStatus.setTextColor(Color.parseColor("#BA3535"))
		tvStatus.setCompoundDrawables(drawableStatusOffLine, null, null, null)

	}

	/**
	 * scheduledExecutorService，定时器对象
	 */
	private var scheduledExecutorService: ScheduledExecutorService? = Executors.newScheduledThreadPool(2)

	/**
	 * 定时器相关操作
	 */
	private fun initTimer() {
		scheduledExecutorService?.scheduleAtFixedRate({
			if (phoneDetailList.size > 0) {
				phoneDetailList[0].comm_time = (phoneDetailList[0].comm_time.toInt() + 1).toString()
				Handler(Looper.getMainLooper()).post {
					notifyAdapterAndJudgeShowNoCall()
				}
			}
		}, 0, 1L, TimeUnit.SECONDS)
	}


	override fun onDestroyView() {
		super.onDestroyView()
		scheduledExecutorService?.shutdown()
	}


	override fun onStart() {
		super.onStart()
		if (!EventBus.getDefault().isRegistered(this)) {
			EventBus.getDefault().register(this)
		}
	}

	override fun onStop() {
		super.onStop()
		if (EventBus.getDefault().isRegistered(this)) {
			EventBus.getDefault().unregister(this)
		}
	}

	/**
	 * 轮询获取通话状态的回调
	 */
	@Subscribe(threadMode = ThreadMode.MAIN)
	fun updatePhoneDetail(event: UpdateCommStatusBeanInfo) {
		if (event.eventBean.device_id != CheckedCenterConstants.curSelectCenter?.device_id || event.eventBean.comm_status == COMM_STATUS_CALL_OVER) {
			phoneDetailList.clear()
			notifyAdapterAndJudgeShowNoCall()
			thereNoCall()
			return
		}

		if (event.update) {
			//判断当前通话状态是否结束
			if (event.eventBean.result == "fail") {
				phoneDetailList.clear()
				notifyAdapterAndJudgeShowNoCall()
				thereNoCall()
			} else {
				initRcvPhoneDetail()
			}
		} else {
			//doNothing
		}
	}

	/**
	 * 结束通话的回调
	 */
	@Subscribe(threadMode = ThreadMode.MAIN)
	fun commHangUpResult(event: CommHangUpResultEvent) {
		if (event.update) {
			if (event.bean.result.contains("suc", false)) {
				phoneDetailList.clear()
				notifyAdapterAndJudgeShowNoCall()
				thereNoCall()
			} else {
				ToastUtils.show(event.bean.reason)
			}
		} else {
			//doNothing
		}
	}

	/**
	 * 通话状态列表数据和adapter
	 */
	val phoneDetailList = ArrayList<GetCommStatusRespBean>()
	private var phoneDetailAdapter: AbstractSimpleAdapter<GetCommStatusRespBean>? = null

	/**
	 * 通话状态列表RV相关加载
	 */
	private fun initRcvPhoneDetail() {
		if (null != ReceivedConstants.commStatusBean && ReceivedConstants.commStatusBean!!.result == "suc" && ReceivedConstants.commStatusBean!!.comm_status != COMM_STATUS_CALL_OVER) {
			phoneDetailList.clear()
			phoneDetailList.add(ReceivedConstants.commStatusBean!!)
			thereIsACall(ReceivedConstants.commStatusBean!!.comm_status)
		} else {
			thereNoCall()
			return
		}

		if (null == phoneDetailAdapter) {
			phoneDetailAdapter = object :
				AbstractSimpleAdapter<GetCommStatusRespBean>(
					context,
					phoneDetailList,
					R.layout.item_phone_detail_item
				) {
				@SuppressLint("SimpleDateFormat")
				override fun onBindViewHolder(holder: ViewHolder?, data: GetCommStatusRespBean?, curPosition: Int) {
					holder!!.getView<TextView>(R.id.tvCallTyep).text = when (data!!.call_type) {
						TYPE_AUDIO_SINGLE_VIDEO_CALL -> "视频通话"
						TYPE_AUDIO_MEETING_VIDEO_CALL -> "视频会议"
						TYPE_AUDIO_SINGLE_AUDIO_CALL -> "语音通话"
						TYPE_AUDIO_MEETING_AUDIO_CALL -> "语音会议"
						else -> "通话状态尚未设置"
					}

					holder.getView<TextView>(R.id.tvCallName).text = data?.comm_direct?.call_out!![0]
					holder.getView<TextView>(R.id.tvBeCalledName).text = data.comm_direct.call_in[0]

					when (data.comm_status) {
						COMM_STATUS_CALL_PLAYING -> {
							val formattar = SimpleDateFormat("HH:mm:ss")
							val time = formattar.format(data.comm_time.toInt() * 1000 - TimeZone.getDefault().rawOffset)
							holder.getView<TextView>(R.id.tvConnectedTime).text = time
						}
						COMM_STATUS_CALL_OUT_WAITING -> {
							holder.getView<TextView>(R.id.tvConnectedTime).text = "呼叫中..."
							holder.getView<TextView>(R.id.tvConnectedTime).setTextColor(Color.parseColor("#66adff"))
						}
						COMM_STATUS_CALL_IN_WAITING -> {
							holder.getView<TextView>(R.id.tvConnectedTime).text = "振铃中..."
							holder.getView<TextView>(R.id.tvConnectedTime).setTextColor(Color.parseColor("#66adff"))
						}
						COMM_STATUS_CALL_OVER -> {
							//doNothing
						}
						else -> {
							//doNothing
						}
					}
				}
			}
			rcvPhoneDetailList.layoutManager = LinearLayoutManager(context)
			rcvPhoneDetailList.adapter = phoneDetailAdapter
		} else {
			//doNothing
		}


		phoneDetailAdapter!!.setOnItemClickListener { o, position, viewHolder ->
			//点击事件保留
		}
		notifyAdapterAndJudgeShowNoCall()

		if (ReceivedConstants.commStatusBean!!.comm_mute_status == "true") {
			btnMute.tag = "mute"
		} else if (ReceivedConstants.commStatusBean!!.comm_mute_status == "false") {
			btnMute.tag = "cancelMute"
		}
		switchBtnMuteStatus()
		if (ReceivedConstants.commStatusBean!!.comm_ptt_status == "true") {
			btnClickAndSpeak.tag = "clickAndSpeak"
		} else if (ReceivedConstants.commStatusBean!!.comm_ptt_status == "false") {
			btnClickAndSpeak.tag = "clickAndCancelSpeak"
		}
		switchBtnClickSpeakStatus()
	}

	/**
	 * 当前列表中无通话，隐藏底部状态栏的所有按钮
	 */
	private fun thereNoCall() {
//		TODO("此处隐藏发起新的呼叫,正确的逻辑是btnBottomFunc显示，但是根据需求，先隐藏")
		btnBottomFunc.visibility = View.GONE

		btnMute.visibility = View.GONE
		btnEndCall.visibility = View.GONE
		//根据通话类型，判断是否要显示按一下发言
		btnClickAndSpeak.visibility = View.GONE

		ctlBottomBarWhileWaiting.visibility = View.GONE
	}

	/**
	 * 当前列表中有通话，改变底部状态栏的相关状态
	 */
	private fun thereIsACall(commStatus: String) {
		when (commStatus) {
			COMM_STATUS_CALL_PLAYING -> {
				btnBottomFunc.visibility = View.GONE
				btnMute.visibility = View.VISIBLE
				btnEndCall.visibility = View.VISIBLE
				ctlBottomBarWhileWaiting.visibility = View.GONE
				if (ReceivedConstants.commStatusBean != null) {
					when (ReceivedConstants.commStatusBean!!.call_type) {
						TYPE_AUDIO_SINGLE_AUDIO_CALL, TYPE_AUDIO_SINGLE_VIDEO_CALL -> {
							btnClickAndSpeak.visibility = View.GONE
							btnMute.visibility = View.VISIBLE
						}
						else -> {
							btnClickAndSpeak.visibility = View.VISIBLE
							btnMute.visibility = View.GONE
						}
					}
				}
			}
			COMM_STATUS_CALL_OUT_WAITING -> {
				btnBottomFunc.visibility = View.GONE
				btnMute.visibility = View.GONE
				btnEndCall.visibility = View.GONE
				btnClickAndSpeak.visibility = View.GONE
				ctlBottomBarWhileWaiting.visibility = View.VISIBLE
				btnEndTheCallWhileWaiting.visibility = View.VISIBLE
				btnAgreeCall.visibility = View.GONE
			}
			COMM_STATUS_CALL_IN_WAITING -> {
				btnBottomFunc.visibility = View.GONE
				btnMute.visibility = View.GONE
				btnEndCall.visibility = View.GONE
				btnClickAndSpeak.visibility = View.GONE
				ctlBottomBarWhileWaiting.visibility = View.VISIBLE
				btnEndTheCallWhileWaiting.visibility = View.VISIBLE
				btnAgreeCall.visibility = View.VISIBLE
			}
		}
	}

	/**
	 * 切换btnClickSpeak的状态
	 */
	private fun switchBtnClickSpeakStatus() {
		btnClickAndSpeak.apply {
			if ("clickAndSpeak" == tag) {
				setBackgroundResource(R.drawable.drawable_little_blue_4dp_rect)
				this.tag = "clickAndCancelSpeak"
				text = "按一下取消发言"
			} else if ("clickAndCancelSpeak" == tag) {
				setBackgroundResource(R.drawable.drawable_blue_4dp_rect)
				this.tag = "clickAndSpeak"
				text = "按一下发言"
			}
		}
	}

	/**
	 * 切换btnMute的状态
	 */
	private fun switchBtnMuteStatus() {
		btnMute.apply {
			if ("mute" == tag) {
				setBackgroundResource(R.drawable.drawable_little_gray_4dp_rect)
				text = "已静音"
				this.tag = "cancelMute"
			} else if ("cancelMute" == tag) {
				setBackgroundResource(R.drawable.drawable_blue_gray_4dp_rect)
				text = "静音"
				this.tag = "mute"
			}
		}
	}


	/**
	 * 当前界面的点击事件
	 */
	private fun initClick() {
		ivBack.setOnClickListener {
			if (activity is MainActivity) {
				Navigation.findNavController(it).navigateUp()
			} else {
				activity?.finish()
			}

		}

		btnBottomFunc.setOnClickListener {
			ctlBottomDialog.apply {
				visibility = View.VISIBLE
				startAnimation(AnimationUtils.loadAnimation(context!!, R.anim.slide_in_bottom))
			}
		}

		btnCancel.setOnClickListener {
			ctlBottomDialog.apply {
				visibility = View.GONE
				startAnimation(AnimationUtils.loadAnimation(context!!, R.anim.slide_out_bottom))
			}
		}

		btnCallPhone.setOnClickListener {
			DialogUtils.showNumKeyBoard(context!!, object : DialogUtils.MCallBack {
				override fun onCallBackDispatch(type: String, text: String) {
					when (type) {
						"audio" -> {
							UDPSocket.getmUDPSocket(context)
								.sendMessage(
									RequestModel.getCommCallOutRequestJson(
										TYPE_AUDIO_SINGLE_AUDIO_CALL,
										"\"[$text\"]"
									)
								)
							ToastUtils.show("打语音电话给$text")
						}
						"video" -> {
							UDPSocket.getmUDPSocket(context)
								.sendMessage(
									RequestModel.getCommCallOutRequestJson(
										TYPE_AUDIO_SINGLE_VIDEO_CALL,
										"\"[$text\"]"
									)
								)
							ToastUtils.show("打视频电话给$text")
						}
					}
				}
			})
		}

		btnWorkGroup.setOnClickListener {
			Navigation.findNavController(it).navigate(R.id.action_farPhoneFragment_to_selectFromWorkGroupFragment)
		}

		btnContact.setOnClickListener {
			Navigation.findNavController(it).navigate(R.id.action_farPhoneFragment_to_selectFromPersonsFragment)
		}


		btnClickAndSpeak.setOnClickListener {
			val tag: String = btnClickAndSpeak.tag as String
			btnClickAndSpeak.apply {
				if ("clickAndSpeak" == tag) {
					UDPSocket.getmUDPSocket(context).sendMessage(RequestModel.getCommPttRequestJson(needPtt = true))
				} else if ("clickAndCancelSpeak" == tag) {
					UDPSocket.getmUDPSocket(context).sendMessage(RequestModel.getCommPttRequestJson(needPtt = false))
				}
			}
		}

		btnMute.setOnClickListener {
			val tag: String = btnMute.tag as String
			btnMute.apply {
				if ("mute" == tag) {
					UDPSocket.getmUDPSocket(context).sendMessage(RequestModel.getCommMuteRequestJson(isMute = true))
				} else if ("cancelMute" == tag) {
					UDPSocket.getmUDPSocket(context).sendMessage(RequestModel.getCommMuteRequestJson(isMute = false))
				}
			}
		}

		btnEndCall.setOnClickListener {
			endCallByAllStatus()
		}

		btnEndTheCallWhileWaiting.setOnClickListener {
			endCallByAllStatus()
		}


		btnAgreeCall.setOnClickListener {
			UDPSocket.getmUDPSocket(context).sendMessage(RequestModel.getCommAgreeCallRequestJson())
		}

	}

	/**
	 * 结束通话
	 */
	private fun endCallByAllStatus() {
		ToastUtils.show("结束通话")
		thereNoCall()
		UDPSocket.getmUDPSocket(context).sendMessage(RequestModel.getCommHangUpRequestJson())
	}

	/**
	 * 静音/已静音 的回调
	 */
	@Subscribe(threadMode = ThreadMode.MAIN)
	fun commMuteResult(event: CommMuteResultEvent) {
		if (event.update) {
			if (event.bean.result.contains("suc", ignoreCase = false)) {
				switchBtnMuteStatus()
			} else {
				//doNothing
			}
		} else {
			//doNothing
		}
	}


	/**
	 * 获取远程会议的用户信息的回调
	 */
	@Subscribe(threadMode = ThreadMode.MAIN)
	fun commInfoResult(event: CommInfoResultEvent) {
		if (event.update) {
			tvProductNickName.text = event.bean.comm_name
			tvOnlineNumber.text = event.bean.comm_tel
			tvStatus.setTextColor(Color.parseColor("#44760B"))
			tvStatus.setCompoundDrawables(drawableStatusOnline, null, null, null)

		} else {
			//doNothing
		}
		tvStatus.text = "在线"
	}


	/**
	 * 按一下发言/按一下取消发言 的回调
	 */
	@Subscribe(threadMode = ThreadMode.MAIN)
	fun commPttResult(event: CommPttResultEvent) {
		if (event.update) {
			if (event.bean.result.contains("suc", ignoreCase = false)) {
				switchBtnClickSpeakStatus()
			} else {
				//doNothing
			}
		} else {
			//doNothing
		}
	}


}

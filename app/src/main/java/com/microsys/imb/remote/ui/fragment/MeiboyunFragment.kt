package com.microsys.imb.remote.ui.fragment

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.microsys.imb.remote.R
import com.microsys.imb.remote.base.BaseNavigationFragment
import com.microsys.imb.remote.constants.ReceivedConstants
import com.microsys.imb.remote.constants.RequestModel
import com.microsys.imb.remote.event.ControlLiveInfoEvent
import com.microsys.imb.remote.event.UpdateMeiboyunLiveInfo
import com.microsys.imb.remote.udps.socket.UDPSocket
import com.microsys.imb.remote.ui.activity.MainActivity
import com.sunday.common.utils.StatusBarUtils
import com.tencent.rtmp.ITXLivePlayListener
import com.tencent.rtmp.TXLiveConstants
import com.tencent.rtmp.TXLivePlayConfig
import com.tencent.rtmp.TXLivePlayer
import kotlinx.android.synthetic.main.fragment_meiboyun.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 *
 * 描述：直播页面
 * 作者: mlx
 * 创建时间： 2019.07.08
 *
 */
class MeiboyunFragment : BaseNavigationFragment() {

	private val sText = "<html>\n" +
			"<table class=\"table\">\n" +
			"            <thead>\n" +
			"                <tr>\n" +
			"                    <td class=\"intro-td\">\n" +
			"                        <li class=\"point\"><span>议程时间</span></li>\n" +
			"                    </td>\n" +
			"                </tr>\n" +
			"                </thead>\n" +
			"                <tbody>\n" +
			"                <tr>\n" +
			"                    <td class=\"intro-td\">\n" +
			"                        &nbsp;&nbsp;&nbsp;&nbsp;大会演讲直播时间：2018年4月12日 11：00-12：00<br>媒体专访直播时间：2018年4月13日 14：20-14：40</td>\n" +
			"                </tr>\n" +
			"                </tbody>\n" +
			"                <thead>\n" +
			"                <tr>\n" +
			"                    <td class=\"intro-td\">\n" +
			"                        <li class=\"point\"><span>议程地点</span></li>\n" +
			"                    </td>\n" +
			"                </tr>\n" +
			"                </thead>\n" +
			"                <tbody>\n" +
			"                <tr>\n" +
			"                    <td class=\"intro-td\">\n" +
			"                        &nbsp;&nbsp;&nbsp;&nbsp;大会议室</td>\n" +
			"                </tr>\n" +
			"                </tbody>\n" +
			"                <thead>\n" +
			"                <tr>\n" +
			"                    <td class=\"intro-td\">\n" +
			"                        <li class=\"point\"><span>议程简介</span></li>\n" +
			"                    </td>\n" +
			"                </tr>\n" +
			"                </thead>\n" +
			"                <tbody>\n" +
			"                <tr>\n" +
			"                    <td class=\"intro-td\">\n" +
			"                        &nbsp;&nbsp;&nbsp;&nbsp;随着人工智能，云计算，大数据以及社交媒体的兴起，传统的通信模式、企业和客户的交互方式以及沟通形态，服务理念都在发生转变，新的行业格局正在形成，将改变传统的服务与营销，重新诠释互联网+时代下的客户体验。</td>\n" +
			"                </tr>\n" +
			"                </tbody>\n" +
			"            \n" +
			"        </table>\n" +
			"</html>"

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_meiboyun, container, false)
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		initData()
	}

	companion object {
		//正在直播中
		const val LIVE_PLAYING = "1"
		//预约直播
		const val LIVE_PREPARING = "0"
		//直播结束
		const val LIVE_STOPPING = "-1"
	}

	private var liveUrl: String? = ""
	private var liveStatus: String? = ""

	private fun initData() {
		StatusBarUtils.initSystemBarColor(this.activity as Activity?, false, Color.WHITE)
		tvProductName.text = "网络直播"
		ivProductPic.setImageResource(R.mipmap.icon_meiboyun_large)
//        val text=String(Base64.decode(sText,Base64.DEFAULT))
		val textString = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
			Html.fromHtml(sText, Html.FROM_HTML_MODE_LEGACY)
		} else {
			Html.fromHtml(sText)
		}
		tvHtmlText.text = textString
		initClick()

		initLiveInfo()
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	fun initLiveInfo(liveEvent: UpdateMeiboyunLiveInfo) {
		if (liveEvent.update) {
			initLiveInfo()
		} else {
			//doNothing
		}

	}

	/**
	 * 加载直播信息
	 */
	private fun initLiveInfo() {
		if (null == ReceivedConstants.liveInfoBean) {
			return
		}
		liveUrl = ReceivedConstants.liveInfoBean?.play_url
		liveStatus = ReceivedConstants.liveInfoBean?.live_status
		tvProductNickName.text = ReceivedConstants.liveInfoBean?.device_id
		when (liveStatus) {
			LIVE_PLAYING -> {
				btnStartPlay.visibility = View.GONE
				ivPlayingStatus.setImageResource(R.mipmap.icon_live_playing_now)
				startPlayLive()
			}
			LIVE_PREPARING -> {
				btnStartPlay.visibility = View.VISIBLE
				ivPlayingStatus.setImageResource(R.mipmap.icon_start_play_right_away_bg)
			}
			LIVE_STOPPING -> {
			}
			else -> {
			}
		}
	}

	private var mLivePlayer: TXLivePlayer? = null
	private var mPlayConfig: TXLivePlayConfig? = null

	private fun startPlayLive() {
		if (null == mLivePlayer) {
			mLivePlayer = TXLivePlayer(context)
		}
		mLivePlayer?.setPlayerView(txVideoView)

		mLivePlayer?.enableHardwareDecode(true)
		mLivePlayer?.setRenderRotation(TXLiveConstants.RENDER_ROTATION_PORTRAIT)
		mLivePlayer?.setRenderMode(TXLiveConstants.RENDER_MODE_FULL_FILL_SCREEN)

		mPlayConfig = TXLivePlayConfig()


		//流畅模式
		mPlayConfig?.setAutoAdjustCacheTime(false)
		mPlayConfig?.setMinAutoAdjustCacheTime(5F)
		mPlayConfig?.setMaxAutoAdjustCacheTime(5F)

		mLivePlayer?.setConfig(mPlayConfig)

		mLivePlayer?.setPlayListener(object : ITXLivePlayListener {
			override fun onPlayEvent(i: Int, bundle: Bundle) {

			}

			override fun onNetStatus(bundle: Bundle) {

			}
		})

		mLivePlayer?.startPlay(liveUrl, TXLivePlayer.PLAY_TYPE_LIVE_RTMP)
	}

	override fun onStart() {
		super.onStart()
		if (!EventBus.getDefault().isRegistered(this)) {
			EventBus.getDefault().register(this)
		}
	}

	override fun onDestroy() {
		if (EventBus.getDefault().isRegistered(this)) {
			EventBus.getDefault().unregister(this)
		}
		// true 代表清除最后一帧画面
		mLivePlayer?.stopPlay(true)
		txVideoView?.onDestroy()
		super.onDestroy()
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	fun getControlLiveInfoCallbackEvent(event: ControlLiveInfoEvent) {
		if (event.mCallback) {
			if (event.controlLiveRespBean.type == "start_live") {
				btnStartPlay.tag = "clickToPlay"
				btnStartPlay.callOnClick()
			} else if (event.controlLiveRespBean.type == "stop_live") {
				btnStartPlay.tag = "clickToStop"
				btnStartPlay.callOnClick()
			}
		}
	}


	private fun initClick() {
		ivBack.setOnClickListener {
			if (activity is MainActivity) {
				Navigation.findNavController(it).navigateUp()
			} else {
				activity?.finish()
			}
		}

		btnStartPlay.setOnClickListener {
			val tag: String = it.tag as String
			btnStartPlay.apply {
				if ("clickToPlay" == tag) {
					setBackgroundResource(R.drawable.drawable_red_4dp_rect)
					text = "停止直播"
					this.tag = "clickToStop"
					UDPSocket.getmUDPSocket(context).sendMessage(
						RequestModel.getStartLiveRequestJson()
					)
					mLivePlayer?.pause()
				} else {
					setBackgroundResource(R.drawable.drawable_blue_4dp_rect)
					text = "开始直播"
					this.tag = "clickToPlay"
					UDPSocket.getmUDPSocket(context).sendMessage(
						RequestModel.getStopLiveRequestJson()
					)
					mLivePlayer?.resume()
				}
			}
		}
	}


}

package com.microsys.imb.remote.ui.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.core.BasePopupView
import com.microsys.imb.remote.R
import com.microsys.imb.remote.app.MyApplication
import com.microsys.imb.remote.base.BaseNavigationFragment
import com.microsys.imb.remote.bean.ConnectResponseBean
import com.microsys.imb.remote.bean.DeviceInfoBean
import com.microsys.imb.remote.constants.*
import com.microsys.imb.remote.event.*
import com.microsys.imb.remote.service.StartHeartService
import com.microsys.imb.remote.service.UdpHeartBeatService
import com.microsys.imb.remote.udps.socket.UDPSocket
import com.microsys.imb.remote.ui.activity.CommMainActivity
import com.microsys.imb.remote.ui.activity.GisMainActivity
import com.microsys.imb.remote.ui.activity.MeiboyunMainActivity
import com.microsys.imb.remote.ui.adapter.recycler.AbstractSimpleAdapter
import com.microsys.imb.remote.ui.adapter.recycler.ViewHolder
import com.microsys.imb.remote.utils.ChangeCenterUtils
import com.microsys.imb.remote.utils.CheckVersionUtil.Companion.checkVersion
import com.microsys.imb.remote.widgets.dialog.CUR_SELECT_CENTER
import com.microsys.imb.remote.widgets.dialog.DeleteCenterDialog
import com.microsys.imb.remote.widgets.dialog.SearchDeviceDialogView
import com.sunday.common.logger.Logger
import com.sunday.common.utils.NetWorkBroadCast
import com.sunday.common.utils.SharedPerencesUtil
import com.sunday.common.utils.StatusBarUtils
import com.sunday.common.utils.ToastUtils
import com.youth.banner.loader.ImageLoader
import kotlinx.android.synthetic.main.fragment_home.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

const val CHECKED_CENTER_LIST = "checkedCenterList"

/**
 *
 * 描述：主页
 * 作者: mlx
 * 创建时间： 2019.07.08
 *
 */
class HomeFragment : BaseNavigationFragment() {

	companion object {
		var isAlive = false
	}

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		return inflater.inflate(R.layout.fragment_home, container, false)
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		initData()
	}

	/**
	 * 初始化一些数据
	 */
	private fun initData() {
		StatusBarUtils.initSystemBarColor(this.activity as Activity?, true, Color.parseColor("#434958"))

		isAlive = true

		initBanner()
		initRcvProductList()
		initRcvCenterList()

		checkVersion(context)

		initClick()

		registerNetBoardCast()

		recoverInfo()


	}

	/**
	 * 恢复之前保存的一些状态
	 */
	private fun recoverInfo() {
		if (null != CheckedCenterConstants.curSelectCenter) {
			//设定Center的name
			tvCenterName.text = CheckedCenterConstants.curSelectCenter?.account_name
			//已经有选中了，就把添加取消
			tvAddNewCenter.visibility = View.GONE
			//appInfo，保存过的就重新设置进去
			if (!TextUtils.isEmpty(ProductListConstants.productListInfo)) {
				val dataBeanList: ArrayList<DeviceInfoBean>? =
					Gson().fromJson(
						ProductListConstants.productListInfo,
						object : TypeToken<ArrayList<DeviceInfoBean>?>() {}.type
					)
				adapterProduct?.resetData(dataBeanList)
			}
			//配对过的center重新设置到rcv中
			if (!TextUtils.isEmpty(ProductListConstants.centerListInfo)) {
				val dataBeanList: ArrayList<ConnectResponseBean>? =
					Gson().fromJson(
						ProductListConstants.centerListInfo,
						object : TypeToken<ArrayList<ConnectResponseBean>?>() {}.type
					)
				if (null != dataBeanList) {
					resetDataForCenter(dataBeanList)
				} else {
					//doNothing
				}

			}
			tvCenterName.text = ProductListConstants.accountName
		}
	}

	//心跳service
	private var heartBeatServiceIntent: Intent? = null


	override fun onStart() {
		super.onStart()
		if (!EventBus.getDefault().isRegistered(this)) {
			EventBus.getDefault().register(this)
		}
	}


	/**
	 * 用户是否第一点击center，如果是的话，则会引导用户如何删除center
	 * 第一次过后会重置为false
	 */
	private val IS_FIRST_CLICK_CENTER = "IS_FIRST_CLICK_CENTER"

	/**
	 * 加载点击事件
	 */
	private fun initClick() {
		tvCenterName.setOnClickListener {
			//			startActivity(Intent(context, CommMainActivity::class.java))
			//			Navigation.findNavController(tvCenterName).navigate(R.id.action_homeFragment_to_farPhoneFragment)
//			var isRequest = false
//			var popDialog = XPopup.Builder(context)
//				.dismissOnTouchOutside(!isRequest)
//				.dismissOnBackPressed(!isRequest)
//				.asCustom(UpdateDialog(context!!, isRequest))
//				.show()
//			UDPSocket.getmUDPSocket(context).sendMessage(RequestModel.getConnectRequestJson("255.255.255.255","6000"))

		}

		tvCenterInfo.setOnClickListener {
			val isFirstClickCenter = SharedPerencesUtil.getString(IS_FIRST_CLICK_CENTER)
			if (TextUtils.isEmpty(isFirstClickCenter)) {
				SharedPerencesUtil.putString(IS_FIRST_CLICK_CENTER, "true")
				viewShadow.setBackgroundColor(Color.parseColor("#99000000"))
				ivLongClickToDelete.visibility = View.VISIBLE
			} else {
				viewShadow.setBackgroundColor(Color.TRANSPARENT)
				ivLongClickToDelete.visibility = View.GONE
				//doNothing
			}
			if (flCenterList.visibility == View.VISIBLE) {
				flCenterList.visibility = View.GONE
				viewShadow.visibility = View.GONE
//				rcvCenterList.startAnimation(AnimationUtils.loadAnimation(context, R.anim.slide_out_left))
			} else {
				flCenterList.visibility = View.VISIBLE
				viewShadow.visibility = View.VISIBLE
//				rcvCenterList.startAnimation(AnimationUtils.loadAnimation(context, R.anim.slide_in_left))
			}
			if (adapter!!.getmDatas().size == 0) {
				tvNoCenter.visibility = View.VISIBLE
			} else {
				tvNoCenter.visibility = View.GONE
			}
		}

		viewSearchDeviceInfo.setOnClickListener {
			showSearchDeviceView()
		}

		tvAddNewCenter.setOnClickListener {
			showSearchDeviceView()
		}

		viewShadow.setOnClickListener {
			tvCenterInfo.callOnClick()
		}

	}

	var popDialog: BasePopupView? = null
	/**
	 * 弹出搜索设备的dialog
	 */
	private fun showSearchDeviceView() {

		popDialog = XPopup.Builder(context)
			.dismissOnBackPressed(true)
			.asCustom(SearchDeviceDialogView(context!!))
			.show()
		//停止掉3S心跳Service,第一次安装启动会有点问题
		if (StartHeartServiceConstants.isRunning) {
			stopStartHeartService()
		} else {
			//doNothing
		}
	}

	private var mHandler: Handler = object : Handler(Looper.getMainLooper()) {
		override fun handleMessage(msg: Message?) {
			when (msg?.what) {
				NetWorkBroadCast.NET_CONNECT -> {
					srfLayout.visibility = View.GONE
					rcvCurProducts.visibility = View.VISIBLE
					if (null == CheckedCenterConstants.curSelectCenter) {
						tvAddNewCenter.visibility = View.VISIBLE
					} else {
						//doNothing
					}
				}
				NetWorkBroadCast.NET_DISCONNECT -> {
					tvAddNewCenter.visibility = View.GONE
					srfLayout.visibility = View.GONE
					rcvCurProducts.visibility = View.GONE
				}
			}
			super.handleMessage(msg)
		}
	}

	//网络状态变化的监听
	private lateinit var broadCast: NetWorkBroadCast

	/**
	 * 注册网络变化receiver
	 */
	private fun registerNetBoardCast() {
		broadCast = NetWorkBroadCast(mHandler)
		val filter = IntentFilter()
		filter.addAction("android.net.conn.CONNECTIVITY_CHANGE")
		context?.registerReceiver(broadCast, filter)
	}

	override fun onDestroyView() {
		isAlive = false
		context?.unregisterReceiver(broadCast)
		stopStartHeartService()
		ProductListConstants.productListInfo = Gson().toJson(adapterProduct?.getmDatas())
		ProductListConstants.centerListInfo = Gson().toJson(adapter?.getmDatas())
		ProductListConstants.accountName = tvCenterName.text.toString()
		//保存已经配对过的云上会面，并且保存当前选中的云上会面
		ChangeCenterUtils.saveCenterInfo()
		super.onDestroyView()
	}

	override fun onDestroy() {
		super.onDestroy()

		if (EventBus.getDefault().isRegistered(this)) {
			EventBus.getDefault().unregister(this)
		}
		stopHeartBeatService()
		Logger.e("我被销毁了")
	}


	fun stopHeartBeatService() {
		try {
			context?.stopService(heartBeatServiceIntent)
		} catch (e: Exception) {

		}
	}

	/**
	 * 选中center以后，更新列表，并且开始获取三个子产品的信息
	 */
	@Subscribe(threadMode = ThreadMode.MAIN)
	fun startGetAppInfo(event: StartToGetAppInfoEvent) {
		if (event.isStart) {
			//从Map中更新数据到最新的set和list中
			CheckedCenterConstants.checkedSetList = CheckedCenterConstants.checkedMapList.values.toHashSet()
			CheckedCenterConstants.checkedArrayList = CheckedCenterConstants.checkedMapList.values.toMutableList()
			val tempCheckedList = ArrayList(CheckedCenterConstants.checkedArrayList)

			if (adapter?.selectItemList!!.size > 0) {
				adapter?.selectItemList!!.clear()
			} else {
				//doNothing
			}
			adapter?.setSelectItemListAdd(CheckedCenterConstants.curSelectCenter)


			resetDataForCenter(tempCheckedList)

			//保存已经配对过的云上会面，之后进入记得加载

//			TODO("设置已经选中的配对中心位true,并且更新列表")
			ChangeCenterUtils.saveCenterInfo()
			EventBus.getDefault().post(DeviceConnectStatusEvent(true))
			tvCenterName.text = CheckedCenterConstants.curSelectCenter?.account_name
			CheckedCenterConstants.curAccountNum = ""
			UDPSocket.getmUDPSocket(context).sendMessage(
				RequestModel.getAppInfoRequestJson()
			)
		}
	}

	private fun resetDataForCenter(list: ArrayList<ConnectResponseBean>) {
		adapter?.resetData(list)
		if (adapter?.itemCount == 0 || null == adapter?.itemCount) {
			tvNoCenter.visibility = View.VISIBLE
		} else {
			tvNoCenter.visibility = View.GONE
		}
	}

	/**
	 * 用户按下物理返回键，如果有弹窗，则让弹窗消失
	 */
	@Subscribe(threadMode = ThreadMode.MAIN)
	fun dismissDialog(event: DismissCurDialogEvent) {
		if (event.dismiss) {
			popDialog?.dismiss()
		} else {
			//doNothing
		}
	}

	/**
	 * 获得三大板块的实时信息,更新产品列表
	 */
	@Subscribe(threadMode = ThreadMode.MAIN)
	fun getAppInfoResult(event: TransAppInfoEvent) {
		val deviceList: ArrayList<DeviceInfoBean>? = ArrayList()
		CheckedCenterConstants.curAccountNum = ""

		if (event.appInfoBean?.device_id != CheckedCenterConstants.curSelectCenter?.device_id) {
			return
		}

		if (null == CheckedCenterConstants.curSelectCenter || CheckedCenterConstants.curSelectCenter!!.device_ip == "123456") {
			return
		}

		CheckedCenterConstants.curAccountNum = event.appInfoBean?.account_num

//		TODO("尝试把这部分review")
		val tempCommBean = DeviceInfoBean(
			isExist = event.appInfoBean?.info?.communication?.isExist?.toBoolean()!!
			, isOnline = event.appInfoBean?.info?.communication?.isOnline?.toBoolean()!!, name = "comm"
		)
		if (tempCommBean.isExist) {
			deviceList?.add(tempCommBean)
		}

		val tempLiveBean = DeviceInfoBean(
			isExist = event.appInfoBean?.info?.live?.isExist?.toBoolean()!!
			, isOnline = event.appInfoBean?.info?.live?.isOnline?.toBoolean()!!, name = "live"
		)
		if (tempLiveBean.isExist) {
			deviceList?.add(tempLiveBean)
		}

		val tempGisBean = DeviceInfoBean(
			isExist = event.appInfoBean?.info?.gis?.isExist?.toBoolean()!!
			, isOnline = event.appInfoBean?.info?.gis?.isOnline?.toBoolean()!!, name = "gis"
		)

		if (tempGisBean.isExist) {
			deviceList?.add(tempGisBean)
		}

		if (CheckedCenterConstants.curSelectCenter!!.device_ip.isEmpty()) {
			deviceList?.clear()
		} else {
			//doNothing
		}

		adapterProduct?.resetData(deviceList)
		if (adapterProduct!!.itemCount > 0) {

			startCheckDeviceInfoLoop()
		} else {
			//doNothing
		}
		//只要配对成功了，就隐藏tvAddNewCenter
		tvAddNewCenter.visibility = View.GONE
	}


	var adapter: AbstractSimpleAdapter<ConnectResponseBean>? = null


	/**
	 * 判断之前是否已经有配对过的center，如果有就设置到list进去，并且初始化选中的center
	 */
	private fun checkSavedCenterInfo(): ArrayList<ConnectResponseBean>? {
		var list: ArrayList<ConnectResponseBean>? = ArrayList()
		val checkedResult = SharedPerencesUtil.getString(CHECKED_CENTER_LIST)
		if (TextUtils.isEmpty(checkedResult)) {
			//doNothing
		} else {
			list = Gson().fromJson(
				checkedResult,
				object : TypeToken<ArrayList<ConnectResponseBean>?>() {}.type
			)
			if (list?.size == 0) {
				return list
			}
			for (item in list!!) {
				CheckedCenterConstants.checkedMapList.put(item.device_id, item)
			}

			//开启3S轮回一次的心跳
			MyApplication.sInstance.startService(Intent(MyApplication.sInstance, StartHeartService::class.java))
		}
		val curSelectCenterString = SharedPerencesUtil.getString(CUR_SELECT_CENTER)
		if (TextUtils.isEmpty(curSelectCenterString)) {
			//doNothing
		} else {
			if (curSelectCenterString.contains("123456", false)) {
				//如果是有123456，那就说明搜索的时候点击了申请配对，但是配对流程中止或者失败了
			} else {
				CheckedCenterConstants.curSelectCenter =
					Gson().fromJson(curSelectCenterString, ConnectResponseBean::class.java)
			}
		}
		return list
	}

	/**
	 * 加载获取到的中心信息
	 */
	private fun initRcvCenterList() {
		var list: ArrayList<ConnectResponseBean>? = checkSavedCenterInfo()
		adapter = object : AbstractSimpleAdapter<ConnectResponseBean>(context, list, R.layout.item_center_info) {
			override fun onBindViewHolder(holder: ViewHolder?, data: ConnectResponseBean?, curPosition: Int) {
				holder?.getView<ImageView>(R.id.ivCenterPic)?.apply {
					if (adapter?.selectItemList!!.contains(data)) {
						setBackgroundResource(R.mipmap.icon_center_select)
					} else {
						setBackgroundResource(R.mipmap.icon_center_not_select)
					}
				}
				holder?.getView<TextView>(R.id.tvCenterName)?.text = data?.account_name
			}
		}
		if (null != CheckedCenterConstants.curSelectCenter) {
			setCurSelectCenter(CheckedCenterConstants.curSelectCenter!!)
		}
		adapter?.setOnItemClickListener { o, position, _ ->
			val data = o as ConnectResponseBean
			if (data.device_ip.isEmpty()) {
				EventBus.getDefault().post(DeviceConnectStatusEvent(false))
			} else {
				EventBus.getDefault().post(DeviceConnectStatusEvent(true))
			}
			if (setCurSelectCenter(data)) {
				tvCenterInfo.callOnClick()
			}

			stopStartHeartService()
//			TODO("直接切换到已经配对了的云上会面")
		}
		adapter?.setOnItemLongClickListener { o, position, viewHolder ->

			XPopup.Builder(context)
				.asCustom(DeleteCenterDialog(context!!, o as ConnectResponseBean, position))
				.show()

		}
		rcvCenterList.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
		rcvCenterList.setHasFixedSize(true)
		rcvCenterList.adapter = adapter
	}

	var adapterProduct: AbstractSimpleAdapter<DeviceInfoBean>? = null

	/**
	 * 产品列表加载
	 */
	private fun initRcvProductList() {
		val list: ArrayList<DeviceInfoBean>? = ArrayList()
		val drawableStatusOnline = ContextCompat.getDrawable(MyApplication.sInstance, R.mipmap.icon_point_online)
		drawableStatusOnline?.setBounds(0, 0, 12, 12)
		val drawableStatusOffLine = ContextCompat.getDrawable(MyApplication.sInstance, R.mipmap.icon_point_offline)
		drawableStatusOffLine?.setBounds(0, 0, 12, 12)

		adapterProduct = object : AbstractSimpleAdapter<DeviceInfoBean>(context, list, R.layout.item_product) {
			override fun onBindViewHolder(holder: ViewHolder?, data: DeviceInfoBean?, curPosition: Int) {
				holder?.itemView?.apply {
					if (curPosition == adapterProduct?.selectPosition) {
						setBackgroundColor(Color.parseColor("#fff7f8fa"))
					} else {
						setBackgroundColor(Color.TRANSPARENT)
					}
				}

				holder?.getView<ImageView>(R.id.ivProductPic)?.apply {
					when (data!!.name) {
						"comm" -> setBackgroundResource(R.mipmap.icon_far_phone)
						"gis" -> setBackgroundResource(R.mipmap.icon_gis_map)
						"live" -> setBackgroundResource(R.mipmap.icon_meiboyun)
					}
				}

				holder?.getView<TextView>(R.id.tvProductName)?.text =
					when (data!!.name) {
						"comm" -> "远程会议"
						"gis" -> "联情指挥"
						"live" -> "网络直播"
						else -> "未知设备"
					}

				holder?.getView<TextView>(R.id.tvProductStatus)?.apply {
					if (data.isOnline) {
						setTextColor(Color.parseColor("#44760B"))
						text = "在线"
						setCompoundDrawables(drawableStatusOnline, null, null, null)
					} else {
						setTextColor(Color.parseColor("#BA3535"))
						text = "离线"
						setCompoundDrawables(drawableStatusOffLine, null, null, null)
					}

				}
			}
		}
		adapterProduct!!.setOnItemClickListener { o, position, holder ->
			adapterProduct!!.selectPosition = position
			adapterProduct!!.notifyDataSetChanged()
			stopStartHeartService()
			when ((o as DeviceInfoBean).name) {
				"comm" -> {
					Thread.sleep(200L)
//					Navigation.findNavController(holder.itemView)
//						.navigate(R.id.action_homeFragment_to_farPhoneFragment)
					startActivity(Intent(context, CommMainActivity::class.java))
				}
				"gis" -> {
					startActivity(Intent(context, GisMainActivity::class.java))
//					Navigation.findNavController(holder.itemView).navigate(R.id.action_homeFragment_to_gisMapFragment)
				}
				"live" -> {
					Thread.sleep(200L)
					startActivity(Intent(context, MeiboyunMainActivity::class.java))
//					Navigation.findNavController(holder.itemView).navigate(R.id.action_homeFragment_to_meiboyunFragment)

				}
			}
		}
		rcvCurProducts.layoutManager = object : LinearLayoutManager(context) {}
		rcvCurProducts.setHasFixedSize(true)
		rcvCurProducts.adapter = adapterProduct



		if (list!!.isNotEmpty()) {
			tvAddNewCenter.visibility = View.GONE
		} else {
			tvAddNewCenter.visibility = View.VISIBLE
		}
	}

	/**
	 * 定时器对象
	 */
	private var scheduledExecutorService: ScheduledExecutorService = Executors.newScheduledThreadPool(2)
	/**
	 * 轮询是否开启
	 */
	private var isTimerStarted = false

	/**
	 * 轮询获取设备的状态的通话状态，以及直播信息
	 */
	private fun startCheckDeviceInfoLoop() {
		if (isTimerStarted) {
			return
		}
		heartBeatServiceIntent = Intent(activity, UdpHeartBeatService::class.java)
		context?.startService(heartBeatServiceIntent)
		scheduledExecutorService.scheduleAtFixedRate({
			isTimerStarted = true
			UDPSocket.getmUDPSocket(context).sendMessage(RequestModel.getAppInfoRequestJson())
			UDPSocket.getmUDPSocket(context).sendMessage(RequestModel.getCommStatusRequestJson())
		}, 0, 3L, TimeUnit.SECONDS)
	}

	/**
	 * banner相关
	 */
	private fun initBanner() {
		val bannerLists = listOf(R.mipmap.icon_banner_01, R.mipmap.icon_banner_02, R.mipmap.icon_banner_03)
		homeBanner.setImageLoader(object : ImageLoader() {
			override fun displayImage(context: Context?, path: Any?, imageView: ImageView?) {
				imageView?.setImageResource(path as Int)
			}
		})
		homeBanner.setImages(bannerLists)
		homeBanner.start()
	}


	/**
	 * 设置当前选中的云上会面
	 */
	private fun setCurSelectCenter(bean: ConnectResponseBean): Boolean {
		if (null != CheckedCenterConstants.curSelectCenter) {
			if (CheckedCenterConstants.curSelectCenter!!.device_id == bean.device_id) {
				return false
			} else {
				//doNothing
			}
		}
		CheckedCenterConstants.curSelectCenter = null
		CheckedCenterConstants.curSelectCenter = bean
		ReceivedConstants.commStatusBean = null
		UDPSocket.setBroadcastIp(bean.device_ip)
		UDPSocket.setServerPort(bean.device_port.toInt())
		if (bean.device_ip.isEmpty()) {
			UDPSocket.clearUdpSocket()
		}
		adapter?.selectItemList?.clear()
		adapter?.setSelectItemListAdd(bean)
		notifyAdapterAndJudgeShowNoCenter()
		tvCenterName.text = bean.account_name
		CheckedCenterConstants.curAccountNum = ""
		ToastUtils.show("切换成功")
		startCheckDeviceInfoLoop()
		return true
	}

	/**
	 * 刷新center列表，并且判断当前有无center，如果有，则隐藏提示，反之显示提示
	 */
	private fun notifyAdapterAndJudgeShowNoCenter() {
		adapter?.notifyDataSetChanged()
		if (adapter?.itemCount == 0 || null == adapter?.itemCount) {
			tvNoCenter.visibility = View.VISIBLE
		} else {
			tvNoCenter.visibility = View.GONE
		}
	}


	/**
	 * 从已经保存的配对过的云上会面中删除其中之一
	 */
	@Subscribe(threadMode = ThreadMode.MAIN)
	fun deleteOneSavedCenter(event: DeleteOneSavedCenterEvent) {
		if (event.delete) {
			CheckedCenterConstants.checkedMapList.remove(event.data.device_id)
			CheckedCenterConstants.checkedSetList.clear()
			CheckedCenterConstants.checkedSetList = CheckedCenterConstants.checkedMapList.values.toHashSet()
			CheckedCenterConstants.checkedArrayList.clear()
			CheckedCenterConstants.checkedArrayList = CheckedCenterConstants.checkedMapList.values.toMutableList()
			UDPSocket.clearUdpSocket()
			CheckedCenterConstants.curAccountNum = ""
			CheckedCenterConstants.curSelectCenter = null

			if (adapter?.selectItemList!!.contains(event.data)) {
				adapter?.getmDatas()?.remove(event.data)
			}

			if (CheckedCenterConstants.checkedArrayList.size > 0) {
				setCurSelectCenter(CheckedCenterConstants.checkedArrayList[0])
			} else {
				adapterProduct?.getmDatas()!!.clear()
				adapterProduct?.notifyDataSetChanged()
				tvAddNewCenter.visibility = View.VISIBLE
				tvCenterName.text = "无"
				notifyAdapterAndJudgeShowNoCenter()
			}

			ChangeCenterUtils.saveCenterInfo()

			ToastUtils.show("删除成功")
		}
	}

	/**
	 * 根据传过来的bean，修改当前已经保存在本地的bean的数据
	 */
	@Subscribe(threadMode = ThreadMode.MAIN)
	fun setOtherBeanInfo(event: SetOtherCenterBeanInfoEvent) {
		if (event.update) {
			//如果拿到的center是已经配对过的,则更新，否则啥事也不做
			if (CheckedCenterConstants.checkedMapList.keys.contains(event.bean.device_id)) {
				CheckedCenterConstants.checkedMapList.put(event.bean.device_id, event.bean)
				CheckedCenterConstants.checkedSetList = CheckedCenterConstants.checkedMapList.values.toHashSet()
				CheckedCenterConstants.checkedArrayList = CheckedCenterConstants.checkedMapList.values.toMutableList()
				val tempCheckedList = ArrayList(CheckedCenterConstants.checkedArrayList)
				resetDataForCenter(tempCheckedList)

				ChangeCenterUtils.saveCenterInfo()
			} else {
				//doNothing
			}


		}
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	fun receiveDeviceInternetStatus(event: DeviceConnectStatusEvent) {
		if (event.statusNormal) {
			hideNewNoInternet()
		} else {
			if (CheckedCenterConstants.checkedMapList.keys.size == 0) {
				//应该是显示状态,为了版本稳定，先不改变
				tvAddNewCenter.visibility = View.VISIBLE
			} else {
				if (null != CheckedCenterConstants.curSelectCenter && CheckedCenterConstants.curSelectCenter!!.device_ip.isEmpty()) {
					showNewNoInternet()
				} else {
					//doNothing
				}

			}

		}
	}

	/**
	 * 显示当前设备网络连接问题画面
	 */
	private fun showNewNoInternet() {
		flNewNoInternet.visibility = View.VISIBLE
	}

	/**
	 * 隐藏当前设备网络连接问题画面
	 */
	private fun hideNewNoInternet() {
		flNewNoInternet.visibility = View.GONE
	}

	private fun stopStartHeartService() {
		StartHeartServiceConstants.stopService = true
		StartHeartServiceConstants.isRunning = false
		MyApplication.sInstance.stopService(Intent(MyApplication.sInstance, StartHeartService::class.java))
	}


}

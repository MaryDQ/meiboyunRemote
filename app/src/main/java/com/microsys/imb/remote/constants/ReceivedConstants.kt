package com.microsys.imb.remote.constants

import com.microsys.imb.remote.bean.*
import com.microsys.imb.remote.event.TransAppInfoEvent

class ReceivedConstants {
	companion object {
		//首页设备列表的数据bean
		var transDeviceListEvent: TransAppInfoEvent? = null
		//首页联络中心数据列表
		var centerListJson = ""
		//网络直播平台的相关信息bean
		var liveInfoBean: GetLiveInfoRespBean? = null
		//获取通话的网络状态
		var commStatusBean: GetCommStatusRespBean? = null
		//挂断通话返回的结果
		var commHangUpBean: CommHangUpRespBean? = null
		//切换静音返回的结果
		var commMuteBean: CommMuteRespBean? = null
		//切换发言状态返回的结果
		var commPttBean: CommPttRespBean? = null
		//获取远程会议信息
		var commInfoBean: GetCommInfoRespBean? = null
		//gis地图相关信息
//        var gisInfoBean:
	}
}
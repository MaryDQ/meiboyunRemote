package com.microsys.imb.remote.constants

import com.blankj.utilcode.util.NetworkUtils
import com.microsys.imb.remote.udps.socket.UDPSocket

/**
 * 请求UDP数据需要传递的一些JSON格式
 * @author mlx
 * @date 2019.0708
 *
 */
class RequestModel {

	companion object {
		//startHeartRequestId，20000开始是启动时心跳专用包
		var startHeartRequestId = 20_000

		/**
		 * 获得请求连接需要的json,启动心跳专用
		 */
		fun getStartHeartConnectRequestJson(ip: String, port: String): String {
			val jsonResult = "{\n" +
					"\t\"id\": \"$startHeartRequestId\",\n" +
					"\t\"type\": \"connect\",\n" +
					"\t\"remote_ip\": \"$ip\",\n" +
					"\t\"remote_port\": \"$port\"\n" +
					"\n" +
					"}\n"
			startHeartRequestId++
			return jsonResult
		}


		//heartRequestID,10000开始的是心跳专用包
		var heartRequestId = 10000

		/**
		 * 获得请求连接需要的json,心跳专用
		 */
		fun getHeartConnectRequestJson(ip: String, port: String): String {
			val jsonResult = "{\n" +
					"\t\"id\": \"$heartRequestId\",\n" +
					"\t\"type\": \"connect\",\n" +
					"\t\"remote_ip\": \"$ip\",\n" +
					"\t\"remote_port\": \"$port\"\n" +
					"\n" +
					"}\n"
			heartRequestId++
			return jsonResult
		}


		//requestId,自增长
		private var connectRequestId = 1

		/**
		 * 获得请求连接需要的json
		 */
		fun getConnectRequestJson(ip: String, port: String): String {
			val jsonResult = "{\n" +
					"\t\"id\": \"$connectRequestId\",\n" +
					"\t\"type\": \"connect\",\n" +
					"\t\"remote_ip\": \"$ip\",\n" +
					"\t\"remote_port\": \"$port\"\n" +
					"\n" +
					"}\n"
			connectRequestId++
			return jsonResult
		}

		private var bindRequestId = 1

		/**
		 * 获得绑定连接需要的json
		 */
		fun getBindRequestJson(): String {
			val jsonResult = "{\n" +
					"\t\"id\": \"$bindRequestId\",\n" +
					"\t\"type\": \"bind\",\n" +
					"\t\"remote_ip\": \"${NetworkUtils.getIPAddress(true)}\",\n" +
					"\t\"remote_port\": \"${UDPSocket.getRECEIVE_PORT()}" +
					"\"\n" +
					"\n" +
					"}\n"
			bindRequestId++
			return jsonResult
		}

		private var checkRequestId = 1
		/**
		 * 获得请求验证需要的json
		 */
		fun getCheckRequestJson(pwd: String): String {
			val jsonResult = "{\n" +
					"\t\"id\": \"$checkRequestId\",\n" +
					"\t\"type\": \"check\",\n" +
					"\t\"remote_ip\": \"${NetworkUtils.getIPAddress(true)}\",\n" +
					"\t\"remote_port\": \"${UDPSocket.getRECEIVE_PORT()}\",\n" +
					"\t\"check_num\": \"$pwd\"\n" +
					"}\n"
			checkRequestId++
			return jsonResult
		}

		private var getAppInfoRequestId = 1

		/**
		 * 获得请求appInfo需要的json
		 */
		fun getAppInfoRequestJson(): String {
			val jsonResult = "{\n" +
					"\t\"id\": \"$getAppInfoRequestId\",\n" +
					"\t\"type\": \"get_app_info\",\n" +
					"\t\"remote_ip\": \"${NetworkUtils.getIPAddress(true)}\",\n" +
					"\t\"remote_port\": \"${UDPSocket.getRECEIVE_PORT()}" +
					"\"\n" +
					"\n" +
					"}\n"
			getAppInfoRequestId++
			return jsonResult
		}

		private var getLiveInfoRequestId = 1

		/**
		 * 获得请求直播信息需要的json
		 */
		fun getLiveInfoRequestJson(): String {
			val jsonResult = "{\n" +
					"\t\"id\": \"$getLiveInfoRequestId\",\n" +
					"\t\"type\": \"get_live_info\",\n" +
					"\t\"remote_ip\": \"${NetworkUtils.getIPAddress(true)}\",\n" +
					"\t\"remote_port\": \"${UDPSocket.getRECEIVE_PORT()}" +
					"\"\n" +
					"}"
			getLiveInfoRequestId++
			return jsonResult
		}

		private var getCommInfoRequestId = 1

		/**
		 * 获得请求远程会议需要的json
		 */
		fun getCommInfoRequestJson(): String {
			val jsonResult = "{\n" +
					"\t\"id\": \"$getCommInfoRequestId\",\n" +
					"\t\"type\": \"get_comm_info\",\n" +
					"\t\"remote_ip\": \"${NetworkUtils.getIPAddress(true)}\",\n" +
					"\t\"remote_port\": \"${UDPSocket.getRECEIVE_PORT()}" +
					"\"\n" +
					"\n" +
					"}\n"
			getCommInfoRequestId++
			return jsonResult
		}


		private var getCommStatusRequestId = 1

		/**
		 * 获得请求远程会议状态需要的json
		 */
		fun getCommStatusRequestJson(): String {
			val jsonResult = "{\n" +
					"\t\"id\": \"$getCommStatusRequestId\",\n" +
					"\t\"type\": \"comm_status\",\n" +
					"\t\"remote_ip\": \"${NetworkUtils.getIPAddress(true)}\",\n" +
					"\t\"device_id\": \"${CheckedCenterConstants.curSelectCenter?.device_id}\",\n" +
					"\t\"remote_port\": \"${UDPSocket.getRECEIVE_PORT()}" +
					"\"\n" +
					"}\n"
			getCommStatusRequestId++
			return jsonResult
		}

		private var startLiveRequestId = 1

		/**
		 * 获得请求开始直播需要的json
		 */
		fun getStartLiveRequestJson(): String {
			val jsonResult = "{\n" +
					"\t\"id\": \"$startLiveRequestId\",\n" +
					"\t\"type\": \"start_live\",\n" +
					"\t\"remote_ip\": \"${NetworkUtils.getIPAddress(true)}\",\n" +
					"\t\"remote_port\": \"${UDPSocket.getRECEIVE_PORT()}" +
					"\"\n" +
					"\n" +
					"}\n"
			startLiveRequestId++
			return jsonResult
		}

		private var stopLiveRequestId = 1

		/**
		 * 获得请求关闭直播需要的json
		 */
		fun getStopLiveRequestJson(): String {
			val jsonResult = "{\n" +
					"\t\"id\": \"$stopLiveRequestId\",\n" +
					"\t\"type\": \"stop_live\",\n" +
					"\t\"remote_ip\": \"${NetworkUtils.getIPAddress(true)}\",\n" +
					"\t\"remote_port\": \"${UDPSocket.getRECEIVE_PORT()}" +
					"\"\n" +
					"\n" +
					"}\n"
			stopLiveRequestId++
			return jsonResult
		}

		private var commCallOutRequestId = 1
		/**
		 * 获得请求往外呼出需要的json
		 * @param callType 0：语音通话 1：视频通话
		 */
		fun getCommCallOutRequestJson(callType: String, callNumberArray: String): String {
			val jsonResult = "\"id\": \"$commCallOutRequestId\",\n" +
					"\t\"type\": \"comm_call_out\",\n" +
					"\"call_type\": \"$callType\",\n" +
					"\"call_tel\": [$callNumberArray],\n" +
					"\t\"remote_ip\": \"${NetworkUtils.getIPAddress(true)}\",\n" +
					"\t\"remote_port\": \"${UDPSocket.getRECEIVE_PORT()}" +
					"\"\n"
			commCallOutRequestId++
			return jsonResult
		}


		private var commMuteRequestId = 1

		/**
		 * 获得请求通话静音需要的json
		 */
		fun getCommMuteRequestJson(isMute: Boolean): String {
			val jsonResult = "{\n" +
					"\t\"id\": \"$commMuteRequestId\",\n" +
					"\t\"type\": \"comm_mute\",\n" +
					"\t\"remote_ip\": \"${NetworkUtils.getIPAddress(true)}\",\n" +
					"\t\"remote_port\": \"${UDPSocket.getRECEIVE_PORT()}" +
					"\",\n" +
					"\t\"action\": \"$isMute\"\n" +
					"}\n"
			commMuteRequestId++
			return jsonResult
		}

		private var commHangUpRequestId = 1

		/**
		 * 获得请求挂断电话需要的json
		 */
		fun getCommHangUpRequestJson(): String {
			val jsonResult = "{\n" +
					"\t\"id\": \"$commHangUpRequestId\",\n" +
					"\t\"type\": \"comm_hangup\",\n" +
					"\t\"remote_ip\": \"${NetworkUtils.getIPAddress(true)}\",\n" +
					"\t\"remote_port\": \"${UDPSocket.getRECEIVE_PORT()}" +
					"\"\n" +
					"}\n"
			commHangUpRequestId++
			return jsonResult
		}

		private var commAgreeCallRequestId = 1

		/**
		 * 获得请求挂断电话需要的json
		 */
		fun getCommAgreeCallRequestJson(): String {
			val jsonResult = "{\n" +
					"\t\"id\": \"$commAgreeCallRequestId\",\n" +
					"\t\"type\": \"comm_accept\",\n" +
					"\t\"remote_ip\": \"${NetworkUtils.getIPAddress(true)}\",\n" +
					"\t\"remote_port\": \"${UDPSocket.getRECEIVE_PORT()}" +
					"\"\n" +
					"}\n"
			commAgreeCallRequestId++
			return jsonResult
		}

		private var commPttRequestId = 1

		/**
		 * 获得请求 发言/停止发言 需要的json
		 */
		fun getCommPttRequestJson(needPtt: Boolean): String {
			val jsonResult = "{\n" +
					"\t\"id\": \"$commPttRequestId\",\n" +
					"\t\"type\": \"comm_ptt\",\n" +
					"\t\"remote_ip\": \"${NetworkUtils.getIPAddress(true)}\",\n" +
					"\t\"remote_port\": \"${UDPSocket.getRECEIVE_PORT()}" +
					"\",\n" +
					"\t\"action\": \"$needPtt\"\n" +
					"}\n"
			stopLiveRequestId++
			return jsonResult
		}
	}
}
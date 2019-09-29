package com.microsys.imb.remote.utils

import android.content.Context
import android.os.Environment
import com.google.gson.Gson
import com.lxj.xpopup.XPopup
import com.microsys.imb.remote.BuildConfig
import com.microsys.imb.remote.bean.UpdateResultBean
import com.microsys.imb.remote.constants.HttpConstants
import com.microsys.imb.remote.widgets.dialog.UpdateDialog
import com.sunday.common.logger.Logger
import com.sunday.common.net.YRequest
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.File
import java.io.IOException

class CheckVersionUtil {
	companion object {
		/**
		 * 检查是否有更新，有的话，弹窗提示
		 */
		fun checkVersion(context: Context?) {
			val file = File(Environment.getExternalStorageDirectory().absolutePath + "/Download/meiboyunRemote.apk")
			if (file.exists()) {
				file.delete()
			}
			val map = HashMap<String, String>()
			map["appName"] = "remote"
			map["type"] = "android"
			map["version"] = BuildConfig.VERSION_NAME
			Thread {
				YRequest.getInstance()
					.get(HttpConstants.getCheckVersionUrl(), map, object : Callback {
						override fun onFailure(call: Call, e: IOException) {
							Logger.e(e.message)
						}

						override fun onResponse(call: Call, response: Response) {
							val result = response.body()?.string()
							Logger.json(result)
							val updateResultBean = try {
								Gson().fromJson<UpdateResultBean>(result, UpdateResultBean::class.java)
							} catch (e: Exception) {
								return
							}
							if (200 == updateResultBean.code) {
								if (updateResultBean.data == null || updateResultBean.data.status == 0) {
									//doNothing,无需更新
								} else {
									val couldDismiss = updateResultBean.data.status == 1
									XPopup.Builder(context)
										.dismissOnBackPressed(couldDismiss)
										.dismissOnTouchOutside(couldDismiss)
										.asCustom(UpdateDialog(context!!, updateResultBean))
										.show()
								}
							} else {
								//doNothing
							}
						}

					})
			}.start()

		}
	}
}
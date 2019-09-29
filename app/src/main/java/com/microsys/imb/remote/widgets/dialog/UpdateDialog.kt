package com.microsys.imb.remote.widgets.dialog

import android.content.Context
import android.view.View
import android.widget.TextView
import com.lxj.xpopup.core.CenterPopupView
import com.microsys.imb.remote.R
import com.microsys.imb.remote.bean.UpdateResultBean
import com.microsys.imb.remote.constants.DialogConstants
import com.microsys.imb.remote.service.DownloadService
import kotlinx.android.synthetic.main.dialog_update.view.*


class UpdateDialog(context: Context, private val updateBean: UpdateResultBean) :
	CenterPopupView(context) {

	override fun getImplLayoutId(): Int {
		return R.layout.dialog_update
	}

	private var tvUpdateInfo: TextView? = null

	override fun onCreate() {
		super.onCreate()
		initData()
	}

	private var isRequest: Boolean = false


	override fun onDismiss() {
		DialogConstants.isDialogShowing = false
		super.onDismiss()
	}

	private fun initData() {
		DialogConstants.isDialogShowing = true
		isRequest = when (updateBean.data.status) {
			1 -> false
			2 -> true
			else ->
				false
		}
		tvVersionCode.text = updateBean.data.version
		tvUpdateInfo = findViewById(R.id.tvUpdateInfo)
		tvUpdateInfo?.text = updateBean.data.content

		llCancelUpdate.visibility = if (isRequest) {
			View.GONE
		} else {
			View.VISIBLE
		}

		/**
		 *  卡丁车 https://imtt.dd.qq.com/16891/apk/31ABBAD8FD859AC51D517CD734621B60.apk?fsname=com.tencent.tmgp.WePop_1.0.6_669.apk&csr=1bbd
		 * 	随笔记 https://imtt.dd.qq.com/16891/12B04E442E16CADEE7D91DC7EDFF9F95.apk?fsname=org.dayup.gnotes_1.8.3.9_1839.apk&csr=1bbd
		 *	爱消除 https://imtt.dd.qq.com/16891/BBBD9FEC1F6E1518861B3383A1B84C71.apk?fsname=com.tencent.peng_1.76.0.0Build20_111105.apk&csr=1bbd
		 */

		val downloadUrl =
			updateBean.data.url
		llUpdateNow.setOnClickListener {
			DownloadService.startActionDownload(context, downloadUrl, "meiboyunRemote.apk")
			dismiss()
		}

		llCancelUpdate.setOnClickListener {
			dismiss()
		}
	}


}
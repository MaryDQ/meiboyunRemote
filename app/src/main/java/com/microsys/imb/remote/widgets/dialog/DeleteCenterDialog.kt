package com.microsys.imb.remote.widgets.dialog

import android.annotation.SuppressLint
import android.content.Context
import android.widget.TextView
import com.lxj.xpopup.core.CenterPopupView
import com.microsys.imb.remote.R
import com.microsys.imb.remote.bean.ConnectResponseBean
import com.microsys.imb.remote.event.DeleteOneSavedCenterEvent
import org.greenrobot.eventbus.EventBus

class DeleteCenterDialog(context: Context, var selectCenter: ConnectResponseBean, var position: Int) :
	CenterPopupView(context) {
	override fun getImplLayoutId(): Int {
		return R.layout.dialog_delete_center
	}

	private var tvDelete: TextView? = null
	private var tvCancel: TextView? = null
	private var tvTitle: TextView? = null


	override fun onCreate() {
		super.onCreate()
		initData()
	}

	@SuppressLint("SetTextI18n")
	private fun initData() {
		tvDelete = findViewById(R.id.tvDelete)
		tvCancel = findViewById(R.id.tvCancel)
		tvTitle = findViewById(R.id.tvDeleteCenterTitle)

		tvTitle?.text = "是否删除云上会面 ${selectCenter.account_name}"

		tvCancel?.setOnClickListener { dismiss() }

		tvDelete?.setOnClickListener {
			EventBus.getDefault().post(DeleteOneSavedCenterEvent(true, selectCenter, position))
			dismiss()
		}
	}
}
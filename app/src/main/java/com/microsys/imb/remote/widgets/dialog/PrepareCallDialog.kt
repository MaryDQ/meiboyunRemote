package com.microsys.imb.remote.widgets.dialog

import android.content.Context
import com.lxj.xpopup.core.BottomPopupView
import com.microsys.imb.remote.R
import com.sunday.common.utils.ToastUtils
import kotlinx.android.synthetic.main.dialog_prepare_call.view.*

class PrepareCallDialog(context:Context) : BottomPopupView(context) {

    override fun getImplLayoutId(): Int {
        return R.layout.dialog_prepare_call
    }

    override fun onCreate() {
        super.onCreate()
        initData()
    }

    private fun initData() {
        btnCancel.setOnClickListener {
            dismiss()
        }
        btnAudioCall.setOnClickListener {
            ToastUtils.show("语音通话")
        }
        btnVideoCall.setOnClickListener {
            ToastUtils.show("视频通话")
        }
    }
}
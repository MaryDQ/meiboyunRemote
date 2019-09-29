package com.microsys.imb.remote.utils

import android.content.Context
import android.view.Gravity
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import com.microsys.imb.remote.R
import com.sunday.common.utils.DeviceUtils
import com.sunday.common.widgets.NumberKeyWordView

class DialogUtils {
    companion object {

        fun showNumKeyBoard(
            context: Context?,
            callBack: MCallBack?
        ) {
            val alertDialog = AlertDialog.Builder(context!!, R.style.dialog_default_style).create()
            alertDialog.show()//显示对话框
            val window = alertDialog.window//对话框窗口
            window!!.setGravity(Gravity.BOTTOM)//设置对话框显示在屏幕中间
            window.setWindowAnimations(R.style.dialog_bottom_anim_style)//添加动画
            window.setContentView(R.layout.dialog_num_key_board)//设置对话框的布局文件
            alertDialog.setCanceledOnTouchOutside(true)

            val params = window.attributes
            params.height = (DeviceUtils.getDisplay(context).heightPixels * 0.61).toInt()
            params.width = WindowManager.LayoutParams.MATCH_PARENT
            window.attributes = params

            var curTel = ""

            val numberKeyWordView: NumberKeyWordView = window.findViewById(R.id.numberKeyWordView)
            val tvInputNum: EditText = window.findViewById(R.id.tvInputNum)
            val ivNumDelete: ImageView = window.findViewById(R.id.ivNumDelete)
            val ivCallAudio: ImageView = window.findViewById(R.id.ivCallAudio)
            val ivCallVideo: ImageView = window.findViewById(R.id.ivCallVideo)

            tvInputNum.requestFocus()

            numberKeyWordView.setOnClickNumListener { num ->
                if (curTel.length < 6) {
                    curTel += num
                    tvInputNum.setText(curTel)
                }
            }

            ivNumDelete.setOnClickListener {
                if (curTel.isNotEmpty()) {
                    curTel = curTel.substring(0, curTel.length - 1)
                    tvInputNum.setText(curTel)
                }
            }

            ivNumDelete.setOnLongClickListener {
                curTel = ""
                tvInputNum.setText(curTel)
                false
            }

            ivCallAudio.setOnClickListener {
                callBack?.onCallBackDispatch("audio", curTel)
                alertDialog.dismiss()
            }

            ivCallVideo.setOnClickListener {
                callBack?.onCallBackDispatch("video", curTel)
                alertDialog.dismiss()
            }

        }

    }


    interface MCallBack {
        /**
         * dialog点击回调
         */
        fun onCallBackDispatch(type: String, text: String)
    }
}
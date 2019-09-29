package com.microsys.imb.remote.event

import com.google.gson.Gson
import com.microsys.imb.remote.bean.ControlLiveRespBean

class ControlLiveInfoEvent(callback: Boolean, json: String) {
    var controlLiveRespBean: ControlLiveRespBean = Gson().fromJson(json, ControlLiveRespBean::class.java)
    var mCallback = callback
}
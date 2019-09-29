package com.microsys.imb.remote.event

import com.google.gson.Gson
import com.microsys.imb.remote.bean.GetAppInfoResponseBean

class TransAppInfoEvent(json: String) {
    var appInfoBean: GetAppInfoResponseBean? = Gson().fromJson(json, GetAppInfoResponseBean::class.java)
}
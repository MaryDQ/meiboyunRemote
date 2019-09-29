package com.microsys.imb.remote.app

import androidx.multidex.MultiDexApplication
import com.tencent.mm.opensdk.openapi.IWXAPI
import com.tencent.mm.opensdk.openapi.WXAPIFactory

class MyApplication : MultiDexApplication() {
    /**
     * 这个对象是专门用来向微信发送数据的一个重要接口,使用强引用持有,所有的信息发送都是基于这个对象的
     */
    var api: IWXAPI? = null
    companion object {
        @JvmStatic
        lateinit var sInstance: MyApplication
            private set
    }

    override fun onCreate() {
        super.onCreate()
        sInstance = this
        init()
    }

    private fun init() {
        //注册微信
        api = WXAPIFactory.createWXAPI(sInstance, "微信appId", true)
    }
}
package com.microsys.imb.remote.event

import com.microsys.imb.remote.bean.GetCommInfoRespBean

class CommInfoResultEvent(var update: Boolean, var bean: GetCommInfoRespBean) {}
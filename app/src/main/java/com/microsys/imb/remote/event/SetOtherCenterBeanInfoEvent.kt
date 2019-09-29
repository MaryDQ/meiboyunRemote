package com.microsys.imb.remote.event

import com.microsys.imb.remote.bean.ConnectResponseBean

class SetOtherCenterBeanInfoEvent(var update: Boolean, var bean: ConnectResponseBean) {}
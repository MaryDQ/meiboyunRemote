package com.microsys.imb.remote.event

import com.microsys.imb.remote.bean.ConnectResponseBean

class DeleteOneSavedCenterEvent(var delete: Boolean, var data: ConnectResponseBean, var position: Int) {

}
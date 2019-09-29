package com.microsys.imb.remote.event

class UpdateCenterEvent(json: String, update: Boolean) {
    val resultJson = json
    val isUpdate = update
}
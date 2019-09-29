package com.microsys.imb.remote.utils

import com.microsys.imb.remote.bean.Local
import com.microsys.imb.remote.bean.Subcontact
import com.microsys.imb.remote.bean.SubcontactX
import com.microsys.imb.remote.constants.TestConstants

class OrgPersonUtils {
	companion object {
		private const val ITEM_SELECT = 1
		private const val ITEM_NOT_SELECT = 0
		fun switchPersonStatus(data: Any) {
			when (data) {
				is Subcontact -> {
					if (data.itemStatus == ITEM_SELECT) {
						data.itemStatus = ITEM_NOT_SELECT
						TestConstants.removePersonFromSelectPersonSet(data)
					} else {
						data.itemStatus = ITEM_SELECT
						TestConstants.addPersonToSelectPersonSet(data)
					}
				}
				is SubcontactX -> {
					if (data.itemStatus == ITEM_SELECT) {
						data.itemStatus = ITEM_NOT_SELECT
						TestConstants.removePersonFromSelectPersonSet(data)
					} else {
						data.itemStatus = ITEM_SELECT
						TestConstants.addPersonToSelectPersonSet(data)
					}
				}
				is Local -> {
					if (data.itemStatus == ITEM_SELECT) {
						data.itemStatus = ITEM_NOT_SELECT
						TestConstants.removePersonFromSelectPersonSet(data)
					} else {
						data.itemStatus = ITEM_SELECT
						TestConstants.addPersonToSelectPersonSet(data)
					}
				}
			}
		}
	}

}
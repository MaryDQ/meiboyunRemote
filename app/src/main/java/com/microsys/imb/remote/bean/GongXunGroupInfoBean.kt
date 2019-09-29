package com.microsys.imb.remote.bean

data class GongXunGroupInfoBean(
	val local: List<Local>,
	val topdept: Topdept
)

data class Local(
	//1为选中，0未选中
	var itemStatus: Int,
	val deptid: Int,
	val name: String,
	val number: String,
	val online: Boolean
) {
	override fun hashCode(): Int {
		return number.toInt()
	}

	override fun equals(other: Any?): Boolean {
		if (this.hashCode() == other.hashCode()) {
			return true
		}
		return super.equals(other)
	}
}

data class Topdept(
	//1为选中，0未选中
	var itemStatus: Int,
	val id: Int,
	val name: String,
	val subdept: List<Subdept>,
	val supid: Int
)

data class Subdept(
	//1为选中，0未选中
	var itemStatus: Int,
	val id: Int,
	val name: String,
	val subcontact: List<Subcontact>,
	val subdept: List<SubdeptX>?,
	val supid: Int
)

data class SubdeptX(
	//1为选中，0未选中
	var itemStatus: Int,
	val id: Int,
	val name: String,
	val subcontact: List<SubcontactX>,
	val supid: Int
)

data class Subcontact(
	//1为选中，0未选中
	var itemStatus: Int,
	val deptid: Int,
	val name: String,
	val number: String,
	val online: Boolean
) {
	override fun hashCode(): Int {
		return number.toInt()
	}

	override fun equals(other: Any?): Boolean {
		if (this.hashCode() == other.hashCode()) {
			return true
		}
		return super.equals(other)
	}
}

data class SubcontactX(
	//1为选中，0未选中
	var itemStatus: Int,
	val deptid: Int,
	val name: String,
	val number: String,
	val online: Boolean
) {
	override fun hashCode(): Int {
		return number.toInt()
	}

	override fun equals(other: Any?): Boolean {
		if (this.hashCode() == other.hashCode()) {
			return true
		}
		return super.equals(other)
	}
}
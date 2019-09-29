package com.microsys.imb.remote.bean

data class GongXunDetailGroupInfoBean(
    val group: List<Group>
)

data class Group(
    val contactsList: List<Contacts>,
    val name: String,
    val number: String,
    val shielded: Boolean,
    val type: Int
)

data class Contacts(
    val deptid: Int,
    val name: String,
    val number: String,
    val online: Boolean
)
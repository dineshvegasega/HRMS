package com.vegasega.hrms.models

data class ItemNomineeData(
    val id: Int,
    val member_id: Int,
    val nominee: Array<HashMap<String, String>>
)
package com.vegasega.hrms.models.profile

data class Role(
    val created_at: String,
    val id: Int,
    val is_super_user: Int,
    val name: String,
    val updated_at: String
)
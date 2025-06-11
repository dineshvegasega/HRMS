package com.vegasega.hrms.models.leave.viewLeave

data class User(
    val created_at: String,
    val email: String,
    val email_verified_at: Any,
    val id: Int,
    val is_active: Int,
    val name: String,
    val role_id: Int,
    val updated_at: String
)
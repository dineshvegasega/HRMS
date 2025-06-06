package com.vegasega.hrms.models.profile

data class Profile(
    val created_at: String,
    val email: String,
    val email_verified_at: String,
    val employee: Employee,
    val id: Int,
    val is_active: Int,
    val name: String,
    val role: Role,
    val role_id: Int,
    val updated_at: String
)
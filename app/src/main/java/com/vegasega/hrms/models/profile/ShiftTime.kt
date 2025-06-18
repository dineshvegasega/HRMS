package com.vegasega.hrms.models.profile

data class ShiftTime(
    val created_at: String,
    val description: String,
    val end_time: String,
    val id: Int,
    val name: String,
    val start_time: String,
    val updated_at: String
)
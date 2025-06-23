package com.vegasega.hrms.models.punch.punchIn

data class Attendance(
    val attendance_date: String,
    val created_at: String,
    val id: Int,
    val punch_in_time: String,
    val status: String,
    val updated_at: String,
    val user_id: Int
)
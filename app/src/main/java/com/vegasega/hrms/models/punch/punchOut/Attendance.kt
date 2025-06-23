package com.vegasega.hrms.models.punch.punchOut

data class Attendance(
    val attendance_date: String,
    val created_at: String,
    val id: Int,
    val punch_in_time: String,
    val punch_out_time: String,
    val status: String,
    val total_break_minutes: Int,
    val total_work_minutes: Int,
    val updated_at: String,
    val user_id: Int
)
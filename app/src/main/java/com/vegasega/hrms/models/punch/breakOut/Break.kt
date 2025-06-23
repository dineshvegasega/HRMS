package com.vegasega.hrms.models.punch.breakOut

data class Break(
    val break_end_time: String,
    val break_start_time: String,
    val created_at: String,
    val duration_minutes: Int,
    val employee_attendance_id: Int,
    val id: Int,
    val updated_at: String
)
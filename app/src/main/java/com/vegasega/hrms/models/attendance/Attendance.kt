package com.vegasega.hrms.models.attendance

data class Attendance(
    val attendance_time_id: Int,
    val attendance_type_id: Int,
    val created_at: String,
    val employee_id: Int,
    val id: Int,
    val message: String,
    val updated_at: String
)
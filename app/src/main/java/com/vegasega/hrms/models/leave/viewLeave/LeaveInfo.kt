package com.vegasega.hrms.models.leave.viewLeave

data class LeaveInfo(
    val casual_leave_quota: Int,
    val created_at: String,
    val employee_id: Int,
    val id: Int,
    val leaves_quota: Int,
    val lop: Int,
    val sick_leave_quota: Int,
    val updated_at: String,
    val used_leaves: Int
)
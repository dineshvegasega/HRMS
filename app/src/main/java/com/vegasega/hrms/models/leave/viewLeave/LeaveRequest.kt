package com.vegasega.hrms.models.leave.viewLeave

import kotlinx.parcelize.RawValue

data class LeaveRequest(
    val checked_by: @RawValue Any,
    val comment: String,
    val created_at: String,
    val employee: Employee,
    val employee_id: Int,
    val from: String,
    val id: Int,
    val message: String,
    val status: String,
    val to: String,
    val updated_at: String
)
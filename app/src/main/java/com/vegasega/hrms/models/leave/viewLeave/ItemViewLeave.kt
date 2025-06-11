package com.vegasega.hrms.models.leave.viewLeave

data class ItemViewLeave(
    val leave_info: LeaveInfo,
    val leave_request: LeaveRequest,
    val total_days: Int
)
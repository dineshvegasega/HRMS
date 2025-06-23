package com.vegasega.hrms.models.punch.punchStatus

data class ItemPunchStatus(
    val active_break: Break,
    val attendance_today: AttendanceToday,
    val message: String,
    val status: String
)
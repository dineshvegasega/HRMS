package com.vegasega.hrms.models.leave.viewLeave

data class Employee(
    val created_at: String,
    val department: Department,
    val department_id: Int,
    val employee_detail: EmployeeDetail,
    val end_of_contract: Any,
    val head_of: Any,
    val id: Int,
    val is_active: Int,
    val name: String,
    val position: Position,
    val position_id: Int,
    val start_of_contract: Any,
    val updated_at: String,
    val user: User,
    val user_id: Int
)
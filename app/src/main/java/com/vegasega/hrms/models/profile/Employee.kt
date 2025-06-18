package com.vegasega.hrms.models.profile

import com.vegasega.hrms.models.profile.AttendanceTrackingType
import com.vegasega.hrms.models.profile.Department
import com.vegasega.hrms.models.profile.Division
import com.vegasega.hrms.models.profile.EmployeeDetail
import com.vegasega.hrms.models.profile.EmployeeType
import com.vegasega.hrms.models.profile.Position
import com.vegasega.hrms.models.profile.ShiftTime
import com.vegasega.hrms.models.profile.WeekOff
import com.vegasega.hrms.models.profile.WorkType
import com.vegasega.hrms.models.score.UserX

data class Employee(
    val attendance_tracking_type: AttendanceTrackingType,
    val attendance_tracking_type_id: Int,
    val contract_end_date: String,
    val contract_start_date: String,
    val created_at: String,
    val date_of_joining: String,
    val department: Department,
    val department_id: Int,
    val division: Division,
    val division_id: Int,
    val employee_detail: EmployeeDetail,
    val employee_type: EmployeeType,
    val employee_type_id: Int,
    val end_of_contract: String,
    val head_of: String,
    val id: Int,
    val is_active: Boolean,
    val name: String,
    val position: Position,
    val position_id: Int,
    val shift_time: ShiftTime,
    val shift_time_id: Int,
    val start_of_contract: String,
    val updated_at: String,
    val user: User,
    val user_id: Int,
    val week_off: WeekOff,
    val week_off_id: Int,
    val work_type: WorkType,
    val work_type_id: Int
)
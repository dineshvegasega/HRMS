package com.vegasega.hrms.models.score

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class ScoredBy(
    val created_at: String,
    val department: @RawValue Department,
    val department_id: Int,
    val employee_detail: @RawValue EmployeeDetailX,
    val end_of_contract: String,
    val head_of: String,
    val id: @RawValue Int,
    val is_active: Int,
    val name: String,
    val position: @RawValue Position,
    val position_id: Int,
    val start_of_contract: String,
    val updated_at: String,
    val user: @RawValue UserX,
    val user_id: Int
): Parcelable{
    override fun hashCode(): Int {
        val result = id.hashCode()
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ScoredBy

        if (department_id != other.department_id) return false
        if (id != other.id) return false
        if (is_active != other.is_active) return false
        if (position_id != other.position_id) return false
        if (user_id != other.user_id) return false
        if (created_at != other.created_at) return false
        if (department != other.department) return false
        if (employee_detail != other.employee_detail) return false
        if (end_of_contract != other.end_of_contract) return false
        if (head_of != other.head_of) return false
        if (name != other.name) return false
        if (position != other.position) return false
        if (start_of_contract != other.start_of_contract) return false
        if (updated_at != other.updated_at) return false
        if (user != other.user) return false

        return true
    }
}
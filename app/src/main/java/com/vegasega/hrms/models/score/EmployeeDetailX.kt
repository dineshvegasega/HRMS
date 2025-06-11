package com.vegasega.hrms.models.score

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class EmployeeDetailX(
    val address: String,
    val birth_place: String,
    val blood_group: String,
    val country_of_birth: String,
    val created_at: String,
    val cv: String,
    val date_of_birth: String,
    val e_mobile: String,
    val email: String,
    val employee_id: Int,
    val father_name: String,
    val gender: String,
    val gpa: String,
    val id: Int,
    val identity_number: String,
    val last_education: String,
    val marriage_anniversary: String,
    val mother_name: String,
    val name: String,
    val nationality: String,
    val p_email: String,
    val pan_number: String,
    val permanent_address: String,
    val phone: String,
    val photo: String,
    val previous_employee_details: String,
    val spouse_name: String,
    val uan_no: String,
    val updated_at: String,
    val work_experience_in_years: Int
): Parcelable{
    override fun hashCode(): Int {
        val result = id.hashCode()
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as EmployeeDetailX

        if (employee_id != other.employee_id) return false
        if (id != other.id) return false
        if (work_experience_in_years != other.work_experience_in_years) return false
        if (address != other.address) return false
        if (birth_place != other.birth_place) return false
        if (blood_group != other.blood_group) return false
        if (country_of_birth != other.country_of_birth) return false
        if (created_at != other.created_at) return false
        if (cv != other.cv) return false
        if (date_of_birth != other.date_of_birth) return false
        if (e_mobile != other.e_mobile) return false
        if (email != other.email) return false
        if (father_name != other.father_name) return false
        if (gender != other.gender) return false
        if (gpa != other.gpa) return false
        if (identity_number != other.identity_number) return false
        if (last_education != other.last_education) return false
        if (marriage_anniversary != other.marriage_anniversary) return false
        if (mother_name != other.mother_name) return false
        if (name != other.name) return false
        if (nationality != other.nationality) return false
        if (p_email != other.p_email) return false
        if (pan_number != other.pan_number) return false
        if (permanent_address != other.permanent_address) return false
        if (phone != other.phone) return false
        if (photo != other.photo) return false
        if (previous_employee_details != other.previous_employee_details) return false
        if (spouse_name != other.spouse_name) return false
        if (uan_no != other.uan_no) return false
        if (updated_at != other.updated_at) return false

        return true
    }
}
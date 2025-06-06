package com.vegasega.hrms.models.dashboard

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class EmployeesBirthdayThisMonth(
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
): Parcelable
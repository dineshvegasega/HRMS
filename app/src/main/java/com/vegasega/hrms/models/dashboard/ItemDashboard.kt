package com.vegasega.hrms.models.dashboard
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class ItemDashboard(
    val announcements: @RawValue List<Announcement> = emptyList(),
    val employees_birthday_this_month: @RawValue List<EmployeesBirthdayThisMonth> = emptyList(),
    val employees_count: Int,
    val employees_marriage_anniversary_this_month: @RawValue List<EmployeesMarriageAnniversaryThisMonth> = emptyList(),
    val has_attendance_today: Boolean,
    val holidays: @RawValue List<Holiday> = emptyList(),
    val recruitment_candidates_count: Int,
    val total_login_time_today: String
): Parcelable
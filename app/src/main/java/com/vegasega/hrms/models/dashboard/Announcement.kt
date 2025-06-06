package com.vegasega.hrms.models.dashboard

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Announcement(
    val attachment: String,
    val created_at: String,
    val created_by: Int,
    val department_id: Int,
    val description: String,
    val id: Int,
    val title: String,
    val updated_at: String
): Parcelable
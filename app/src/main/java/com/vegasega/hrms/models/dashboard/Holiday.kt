package com.vegasega.hrms.models.dashboard

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Holiday(
    val created_at: String,
    val date: String,
    val id: Int,
    val name: String,
    val updated_at: String
): Parcelable
package com.vegasega.hrms.models.leave

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class Data(
    val checked_by: @RawValue Any,
    val comment: String,
    val created_at: String,
    val employee_id: Int,
    val from: String,
    val id: Int,
    val message: String,
    val status: String,
    val to: String,
    val updated_at: String
) : Parcelable
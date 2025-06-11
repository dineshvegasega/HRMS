package com.vegasega.hrms.models.leave

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class ItemLeaveRequestPost(
    val `data`: @RawValue Data ?= null,
    val message: String,
): Parcelable
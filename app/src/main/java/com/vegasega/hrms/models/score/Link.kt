package com.vegasega.hrms.models.score

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Link(
    val active: Boolean,
    val label: String,
    val url: String
): Parcelable{
    override fun hashCode(): Int {
        val result = label.hashCode()
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Link

        if (active != other.active) return false
        if (label != other.label) return false
        if (url != other.url) return false

        return true
    }
}
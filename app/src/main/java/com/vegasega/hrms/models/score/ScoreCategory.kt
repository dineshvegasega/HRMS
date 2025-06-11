package com.vegasega.hrms.models.score

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ScoreCategory(
    val created_at: String,
    val id: Int,
    val name: String,
    val updated_at: String
): Parcelable{
    override fun hashCode(): Int {
        val result = id.hashCode()
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ScoreCategory

        if (id != other.id) return false
        if (created_at != other.created_at) return false
        if (name != other.name) return false
        if (updated_at != other.updated_at) return false

        return true
    }
}
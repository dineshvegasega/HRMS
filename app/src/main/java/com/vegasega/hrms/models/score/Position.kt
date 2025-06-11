package com.vegasega.hrms.models.score

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Position(
    val created_at: String,
    val description: String,
    val id: Int,
    val min_year_exp_required: Int,
    val name: String,
    val open_for_recruitment: Int,
    val salary: Int,
    val updated_at: String
): Parcelable{
    override fun hashCode(): Int {
        val result = id.hashCode()
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Position

        if (id != other.id) return false
        if (min_year_exp_required != other.min_year_exp_required) return false
        if (open_for_recruitment != other.open_for_recruitment) return false
        if (salary != other.salary) return false
        if (created_at != other.created_at) return false
        if (description != other.description) return false
        if (name != other.name) return false
        if (updated_at != other.updated_at) return false

        return true
    }
}
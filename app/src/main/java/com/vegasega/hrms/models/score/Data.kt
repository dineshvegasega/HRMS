package com.vegasega.hrms.models.score

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class Data(
    val created_at: String,
    val employee: @RawValue Employee,
    val employee_id: Int,
    val group_id: Int,
    val id: Int,
    val score: Int,
    val score_category: @RawValue ScoreCategory,
    val score_category_id: Int,
    val scored_by: @RawValue ScoredBy,
    val updated_at: String
): Parcelable{
    override fun hashCode(): Int {
        val result = id.hashCode()
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Data

        if (employee_id != other.employee_id) return false
        if (group_id != other.group_id) return false
        if (id != other.id) return false
        if (score != other.score) return false
        if (score_category_id != other.score_category_id) return false
        if (created_at != other.created_at) return false
        if (employee != other.employee) return false
        if (score_category != other.score_category) return false
        if (scored_by != other.scored_by) return false
        if (updated_at != other.updated_at) return false

        return true
    }
}
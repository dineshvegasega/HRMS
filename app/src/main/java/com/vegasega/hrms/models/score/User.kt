package com.vegasega.hrms.models.score

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class User(
    val created_at: String,
    val email: String,
    val email_verified_at: String,
    val id: Int,
    val is_active: Int,
    val name: String,
    val role_id: Int,
    val updated_at: String
): Parcelable{
    override fun hashCode(): Int {
        val result = id.hashCode()
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as User

        if (id != other.id) return false
        if (is_active != other.is_active) return false
        if (role_id != other.role_id) return false
        if (created_at != other.created_at) return false
        if (email != other.email) return false
        if (email_verified_at != other.email_verified_at) return false
        if (name != other.name) return false
        if (updated_at != other.updated_at) return false

        return true
    }
}
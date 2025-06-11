package com.vegasega.hrms.models.score

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class ItemEmployeeScore(
    val current_page: Int,
    val `data`: @RawValue List<Data> = emptyList(),
    val first_page_url: String,
    val from: Int,
    val last_page: Int,
    val last_page_url: String,
    val links: @RawValue List<Link> = emptyList(),
    val next_page_url: String,
    val path: String,
    val per_page: Int,
    val prev_page_url: String,
    val to: Int,
    val total: Int
): Parcelable{
    override fun hashCode(): Int {
        val result = current_page.hashCode()
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ItemEmployeeScore

        if (current_page != other.current_page) return false
        if (from != other.from) return false
        if (last_page != other.last_page) return false
        if (per_page != other.per_page) return false
        if (to != other.to) return false
        if (total != other.total) return false
        if (`data` != other.`data`) return false
        if (first_page_url != other.first_page_url) return false
        if (last_page_url != other.last_page_url) return false
        if (links != other.links) return false
        if (next_page_url != other.next_page_url) return false
        if (path != other.path) return false
        if (prev_page_url != other.prev_page_url) return false

        return true
    }
}
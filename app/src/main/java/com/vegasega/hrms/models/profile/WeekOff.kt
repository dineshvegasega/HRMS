package com.vegasega.hrms.models.profile

data class WeekOff(
    val created_at: String,
    val description: String,
    val id: Int,
    val name: String,
    val saturday_off: Int,
    val second_fourth_saturday_off: Int,
    val sunday_off: Int,
    val updated_at: String
)
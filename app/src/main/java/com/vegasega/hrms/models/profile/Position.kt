package com.vegasega.hrms.models.profile

data class Position(
    val created_at: String,
    val description: String,
    val id: Int,
    val min_year_exp_required: Int,
    val name: String,
    val open_for_recruitment: Int,
    val salary: Int,
    val updated_at: String
)
package com.vegasega.hrms.models

data class ItemSubscription(
    val id: Int,
    val state_id: Int,
    val subscription_cost: Double = 0.00
)
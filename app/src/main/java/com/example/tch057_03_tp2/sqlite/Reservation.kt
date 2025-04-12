package com.example.tch057_03_tp2.sqlite

data class Reservation(
    val id: Int,
    val destination: String,
    val travel_date: String,
    val booking_date: String,
    val price: Double,
    val status: String
)
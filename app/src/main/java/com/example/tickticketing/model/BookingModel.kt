package com.example.tickticketing.model

data class Booking(
    var id: String = "",
    val eventId: String = "",
    val userId: String = "",
    val bookingDate: Long = 0,
    val status: String = "CONFIRMED"
)


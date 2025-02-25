package com.example.tickticketing.model

data class Event(
    var id: String = "",
    val title: String = "",
    val description: String = "",
    val date: Long = 0,
    val location: String = "",
    val category: String = "",
    val creatorId: String = "",
    val imageUrl: String = "",
    val price: Double = 0.0,
    val capacity: Int = 0,
    val bookedCount: Int = 0
)

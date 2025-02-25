package com.example.tickticketing.repository

import com.example.tickticketing.model.Booking

interface BookingRepository {
    fun createBooking(booking: Booking, callback: (Boolean, String, String) -> Unit)
    fun getBooking(bookingId: String, callback: (Booking?, Boolean, String) -> Unit)
    fun updateBooking(bookingId: String, data: MutableMap<String, Any>, callback: (Boolean, String) -> Unit)
    fun deleteBooking(bookingId: String, callback: (Boolean, String) -> Unit)
    fun getUserBookings(userId: String, callback: (List<Booking>, Boolean, String) -> Unit)
    fun getEventBookings(eventId: String, callback: (List<Booking>, Boolean, String) -> Unit)
}


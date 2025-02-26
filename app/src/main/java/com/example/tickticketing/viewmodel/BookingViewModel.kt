package com.example.tickticketing.viewmodel

import androidx.lifecycle.ViewModel
import com.example.tickticketing.model.Booking
import com.example.tickticketing.repository.BookingRepositoryImpl

class BookingViewModel(
    private val repo: BookingRepositoryImpl = BookingRepositoryImpl()
) : ViewModel() {

    fun createBooking(booking: Booking, callback: (Boolean, String, String) -> Unit) {
        repo.createBooking(booking, callback)
    }

    fun deleteBooking(bookingId: String, callback: (Boolean, String) -> Unit) {
        repo.deleteBooking(bookingId, callback)
    }

    fun getUserBookings(userId: String, callback: (List<Booking>, Boolean, String) -> Unit) {
        repo.getUserBookings(userId, callback)
    }
}

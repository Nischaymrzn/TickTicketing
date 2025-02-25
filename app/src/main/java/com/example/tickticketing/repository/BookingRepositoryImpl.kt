package com.example.tickticketing.repository

import com.example.tickticketing.model.Booking
import com.google.firebase.database.*

class BookingRepositoryImpl : BookingRepository {
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val reference: DatabaseReference = database.reference.child("bookings")

    override fun createBooking(booking: Booking, callback: (Boolean, String, String) -> Unit) {
        val bookingId = reference.push().key ?: return
        booking.id = bookingId
        reference.child(bookingId).setValue(booking).addOnCompleteListener {
            if (it.isSuccessful) {
                callback(true, "Booking created successfully", bookingId)
            } else {
                callback(false, it.exception?.message ?: "Failed to create booking", "")
            }
        }
    }

    override fun getBooking(bookingId: String, callback: (Booking?, Boolean, String) -> Unit) {
        reference.child(bookingId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val booking = snapshot.getValue(Booking::class.java)
                if (booking != null) {
                    callback(booking, true, "Booking fetched successfully")
                } else {
                    callback(null, false, "Booking not found")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                callback(null, false, error.message)
            }
        })
    }

    override fun updateBooking(bookingId: String, data: MutableMap<String, Any>, callback: (Boolean, String) -> Unit) {
        reference.child(bookingId).updateChildren(data).addOnCompleteListener {
            if (it.isSuccessful) {
                callback(true, "Booking updated successfully")
            } else {
                callback(false, it.exception?.message ?: "Failed to update booking")
            }
        }
    }

    override fun deleteBooking(bookingId: String, callback: (Boolean, String) -> Unit) {
        reference.child(bookingId).removeValue().addOnCompleteListener {
            if (it.isSuccessful) {
                callback(true, "Booking deleted successfully")
            } else {
                callback(false, it.exception?.message ?: "Failed to delete booking")
            }
        }
    }

    override fun getUserBookings(userId: String, callback: (List<Booking>, Boolean, String) -> Unit) {
        reference.orderByChild("userId").equalTo(userId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val bookings = snapshot.children.mapNotNull { it.getValue(Booking::class.java) }
                callback(bookings, true, "User bookings fetched successfully")
            }

            override fun onCancelled(error: DatabaseError) {
                callback(emptyList(), false, error.message)
            }
        })
    }

    override fun getEventBookings(eventId: String, callback: (List<Booking>, Boolean, String) -> Unit) {
        reference.orderByChild("eventId").equalTo(eventId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val bookings = snapshot.children.mapNotNull { it.getValue(Booking::class.java) }
                callback(bookings, true, "Event bookings fetched successfully")
            }

            override fun onCancelled(error: DatabaseError) {
                callback(emptyList(), false, error.message)
            }
        })
    }
}


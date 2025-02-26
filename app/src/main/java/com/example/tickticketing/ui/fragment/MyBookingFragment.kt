package com.example.tickticketing.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tickticketing.R
import com.example.tickticketing.adapter.BookingAdapter
import com.example.tickticketing.databinding.FragmentMyBookingBinding
import com.example.tickticketing.model.Booking
import com.example.tickticketing.repository.BookingRepositoryImpl
import com.example.tickticketing.repository.EventRepositoryImpl
import com.example.tickticketing.repository.UserRepositoryImpl
import com.example.tickticketing.viewmodel.BookingViewModel
import com.example.tickticketing.viewmodel.EventViewModel
import com.example.tickticketing.viewmodel.UserViewModel
import com.google.android.material.snackbar.Snackbar

class MyBookingFragment : Fragment() {

    private lateinit var binding: FragmentMyBookingBinding
    private lateinit var userViewModel: UserViewModel
    private val bookingViewModel: BookingViewModel by lazy { BookingViewModel(BookingRepositoryImpl()) }

    private val eventViewModel: EventViewModel by lazy { EventViewModel(EventRepositoryImpl()) }
    private lateinit var bookingAdapter: BookingAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMyBookingBinding.inflate(inflater, container, false)
        val userRepository = UserRepositoryImpl()
        userViewModel = UserViewModel(userRepository)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        fetchUserBookings()
    }

    private fun setupRecyclerView() {
        bookingAdapter = BookingAdapter(mutableListOf()) { eventId, callback ->

            eventViewModel.getEvent(eventId) { event, success, message ->
                callback(event)
            }
        }
        bookingAdapter.onDeleteClicked = { booking ->
            deleteBooking(booking)
        }
        binding.recyclerViewBookings.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = bookingAdapter
        }
    }

    private fun fetchUserBookings() {
        val currentUser = userViewModel.getCurrentUser()
        if (currentUser == null) {
            Snackbar.make(binding.myBookingRoot, "User not logged in", Snackbar.LENGTH_SHORT).show()
            return
        }
        bookingViewModel.getUserBookings(currentUser.uid) { bookings, success, message ->
            if (success) {
                bookingAdapter.setData(bookings)
                updateEmptyState(bookings.isEmpty())
            } else {
                Snackbar.make(binding.myBookingRoot, "Error: $message", Snackbar.LENGTH_SHORT).show()
                updateEmptyState(true)
            }
        }
    }

    private fun deleteBooking(booking: Booking) {
        bookingViewModel.deleteBooking(booking.id) { success, message ->
            if (success) {
                Snackbar.make(binding.myBookingRoot, "Booking deleted", Snackbar.LENGTH_SHORT).show()
                fetchUserBookings()
            } else {
                Snackbar.make(binding.myBookingRoot, "Error: $message", Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateEmptyState(isEmpty: Boolean) {
        if (isEmpty) {
            binding.recyclerViewBookings.visibility = View.GONE
            binding.tvEmptyState.visibility = View.VISIBLE
        } else {
            binding.recyclerViewBookings.visibility = View.VISIBLE
            binding.tvEmptyState.visibility = View.GONE
        }
    }
}

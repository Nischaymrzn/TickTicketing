package com.example.tickticketing.ui.activity

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.tickticketing.R
import com.example.tickticketing.databinding.ActivityEventDetailBinding
import com.example.tickticketing.model.Booking
import com.example.tickticketing.model.Event
import com.example.tickticketing.repository.EventRepositoryImpl
import com.example.tickticketing.utils.Loader
import com.example.tickticketing.viewmodel.BookingViewModel
import com.example.tickticketing.viewmodel.EventViewModel
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class EventDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEventDetailBinding
    private val eventViewModel: EventViewModel by lazy { EventViewModel(EventRepositoryImpl()) }
    private val bookingViewModel: BookingViewModel by lazy { BookingViewModel() }
    private var eventId: String = ""
    private var currentEvent: Event? = null
    private lateinit var loader: Loader

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEventDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loader = Loader(this)

        // Get event ID from intent
        eventId = intent.getStringExtra("EVENT_ID") ?: ""
        if (eventId.isEmpty()) {
            Toast.makeText(this, "Event not found", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        setupUI()
        loadEventDetails()
    }

    private fun setupUI() {
        // Set up back button
        binding.backButton.setOnClickListener {
            finish()
        }

        // Set up favorite button
        binding.favoriteButton.setOnClickListener {
            Toast.makeText(this, "Added to favorites", Toast.LENGTH_SHORT).show()
            binding.favoriteButton.setImageResource(R.drawable.ic_favorite_filled)
        }

        // Set up book button
        binding.bookButton.setOnClickListener {
            currentEvent?.let { event ->
                handleBooking(event)
            }
        }

        // Set up share button
        binding.shareButton.setOnClickListener {
            Toast.makeText(this, "Share functionality coming soon", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadEventDetails() {
        loader.show()

        eventViewModel.getEvent(eventId) { event, success, message ->
            loader.dismiss()

            if (success && event != null) {
                currentEvent = event
                displayEventDetails(event)
            } else {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun displayEventDetails(event: Event) {
        // Load event image
        Glide.with(this)
            .load(event.imageUrl)
            .placeholder(R.drawable.placeholder)
            .error(R.drawable.placeholder)
            .into(binding.eventImage)

        // Set event details
        binding.eventTitle.text = event.title

        // Format and set date
        val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        val formattedDate = dateFormat.format(Date(event.date))

        binding.eventYear.text = formattedDate.split(",").last().trim()
        binding.eventLocation.text = event.location
        binding.eventDuration.text = "${event.capacity} seats"

        // Set category chips
        binding.eventCategory.text = event.category

        // Set price
        binding.eventPrice.text = "रू ${event.price}"

        // Set description
        binding.eventDescription.text = event.description

        // Update availability
        val availableSeats = event.capacity - event.bookedCount
        binding.availabilityText.text = "$availableSeats seats available"

        // Disable book button if no seats available
        if (availableSeats <= 0) {
            binding.bookButton.isEnabled = false
            binding.bookButton.alpha = 0.5f
            binding.availabilityText.setTextColor(resources.getColor(R.color.red_400, theme))
        }

        // Show the content
        binding.contentLayout.visibility = View.VISIBLE
    }

    private fun handleBooking(event: Event) {
        loader.show()

        // Get current user ID
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        if (userId == null) {
            loader.dismiss()
            Toast.makeText(this, "Please login to book this event", Toast.LENGTH_SHORT).show()
            return
        }

        // Create booking object
        val booking = Booking(
            eventId = event.id,
            userId = userId,
            bookingDate = System.currentTimeMillis(),
            status = "CONFIRMED"
        )

        // Send booking to database
        bookingViewModel.createBooking(booking) { success, message, _ ->
            loader.dismiss()

            if (success) {
                Toast.makeText(this, "Booking successful!", Toast.LENGTH_SHORT).show()

                // Update event booked count
                val updatedData = mutableMapOf<String, Any>()
                updatedData["bookedCount"] = event.bookedCount + 1

                eventViewModel.updateEvent(event.id, updatedData) { updateSuccess, updateMessage ->
                    if (updateSuccess) {
                        // Refresh event details
                        loadEventDetails()
                    }
                }
            } else {
                Toast.makeText(this, "Booking failed: $message", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
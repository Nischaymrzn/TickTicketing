package com.example.tickticketing.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.tickticketing.R
import com.example.tickticketing.adapter.EventAdapter
import com.example.tickticketing.model.Booking
import com.example.tickticketing.model.Event
import com.example.tickticketing.repository.EventRepositoryImpl
import com.example.tickticketing.utils.Loader
import com.example.tickticketing.viewmodel.BookingViewModel
import com.example.tickticketing.viewmodel.EventViewModel
import com.google.android.material.chip.ChipGroup
import com.google.firebase.auth.FirebaseAuth

class ExploreEventFragment : Fragment() {

    private val eventViewModel: EventViewModel by lazy { EventViewModel(EventRepositoryImpl()) }
    private val bookingViewModel: BookingViewModel by lazy { BookingViewModel() }
    private lateinit var eventAdapter: EventAdapter
    private var currentFilter = "All"

    private lateinit var bannerImage: ImageView
    private lateinit var bannerTitle: TextView
    private lateinit var bannerDescription: TextView
    private lateinit var categoryChipGroup: ChipGroup
    private lateinit var eventsRecyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_explore_event, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeViews(view)
        setupBanner()
        setupEventsList()
        setupFilterChips()
        loadEvents()
        setupViewModelObservers()
    }

    private fun initializeViews(view: View) {
        bannerImage = view.findViewById(R.id.bannerImage)
        bannerTitle = view.findViewById(R.id.bannerTitle)
        bannerDescription = view.findViewById(R.id.bannerDescription)
        categoryChipGroup = view.findViewById(R.id.categoryChipGroup)
        eventsRecyclerView = view.findViewById(R.id.eventsRecyclerView)
        progressBar = view.findViewById(R.id.progressBar)
    }

    private fun setupBanner() {
        Glide.with(requireContext())
            .load("https://s1.thcdn.com/design-assets/products/13508485/13508485/No%20way%20home%20banner.jpg")
            .into(bannerImage)

        bannerTitle.text = "Spider-Man: No Way Home"
        bannerDescription.text = "Get ready for the ultimate Spider-Man experience!"
    }

    private fun setupEventsList() {
        eventAdapter = EventAdapter(
            emptyList(),
            onEventClick = { event -> navigateToEventDetail(event) },
            onBookClick = { event -> handleBooking(event) }
        )

        eventsRecyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = eventAdapter
        }
    }

    private fun setupFilterChips() {
        categoryChipGroup.setOnCheckedChangeListener { _, checkedId ->
            currentFilter = when (checkedId) {
                R.id.chipAll -> "All"
                R.id.chipMovies -> "Movie"       // Ensure this matches your event data
                R.id.chipConcerts -> "Concert"     // Ensure this matches your event data
                else -> "All"
            }
            filterEvents()
        }
    }

    private fun loadEvents() {
        progressBar.visibility = View.VISIBLE
        eventViewModel.getAllEvents()
    }

    private fun setupViewModelObservers() {
        eventViewModel.eventData.observe(viewLifecycleOwner) { events ->
            eventAdapter.updateEvents(events)
            filterEvents()
            progressBar.visibility = View.GONE
        }
    }

    private fun filterEvents() {
        val allEvents = eventAdapter.getAllEvents()
        val filteredEvents = if (currentFilter == "All") {
            allEvents
        } else {
            allEvents.filter { it.category == currentFilter }
        }
        eventAdapter.setFilteredEvents(filteredEvents)
    }

    private fun navigateToEventDetail(event: Event) {
        // TODO: Implement navigation to event detail screen
        Toast.makeText(context, "Viewing ${event.title}", Toast.LENGTH_SHORT).show()
    }

    private fun handleBooking(event: Event) {
        // Show a simple loader while processing the booking
        val loader = Loader(requireActivity())
        loader.show()

        // Retrieve the current user ID (assuming FirebaseAuth is set up)
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: "unknown"

        // Create a new booking object; bookingDate is set to the current time.
        val booking = Booking(
            eventId = event.id,
            userId = userId,
            bookingDate = System.currentTimeMillis(),
            status = "CONFIRMED"
        )

        // Send booking data to Firebase via the BookingViewModel
        bookingViewModel.createBooking(booking) { success, message, bookingId ->
            loader.dismiss()
            if (success) {
                Toast.makeText(context, "Booking successful!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Booking failed: $message", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

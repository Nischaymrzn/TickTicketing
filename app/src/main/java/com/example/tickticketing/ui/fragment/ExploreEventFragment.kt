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
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.tickticketing.R
import com.example.tickticketing.adapter.EventAdapter
import com.example.tickticketing.model.Event
import com.example.tickticketing.repository.EventRepositoryImpl
import com.example.tickticketing.viewmodel.EventViewModel
import com.google.android.material.chip.ChipGroup

class ExploreEventFragment : Fragment() {

    // Lazy initialization of the ViewModel using a specific repository implementation.
    private val eventViewModel: EventViewModel by lazy { EventViewModel(EventRepositoryImpl()) }
    private lateinit var eventAdapter: EventAdapter

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

        // Initialize views from layout.
        bannerImage = view.findViewById(R.id.bannerImage)
        bannerTitle = view.findViewById(R.id.bannerTitle)
        bannerDescription = view.findViewById(R.id.bannerDescription)
        categoryChipGroup = view.findViewById(R.id.categoryChipGroup)
        eventsRecyclerView = view.findViewById(R.id.eventsRecyclerView)
        progressBar = view.findViewById(R.id.progressBar)

        setupBanner()
        setupEventsList()
        setupCategoryFilter()
        loadEvents()
        setupViewModelObservers()
    }

    private fun setupBanner() {
        // Load banner image using Glide. Replace "YOUR_BANNER_IMAGE_URL" with your actual image URL.
        Glide.with(requireContext())
            .load("https://imageio.forbes.com/specials-images/imageserve/61bf9846074b90fe3a41ffae/0x0.jpg?format=jpg&height=900&width=1600&fit=bounds")
            .into(bannerImage)

        bannerTitle.text = "Spider-Man: No Way Home"
        bannerDescription.text = "Get ready for the ultimate Spider-Man experience!"
    }

    private fun setupViewModelObservers() {
        // Observe changes to the event data.
        eventViewModel.eventData.observe(viewLifecycleOwner) { events ->
            eventAdapter.updateEvents(events)
            progressBar.visibility = View.GONE
        }
    }

    private fun setupEventsList() {
        eventAdapter = EventAdapter(
            events = emptyList(),
            onEventClick = { event -> navigateToEventDetail(event) },
            onBookClick = { event -> handleBooking(event) }
        )

        eventsRecyclerView.apply {
            layoutManager = GridLayoutManager(context, 2)
            adapter = eventAdapter
        }
    }

    private fun setupCategoryFilter() {
        categoryChipGroup.setOnCheckedChangeListener { _, checkedId ->
            progressBar.visibility = View.VISIBLE
            when (checkedId) {
                R.id.chipAll -> eventViewModel.getAllEvents()
                R.id.chipMovies -> eventViewModel.getEventsByCategory("Movies")
                R.id.chipConcerts -> eventViewModel.getEventsByCategory("Concerts")
                else -> eventViewModel.getAllEvents()
            }
        }
    }

    private fun loadEvents() {
        progressBar.visibility = View.VISIBLE
        eventViewModel.getAllEvents()
    }

    private fun navigateToEventDetail(event: Event) {
        // Navigate to event detail screen (implementation needed)
        Toast.makeText(context, "Viewing ${event.title}", Toast.LENGTH_SHORT).show()
    }

    private fun handleBooking(event: Event) {
        // Implement your booking logic here
        Toast.makeText(context, "Booking ${event.title}", Toast.LENGTH_SHORT).show()
    }
}

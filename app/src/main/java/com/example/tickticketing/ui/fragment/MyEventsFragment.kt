package com.example.tickticketing.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tickticketing.R
import com.example.tickticketing.databinding.FragmentMyEventsBinding
import com.example.tickticketing.model.Event
import com.example.tickticketing.repository.EventRepositoryImpl
import com.example.tickticketing.repository.UserRepositoryImpl
import com.example.tickticketing.ui.activity.AddEventActivity
import com.example.tickticketing.ui.activity.UpdateEventActivity
import com.example.tickticketing.adapter.MyEventsAdapter
import com.example.tickticketing.viewmodel.EventViewModel
import com.example.tickticketing.viewmodel.UserViewModel
import com.google.android.material.snackbar.Snackbar

class MyEventsFragment : Fragment() {

    private lateinit var binding: FragmentMyEventsBinding
    private lateinit var userViewModel: UserViewModel
    private val eventViewModel: EventViewModel by lazy {
        EventViewModel(EventRepositoryImpl())
    }
    private lateinit var myEventsAdapter: MyEventsAdapter
    private var currentFilter: String = "All"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMyEventsBinding.inflate(inflater, container, false)
        val userRepository = UserRepositoryImpl()
        userViewModel = UserViewModel(userRepository)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupFab()
        setupFilterChips()
        fetchUserEvents()
    }

    private fun setupRecyclerView() {
        myEventsAdapter = MyEventsAdapter(
            eventList = mutableListOf(),
            onEditClicked = { event -> launchUpdateEventActivity(event) },
            onDeleteClicked = { event -> deleteEvent(event) }
        )

        binding.recyclerViewMyEvents.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = myEventsAdapter
        }
    }

    private fun setupFab() {
        binding.fabAdd.setOnClickListener {
            Log.d("MyEventsFragment", "FAB clicked! Opening AddEventActivity")
            startActivity(Intent(requireContext(), AddEventActivity::class.java))
        }
    }

    private fun setupFilterChips() {
        binding.filterChipGroup.setOnCheckedChangeListener { group, checkedId ->
            currentFilter = when (checkedId) {
                R.id.chipAll -> "All"
                R.id.chipMovie -> "Movie"
                R.id.chipConcert -> "Concert"
                R.id.chipOthers -> "Others"
                else -> "All"
            }
            filterEvents()
        }
    }

    private fun filterEvents() {
        val filteredEvents = if (currentFilter == "All") {
            myEventsAdapter.getAllEvents()
        } else {
            myEventsAdapter.getAllEvents().filter { it.category == currentFilter }
        }
        myEventsAdapter.setData(filteredEvents)
        updateEmptyState(filteredEvents.isEmpty())
    }

    private fun launchUpdateEventActivity(event: Event) {
        val intent = Intent(requireContext(), UpdateEventActivity::class.java).apply {
            putExtra("EVENT_ID", event.id)
            putExtra("EVENT_TITLE", event.title)
            putExtra("EVENT_DESCRIPTION", event.description)
            putExtra("EVENT_DATE", event.date)
            putExtra("EVENT_LOCATION", event.location)
            putExtra("EVENT_CATEGORY", event.category)
            putExtra("EVENT_PRICE", event.price)
            putExtra("EVENT_CAPACITY", event.capacity)
            putExtra("EVENT_IMAGE_URL", event.imageUrl)
        }
        startActivity(intent)
    }

    private fun deleteEvent(event: Event) {
        eventViewModel.deleteEvent(event.id) { success, message ->
            if (success) {
                Snackbar.make(binding.myEventsRoot, "Event deleted", Snackbar.LENGTH_SHORT).show()
                fetchUserEvents()
            } else {
                Snackbar.make(binding.myEventsRoot, "Error: $message", Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        fetchUserEvents()
    }

    private fun fetchUserEvents() {
        val currentUser = userViewModel.getCurrentUser()
        if (currentUser == null) {
            Snackbar.make(binding.myEventsRoot, "User not logged in", Snackbar.LENGTH_SHORT).show()
            return
        }

        eventViewModel.getEventsByUser(currentUser.uid) { events: List<Event>, success: Boolean, message: String ->
            if (success) {
                myEventsAdapter.setAllEvents(events)
                filterEvents()
            } else {
                Snackbar.make(binding.myEventsRoot, "Error: $message", Snackbar.LENGTH_SHORT).show()
                updateEmptyState(true)
            }
        }
    }

    private fun updateEmptyState(isEmpty: Boolean) {
        if (isEmpty) {
            binding.recyclerViewMyEvents.visibility = View.GONE
            binding.tvEmptyState.visibility = View.VISIBLE
        } else {
            binding.recyclerViewMyEvents.visibility = View.VISIBLE
            binding.tvEmptyState.visibility = View.GONE
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = MyEventsFragment()
    }
}
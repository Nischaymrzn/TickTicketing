package com.example.tickticketing.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.tickticketing.R
import com.example.tickticketing.model.Event
import com.google.android.material.button.MaterialButton
import java.text.SimpleDateFormat
import java.util.*

class EventAdapter(
    private var allEvents: List<Event> = emptyList(),
    private val onEventClick: (Event) -> Unit,
    private val onBookClick: (Event) -> Unit
) : RecyclerView.Adapter<EventAdapter.EventViewHolder>() {

    private var filteredEvents: List<Event> = allEvents

    class EventViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val eventImage: ImageView = view.findViewById(R.id.eventImage)
        val categoryBadge: TextView = view.findViewById(R.id.categoryBadge)
        val eventTitle: TextView = view.findViewById(R.id.eventTitle)
        val eventLocation: TextView = view.findViewById(R.id.eventLocation)
        val eventDate: TextView = view.findViewById(R.id.eventDate)
        val eventPrice: TextView = view.findViewById(R.id.eventPrice)
        val bookButton: MaterialButton = view.findViewById(R.id.bookButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_event, parent, false)
        return EventViewHolder(view)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val event = filteredEvents[position]

        Glide.with(holder.itemView.context)
            .load(event.imageUrl)
            .into(holder.eventImage)

        holder.categoryBadge.text = event.category
        holder.eventTitle.text = event.title
        holder.eventLocation.text = event.location
        holder.eventDate.text = formatDate(event.date)
        holder.eventPrice.text = "रू ${formatPrice(event.price)}"

        holder.itemView.setOnClickListener { onEventClick(event) }
        holder.bookButton.setOnClickListener { onBookClick(event) }
    }

    override fun getItemCount() = filteredEvents.size

    fun updateEvents(newEvents: List<Event>) {
        allEvents = newEvents
        filteredEvents = newEvents
        notifyDataSetChanged()
    }

    fun getAllEvents(): List<Event> = allEvents

    fun setFilteredEvents(events: List<Event>) {
        filteredEvents = events
        notifyDataSetChanged()
    }

    private fun formatDate(timestamp: Long): String {
        val date = Date(timestamp)
        val format = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        return format.format(date)
    }

    private fun formatPrice(price: Double): String {
        return "$price"
    }
}

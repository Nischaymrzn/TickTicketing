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
import java.text.NumberFormat
import java.util.*

class EventAdapter(
    private var events: List<Event>,
    private val onEventClick: (Event) -> Unit,
    private val onBookClick: (Event) -> Unit
) : RecyclerView.Adapter<EventAdapter.EventViewHolder>() {

    class EventViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val eventImage: ImageView = view.findViewById(R.id.eventImage)
        val eventTitle: TextView = view.findViewById(R.id.eventTitle)
        val eventLocation: TextView = view.findViewById(R.id.eventLocation)
        val eventPrice: TextView = view.findViewById(R.id.eventPrice)
        val bookButton: MaterialButton = view.findViewById(R.id.bookButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_event, parent, false)
        return EventViewHolder(view)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val event = events[position]

        Glide.with(holder.itemView.context)
            .load(event.imageUrl)
            .into(holder.eventImage)

        holder.eventTitle.text = event.title
        holder.eventLocation.text = event.location
        holder.eventPrice.text = NumberFormat.getCurrencyInstance().format(event.price)

        holder.itemView.setOnClickListener { onEventClick(event) }
        holder.bookButton.setOnClickListener { onBookClick(event) }
    }

    override fun getItemCount() = events.size

    fun updateEvents(newEvents: List<Event>) {
        events = newEvents
        notifyDataSetChanged()
    }
}
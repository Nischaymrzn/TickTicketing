package com.example.tickticketing.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.tickticketing.R
import com.example.tickticketing.databinding.MyEventItemBinding
import com.example.tickticketing.model.Event
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.*

class MyEventsAdapter(
    private var eventList: MutableList<Event>,
    private val onEditClicked: (Event) -> Unit,
    private val onDeleteClicked: (Event) -> Unit
) : RecyclerView.Adapter<MyEventsAdapter.MyEventsViewHolder>() {

    private var allEvents: List<Event> = listOf()

    inner class MyEventsViewHolder(val binding: MyEventItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyEventsViewHolder {
        val binding = MyEventItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyEventsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyEventsViewHolder, position: Int) {
        val event = eventList[position]
        with(holder.binding) {
            if (event.imageUrl.isNotEmpty()) {
                Picasso.get()
                    .load(event.imageUrl)
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .into(imgEvent)
            } else {
                imgEvent.setImageResource(R.drawable.ic_launcher_foreground)
            }

            tvEventTitle.text = event.title
            chipCategory.text = event.category

            val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
            val dateString = if (event.date != 0L) {
                dateFormat.format(Date(event.date))
            } else {
                "No Date"
            }
            tvEventDate.text = dateString
            tvEventLocation.text = event.location

            tvEventPrice.text = "Price: रू ${event.price}"

            tvEventDate.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_calendar, 0, 0, 0)
            tvEventDate.compoundDrawablePadding = 8
            tvEventLocation.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_location, 0, 0, 0)
            tvEventLocation.compoundDrawablePadding = 8

            btnEdit.setOnClickListener { onEditClicked(event) }
            btnDelete.setOnClickListener { onDeleteClicked(event) }
        }
    }

    override fun getItemCount(): Int = eventList.size

    fun setAllEvents(events: List<Event>) {
        allEvents = events
        setData(events)
    }

    fun getAllEvents(): List<Event> = allEvents

    fun setData(newList: List<Event>) {
        eventList.clear()
        eventList.addAll(newList)
        notifyDataSetChanged()
    }
}
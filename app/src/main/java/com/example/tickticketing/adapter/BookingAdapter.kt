package com.example.tickticketing.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.tickticketing.databinding.ItemBookingBinding
import com.example.tickticketing.model.Booking
import com.example.tickticketing.model.Event
import java.text.SimpleDateFormat
import java.util.*

class BookingAdapter(
    private var bookingList: MutableList<Booking>,

    private val onFetchEvent: (String, (Event?) -> Unit) -> Unit
) : RecyclerView.Adapter<BookingAdapter.BookingViewHolder>() {

    inner class BookingViewHolder(val binding: ItemBookingBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookingViewHolder {
        val binding = ItemBookingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BookingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BookingViewHolder, position: Int) {
        val booking = bookingList[position]
        with(holder.binding) {
            tvEventName.text = "Loading..."
            tvPricePaid.text = "Price Paid: -"
            tvBookingDate.text = "Booked At: ${formatDate(booking.bookingDate)}"
            tvStatus.text = "Status: ${booking.status}"

            Glide.with(root.context)
                .load("")
                .placeholder(com.example.tickticketing.R.drawable.placeholder)
                .into(imgEvent)

            onFetchEvent(booking.eventId) { event ->
                event?.let {
                    tvEventName.text = it.title
                    tvPricePaid.text = "Price Paid: ${it.price}"
                    Glide.with(root.context)
                        .load(it.imageUrl)
                        .placeholder(com.example.tickticketing.R.drawable.placeholder)
                        .into(imgEvent)
                } ?: run {
                    tvEventName.text = "Event not found"
                    tvPricePaid.text = "Price Paid: -"
                }
            }

            btnDelete.setOnClickListener {
                onDeleteClicked?.invoke(booking)
            }
        }
    }

    override fun getItemCount(): Int = bookingList.size

    fun setData(newList: List<Booking>) {
        bookingList.clear()
        bookingList.addAll(newList)
        notifyDataSetChanged()
    }

    private fun formatDate(timestamp: Long): String {
        val date = Date(timestamp)
        val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        return sdf.format(date)
    }

    var onDeleteClicked: ((Booking) -> Unit)? = null
}

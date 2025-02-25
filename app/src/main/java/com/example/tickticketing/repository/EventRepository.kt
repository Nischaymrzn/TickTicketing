package com.example.tickticketing.repository

import android.content.Context
import android.net.Uri
import com.example.tickticketing.model.Event

interface EventRepository {
    fun createEvent(event: Event, callback: (Boolean, String, String) -> Unit)
    fun getEvent(eventId: String, callback: (Event?, Boolean, String) -> Unit)
    fun updateEvent(eventId: String, data: MutableMap<String, Any>, callback: (Boolean, String) -> Unit)
    fun deleteEvent(eventId: String, callback: (Boolean, String) -> Unit)
    fun getAllEvents(callback: (List<Event>, Boolean, String) -> Unit)
    fun getEventsByCategory(category: String, callback: (List<Event>, Boolean, String) -> Unit)
    fun getEventsByUser(userId: String, callback: (List<Event>, Boolean, String) -> Unit)
    fun uploadEventImage(context : Context, imageUri: Uri, callback: (String?) -> Unit)
}


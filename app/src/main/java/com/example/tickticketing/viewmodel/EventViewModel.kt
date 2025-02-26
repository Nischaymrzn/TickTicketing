package com.example.tickticketing.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.tickticketing.model.Event
import com.example.tickticketing.repository.EventRepository

class EventViewModel(private val repo: EventRepository) : ViewModel() {

    private val _eventData = MutableLiveData<List<Event>>()
    val eventData: LiveData<List<Event>> get() = _eventData

    fun createEvent(event: Event, callback: (Boolean, String, String) -> Unit) {
        repo.createEvent(event, callback)
    }

    fun getEvent(eventId: String, callback: (Event?, Boolean, String) -> Unit) {
        repo.getEvent(eventId, callback)
    }

    fun getAllEvents() {
        repo.getAllEvents { events, success, _ ->
            if (success) {
                _eventData.value = events
            }
        }
    }

    fun updateEvent(eventId: String, data: MutableMap<String, Any>, callback: (Boolean, String) -> Unit) {
        repo.updateEvent(eventId, data, callback)
    }

    fun deleteEvent(eventId: String, callback: (Boolean, String) -> Unit) {
        repo.deleteEvent(eventId, callback)
    }

    fun getEventsByCategory(category: String) {
        repo.getEventsByCategory(category) { events, success, _ ->
            if (success) {
                _eventData.value = events
            }
        }
    }

    fun getEventsByUser(userId: String, callback: (List<Event>, Boolean, String) -> Unit) {
        repo.getEventsByUser(userId) { events, success, message ->
            if (success) {
                _eventData.value = events
            }
            callback(events, success, message)
        }
    }

    fun uploadEventImage(context: Context, imageUri: Uri, callback: (String?) -> Unit) {
        repo.uploadEventImage(context, imageUri, callback)
    }
}

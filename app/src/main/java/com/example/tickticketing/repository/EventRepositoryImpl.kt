package com.example.tickticketing.repository

import android.content.Context
import android.net.Uri
import android.os.Handler
import android.os.Looper
import com.cloudinary.Cloudinary
import com.cloudinary.utils.ObjectUtils
import com.example.tickticketing.model.Event
import com.google.firebase.database.*
import java.io.InputStream
import java.util.concurrent.Executors

class EventRepositoryImpl : EventRepository {
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val reference: DatabaseReference = database.reference.child("events")

    private val cloudinary = Cloudinary(
        mapOf(
            "cloud_name" to "dfv1we4dw",
            "api_key" to "388389977171296",
            "api_secret" to "M-suWZhPM1kLvJFjENnapJyeVoA"
        )
    )
    override fun createEvent(event: Event, callback: (Boolean, String, String) -> Unit) {
        val eventId = reference.push().key ?: return
        event.id = eventId
        reference.child(eventId).setValue(event).addOnCompleteListener {
            if (it.isSuccessful) {
                callback(true, "Event created successfully", eventId)
            } else {
                callback(false, it.exception?.message ?: "Failed to create event", "")
            }
        }
    }

    override fun getEvent(eventId: String, callback: (Event?, Boolean, String) -> Unit) {
        reference.child(eventId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val event = snapshot.getValue(Event::class.java)
                if (event != null) {
                    callback(event, true, "Event fetched successfully")
                } else {
                    callback(null, false, "Event not found")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                callback(null, false, error.message)
            }
        })
    }

    override fun updateEvent(
        eventId: String,
        data: MutableMap<String, Any>,
        callback: (Boolean, String) -> Unit
    ) {
        reference.child(eventId).updateChildren(data).addOnCompleteListener {
            if (it.isSuccessful) {
                callback(true, "Event updated successfully")
            } else {
                callback(false, it.exception?.message ?: "Failed to update event")
            }
        }
    }

    override fun deleteEvent(eventId: String, callback: (Boolean, String) -> Unit) {
        reference.child(eventId).removeValue().addOnCompleteListener {
            if (it.isSuccessful) {
                callback(true, "Event deleted successfully")
            } else {
                callback(false, it.exception?.message ?: "Failed to delete event")
            }
        }
    }

    override fun getAllEvents(callback: (List<Event>, Boolean, String) -> Unit) {
        reference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val events = snapshot.children.mapNotNull { it.getValue(Event::class.java) }
                callback(events, true, "Events fetched successfully")
            }

            override fun onCancelled(error: DatabaseError) {
                callback(emptyList(), false, error.message)
            }
        })
    }

    override fun getEventsByCategory(
        category: String,
        callback: (List<Event>, Boolean, String) -> Unit
    ) {
        reference.orderByChild("category").equalTo(category).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val events = snapshot.children.mapNotNull { it.getValue(Event::class.java) }
                callback(events, true, "Events fetched successfully")
            }

            override fun onCancelled(error: DatabaseError) {
                callback(emptyList(), false, error.message)
            }
        })
    }

    override fun getEventsByUser(userId: String, callback: (List<Event>, Boolean, String) -> Unit) {
        reference.orderByChild("creatorId").equalTo(userId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val events = snapshot.children.mapNotNull { it.getValue(Event::class.java) }
                callback(events, true, "Events fetched successfully")
            }

            override fun onCancelled(error: DatabaseError) {
                callback(emptyList(), false, error.message)
            }
        })
    }

    override fun uploadEventImage(context: Context, imageUri: Uri, callback: (String?) -> Unit) {
        val executor = Executors.newSingleThreadExecutor()
        executor.execute {
            try {
                val inputStream: InputStream? = context.contentResolver.openInputStream(imageUri)
                val fileName = "event_image_${System.currentTimeMillis()}"

                val response = cloudinary.uploader().upload(
                    inputStream, ObjectUtils.asMap(
                        "public_id", fileName,
                        "resource_type", "image"
                    )
                )

                var imageUrl = response["url"] as String?
                imageUrl = imageUrl?.replace("http://", "https://")

                Handler(Looper.getMainLooper()).post {
                    callback(imageUrl)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Handler(Looper.getMainLooper()).post {
                    callback(null)
                }
            }
        }
    }

}
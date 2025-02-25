package com.example.tickticketing.ui.activity

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.tickticketing.R
import com.example.tickticketing.databinding.ActivityAddEventBinding
import com.example.tickticketing.model.Event
import com.example.tickticketing.repository.EventRepositoryImpl
import com.example.tickticketing.repository.UserRepositoryImpl
import com.example.tickticketing.utils.ImageUtils
import com.example.tickticketing.utils.Loader
import com.example.tickticketing.viewmodel.EventViewModel
import com.example.tickticketing.viewmodel.UserViewModel
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.Locale

class AddEventActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddEventBinding
    private lateinit var eventViewModel: EventViewModel
    private lateinit var loader: Loader
    private lateinit var imageUtils: ImageUtils
    private var imageUri: Uri? = null
    private lateinit var userViewModel: UserViewModel


    @SuppressLint("UnsafeIntentLaunch")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAddEventBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loader = Loader(this)
        imageUtils = ImageUtils(this)
        val eventRepository = EventRepositoryImpl()
        eventViewModel = EventViewModel(eventRepository)

        imageUtils.registerActivity { uri ->
            uri?.let {
                imageUri = it
                Picasso.get().load(it).into(binding.imageViewEvent)
            }
        }

        binding.imageViewEvent.setOnClickListener {
            imageUtils.launchGallery(this)
        }

        binding.buttonCreateEvent.setOnClickListener {
            handleCreateEvent()
        }

        // Apply window insets to the root layout
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainLayout)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    /**
     * Checks if an image is selected; if so, uploads it then creates the event.
     */
    private fun handleCreateEvent() {
        if (imageUri != null) {
            eventViewModel.uploadEventImage(this, imageUri!!) { imageUrl ->
                Log.d("AddEventActivity", "Image URL: $imageUrl")
                createEvent(imageUrl ?: "")
            }
        } else {
            // No image selected; proceed with an empty image URL
            createEvent("")
        }
    }

    /**
     * Collects event data from the UI and creates an event.
     */
    private fun createEvent(imageUrl: String) {
        loader.show()

        val title = binding.editTextTitle.text.toString().trim()
        val description = binding.editTextDescription.text.toString().trim()
        val dateString = binding.editTextDate.text.toString().trim()
        val location = binding.editTextLocation.text.toString().trim()
        val category = binding.spinnerCategory.selectedItem.toString()
        val price = binding.etPrice.text.toString().trim().toDoubleOrNull() ?: 0.0
        val capacity = binding.etCapacity.text.toString().trim().toIntOrNull() ?: 0

        if (title.isEmpty() || description.isEmpty() || location.isEmpty()) {
            Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show()
            loader.dismiss()
            return
        }

        val date: Long = try {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            sdf.parse(dateString)?.time ?: 0L
        } catch (e: Exception) {
            e.printStackTrace()
            0L
        }

        val userRepository = UserRepositoryImpl()
        userViewModel = UserViewModel(userRepository)
        val currUser = userRepository.getCurrentUser()?.uid
        Log.d("hello", currUser.toString())

        // Create the Event object
        val event = Event(
            title = title,
            description = description,
            date = date,
            location = location,
            category = category,
            creatorId = currUser.toString(),
            imageUrl = imageUrl,
            price = price,
            capacity = capacity,
            bookedCount = 0
        )

        // Call the ViewModel to create the event
        eventViewModel.createEvent(event) { success, message, eventId ->
            loader.dismiss()
            if (success) {
                Toast.makeText(this, "Event created successfully!", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Snackbar.make(binding.mainLayout, "Failed to create event: $message", Snackbar.LENGTH_SHORT).show()
            }
        }
    }
}

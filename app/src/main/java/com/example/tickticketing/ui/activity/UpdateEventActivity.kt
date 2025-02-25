package com.example.tickticketing.ui.activity

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.tickticketing.R
import com.example.tickticketing.databinding.ActivityUpdateEventBinding
import com.example.tickticketing.repository.EventRepositoryImpl
import com.example.tickticketing.utils.ImageUtils
import com.example.tickticketing.utils.Loader
import com.example.tickticketing.viewmodel.EventViewModel
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.*

class UpdateEventActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUpdateEventBinding
    private lateinit var eventViewModel: EventViewModel
    private lateinit var loader: Loader
    private lateinit var imageUtils: ImageUtils

    private var eventId: String? = null
    private var currentImageUrl: String? = null
    private var imageUri: Uri? = null

    @SuppressLint("UnsafeIntentLaunch")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityUpdateEventBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loader = Loader(this)
        imageUtils = ImageUtils(this)
        eventViewModel = EventViewModel(EventRepositoryImpl())

        // Get event data from Intent extras
        eventId = intent.getStringExtra("EVENT_ID")   // Required to update
        val title = intent.getStringExtra("EVENT_TITLE") ?: ""
        val description = intent.getStringExtra("EVENT_DESCRIPTION") ?: ""
        val dateLong = intent.getLongExtra("EVENT_DATE", 0L)
        val location = intent.getStringExtra("EVENT_LOCATION") ?: ""
        val category = intent.getStringExtra("EVENT_CATEGORY") ?: ""
        val price = intent.getDoubleExtra("EVENT_PRICE", 0.0)
        val capacity = intent.getIntExtra("EVENT_CAPACITY", 0)
        currentImageUrl = intent.getStringExtra("EVENT_IMAGE_URL") ?: ""

        // Pre-fill the fields
        binding.editTextTitle.setText(title)
        binding.editTextDescription.setText(description)
        if (dateLong != 0L) {
            val dateString = android.text.format.DateFormat.format("yyyy-MM-dd", dateLong).toString()
            binding.editTextDate.setText(dateString)
        }
        binding.editTextLocation.setText(location)
        binding.etPrice.setText(price.toString())
        binding.etCapacity.setText(capacity.toString())

        // Show existing image if present
        if (!currentImageUrl.isNullOrEmpty()) {
            Picasso.get().load(currentImageUrl).into(binding.imageViewEvent)
        }

        // If you want to set the Spinner's selection programmatically:
        val spinnerAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.event_categories,
            android.R.layout.simple_spinner_dropdown_item
        )
        binding.spinnerCategory.adapter = spinnerAdapter
        // If the category is "Concert", "Movie", or "Others", find index and set selection
        val categories = resources.getStringArray(R.array.event_categories)
        val index = categories.indexOf(category)
        if (index >= 0) binding.spinnerCategory.setSelection(index)

        // Image picking
        imageUtils.registerActivity { uri ->
            uri?.let {
                imageUri = it
                Picasso.get().load(it).into(binding.imageViewEvent)
            }
        }
        binding.imageViewEvent.setOnClickListener {
            imageUtils.launchGallery(this)
        }

        // Update event on button click
        binding.buttonUpdateEvent.setOnClickListener {
            handleUpdateEvent()
        }

        // Apply window insets to the root layout
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainLayout)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun handleUpdateEvent() {
        if (eventId.isNullOrEmpty()) {
            Toast.makeText(this, "Event ID missing. Cannot update.", Toast.LENGTH_SHORT).show()
            return
        }

        if (imageUri != null) {
            // If user picked a new image, upload it
            eventViewModel.uploadEventImage(this, imageUri!!) { imageUrl ->
                Log.d("UpdateEventActivity", "New Image URL: $imageUrl")
                updateEventInFirebase(imageUrl ?: currentImageUrl.orEmpty())
            }
        } else {
            // No new image selected; use existing image URL
            updateEventInFirebase(currentImageUrl.orEmpty())
        }
    }

    /**
     * Updates the event in Firebase with the new data.
     * Uses partial updates via a data map in EventRepository.
     */
    private fun updateEventInFirebase(imageUrl: String) {
        loader.show()

        val newTitle = binding.editTextTitle.text.toString().trim()
        val newDescription = binding.editTextDescription.text.toString().trim()
        val dateString = binding.editTextDate.text.toString().trim()
        val newLocation = binding.editTextLocation.text.toString().trim()
        val newCategory = binding.spinnerCategory.selectedItem.toString()
        val newPrice = binding.etPrice.text.toString().trim().toDoubleOrNull() ?: 0.0
        val newCapacity = binding.etCapacity.text.toString().trim().toIntOrNull() ?: 0

        if (newTitle.isEmpty() || newDescription.isEmpty() || newLocation.isEmpty() || dateString.isEmpty()) {
            Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show()
            loader.dismiss()
            return
        }

        // Parse date string "yyyy-MM-dd" into a timestamp
        val newDate: Long = try {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            sdf.parse(dateString)?.time ?: 0L
        } catch (e: Exception) {
            e.printStackTrace()
            0L
        }

        // Build a data map for partial update
        val data = mutableMapOf<String, Any>()
        data["title"] = newTitle
        data["description"] = newDescription
        data["date"] = newDate
        data["location"] = newLocation
        data["category"] = newCategory
        data["price"] = newPrice
        data["capacity"] = newCapacity
        data["imageUrl"] = imageUrl

        eventViewModel.updateEvent(eventId!!, data) { success, message ->
            loader.dismiss()
            if (success) {
                Toast.makeText(this, "Event updated successfully!", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Snackbar.make(binding.mainLayout, "Failed to update event: $message", Snackbar.LENGTH_SHORT).show()
            }
        }
    }
}

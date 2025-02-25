package com.example.tickticketing.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.tickticketing.R
import com.example.tickticketing.databinding.ActivityEditProfileBinding
import com.example.tickticketing.repository.UserRepositoryImpl
import com.example.tickticketing.utils.ImageUtils
import com.example.tickticketing.utils.Loader
import com.example.tickticketing.viewmodel.UserViewModel
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso

class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding
    private lateinit var userViewModel: UserViewModel
    private lateinit var loader: Loader
    private lateinit var imageUtils: ImageUtils
    private var imageUri: Uri? = null

    @SuppressLint("UnsafeIntentLaunch")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loader = Loader(this)
        imageUtils = ImageUtils(this)
        val userRepository = UserRepositoryImpl()
        userViewModel = UserViewModel(userRepository)

        // Set up image selection similar to SignupActivity
        imageUtils.registerActivity { uri ->
            uri?.let {
                imageUri = it
                Picasso.get().load(it).into(binding.avatarImage)
            }
        }
        binding.avatarImage.setOnClickListener {
            imageUtils.launchGallery(this)
        }

        // Prefill fields with existing user data
        val currentUser = userViewModel.getCurrentUser()
        if (currentUser != null) {
            userViewModel.getDataFromDatabase(currentUser.uid)
            userViewModel.userData.observe(this) { user ->
                user?.let {
                    binding.fullNameEditText.setText(it.fullName)
                    binding.emailEditText.setText(it.email)
                    binding.usernameEditText.setText(it.username)
                    binding.addressEditText.setText(it.address)
                    if (!it.imageUrl.isNullOrEmpty()) {
                        Picasso.get()
                            .load(it.imageUrl)
                            .placeholder(R.drawable.placeholder)
                            .into(binding.avatarImage)
                    } else {
                        binding.avatarImage.setImageResource(R.drawable.placeholder)
                    }
                }
            }
        }

        binding.saveButton.setOnClickListener {
            updateProfile()
        }
    }

    private fun updateProfile() {
        loader.show()

        val fullName = binding.fullNameEditText.text.toString().trim()
        val email = binding.emailEditText.text.toString().trim()
        val username = binding.usernameEditText.text.toString().trim()
        val address = binding.addressEditText.text.toString().trim()

        if (fullName.isEmpty() || email.isEmpty() || username.isEmpty() || address.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            loader.dismiss()
            return
        }

        val currentUser = userViewModel.getCurrentUser()
        if (currentUser == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            loader.dismiss()
            return
        }

        // Prepare the data to update
        val updateData = mutableMapOf<String, Any>(
            "fullName" to fullName,
            "email" to email,
            "username" to username,
            "address" to address
        )

        // If a new image was selected, upload it; otherwise, update with existing data.
        if (imageUri != null) {
            userViewModel.uploadImage(this, imageUri!!) { imageUrl ->
                if (imageUrl != null) {
                    updateData["imageUrl"] = imageUrl
                    performProfileUpdate(currentUser.uid, updateData)
                } else {
                    loader.dismiss()
                    Snackbar.make(binding.root, "Image upload failed", Snackbar.LENGTH_SHORT).show()
                }
            }
        } else {
            performProfileUpdate(currentUser.uid, updateData)
        }
    }

    private fun performProfileUpdate(userId: String, updateData: MutableMap<String, Any>) {
        userViewModel.editProfile(userId, updateData) { success, message ->
            loader.dismiss()
            if (success) {
                Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Snackbar.make(binding.root, "Profile update failed: $message", Snackbar.LENGTH_SHORT).show()
            }
        }
    }
}

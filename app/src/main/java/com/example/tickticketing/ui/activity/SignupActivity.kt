package com.example.tickticketing.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.tickticketing.R
import com.example.tickticketing.databinding.ActivitySignupBinding
import com.example.tickticketing.model.UserModel
import com.example.tickticketing.repository.UserRepositoryImpl
import com.example.tickticketing.utils.ImageUtils
import com.example.tickticketing.utils.Loader
import com.example.tickticketing.viewmodel.UserViewModel
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso

class SignupActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding
    private lateinit var userViewModel: UserViewModel
    private lateinit var loadingUtils: Loader

    lateinit var imageUtils: ImageUtils
    var imageUri: Uri? = null

    @SuppressLint("UnsafeIntentLaunch")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        imageUtils = ImageUtils(this)
        loadingUtils = Loader(this)

        val userRepository = UserRepositoryImpl()
        userViewModel = UserViewModel(userRepository)

        imageUtils.registerActivity { uri ->
            if (uri != null) {
                imageUri = uri
                Picasso.get().load(uri).into(binding.imageBrowse)
                Log.d("SignupActivity", "Image selected: $uri")
            } else {
                Log.e("SignupActivity", "No image selected")
            }
        }

        binding.imageBrowse.setOnClickListener {
            imageUtils.launchGallery(this)
        }

        binding.sumbitBtn.setOnClickListener {
            // Validate inputs before uploading image/signing up
            val email = binding.signupEmailText.text.toString().trim()
            val password = binding.signupPasswordText.text.toString().trim()
            val fullName = binding.signupFnameText.text.toString().trim()
            val username = binding.usernameText.text.toString().trim()
            val address = binding.signupAddressText.text.toString().trim()

            if (email.isEmpty() || password.isEmpty() || fullName.isEmpty() ||
                username.isEmpty() || address.isEmpty()
            ) {
                Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            uploadImage()
        }

        binding.alreadyAccountText.setOnClickListener {
            startActivity(Intent(this@SignupActivity, LoginActivity::class.java))
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun uploadImage() {
        if (imageUri != null) {
            userViewModel.uploadImage(this, imageUri!!) { imageUrl ->
                Log.d("SignupActivity", "Image URL: $imageUrl")
                if (imageUrl != null) {
                    addProduct(imageUrl)
                } else {
                    Log.e("SignupActivity", "Failed to upload image to Cloudinary")

                    addProduct("")
                }
            }
        } else {

            Log.d("SignupActivity", "No image selected, proceeding without image")
            addProduct("")
        }
    }

    private fun addProduct(url: String) {
        loadingUtils.show()
        val email: String = binding.signupEmailText.text.toString().trim()
        val password: String = binding.signupPasswordText.text.toString().trim()
        val address: String = binding.signupAddressText.text.toString().trim()
        val fullName: String = binding.signupFnameText.text.toString().trim()
        val username: String = binding.usernameText.text.toString().trim()
        val userRole: String = "client"

        Log.d("SignupActivity", "Signing up with email: $email")

        userViewModel.signup(email, password) { success, message, userId ->
            if (success) {
                Log.d("SignupActivity", "Signup successful, userId: $userId")
                val userModel = UserModel(
                    userId,
                    fullName,
                    email,
                    username,
                    address,
                    userRole,
                    url
                )
                addUserToDatabase(userModel)
            } else {
                loadingUtils.dismiss()
                Log.e("SignupActivity", "Signup failed: $message")
                Snackbar.make(binding.main, message, Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun addUserToDatabase(userModel: UserModel) {
        userViewModel.addUserToDatabase(userModel) { success, message ->
            loadingUtils.dismiss()
            if (success) {
                Log.d("SignupActivity", "User added to database: $message")
                Toast.makeText(this@SignupActivity, message, Toast.LENGTH_SHORT).show()
                startActivity(Intent(this@SignupActivity, LoginActivity::class.java))
            } else {
                Log.e("SignupActivity", "Failed to add user to database: $message")
            }
        }
    }
}

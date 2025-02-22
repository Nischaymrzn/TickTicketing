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
    private lateinit var loadingUtils : Loader

    lateinit var imageUtils: ImageUtils
    var imageUri: Uri? = null

    @SuppressLint("UnsafeIntentLaunch")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding=ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        imageUtils = ImageUtils(this)

        loadingUtils = Loader(this@SignupActivity)

        val userRepository = UserRepositoryImpl()
        userViewModel = UserViewModel(userRepository)

        imageUtils.registerActivity { url ->
            url.let { it ->
                imageUri = it
                Picasso.get().load(it).into(binding.imageBrowse)
            }
        }

        binding.imageBrowse.setOnClickListener {
            imageUtils.launchGallery(this)
        }

        binding.sumbitBtn.setOnClickListener{
            uploadImage()
        }

        binding.alreadyAccountText.setOnClickListener{
            intent= Intent(this@SignupActivity,LoginActivity::class.java)
            startActivity(intent)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun uploadImage() {
        imageUri?.let { uri ->
            userViewModel.uploadImage(this, uri) { imageUrl ->
                Log.d("checpoirs", imageUrl.toString())
                if (imageUrl != null) {
                    addProduct(imageUrl)
                } else {
                    Log.e("Upload Error", "Failed to upload image to Cloudinary")
                }
            }
        }
    }

    private fun addProduct(url : String){
        loadingUtils.show()
        val email : String = binding.signupEmailText.text.toString()
        val password : String = binding.signupPasswordText.text.toString()
        val address : String= binding.signupAddressText.text.toString()
        val fullName : String = binding.signupFnameText.text.toString()
        val username : String = binding.usernameText.text.toString()
        val userRole : String = "client"

        userViewModel.signup(email,password){ success, message, userId ->
            if (success) {
                val userModel = UserModel(
                    userId,
                    fullName, email, username, address, userRole, url
                )
                addUserToDatabase(userModel)
            }else{
                loadingUtils.dismiss()
                Snackbar.make(binding.main,message, Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun addUserToDatabase(userModel: UserModel){
        userViewModel.addUserToDatabase(userModel){
                _, message ->
            loadingUtils.dismiss()
            Toast.makeText(this@SignupActivity,message, Toast.LENGTH_SHORT).show()
        }
    }
}
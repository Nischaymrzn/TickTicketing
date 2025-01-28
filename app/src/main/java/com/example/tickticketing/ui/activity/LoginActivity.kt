package com.example.tickticketing.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.tickticketing.R
import com.example.tickticketing.databinding.ActivityLoginBinding

import com.example.tickticketing.model.UserModel
import com.example.tickticketing.repository.UserRepositoryImpl
import com.example.tickticketing.utils.Loader
import com.example.tickticketing.viewmodel.UserViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {
    private lateinit var binding : ActivityLoginBinding
    private lateinit var userViewModel: UserViewModel
    private lateinit var loadingUtils : Loader

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding=ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val userRepository = UserRepositoryImpl()
        userViewModel = UserViewModel(userRepository)
        loadingUtils = Loader(this@LoginActivity)

        binding.noAccountText.setOnClickListener{
            val intent=Intent(this@LoginActivity, SignupActivity::class.java)
            startActivity(intent)
        }

        binding.loginBtn.setOnClickListener {
            loadingUtils.show()
            val email: String = binding.emailText.text.toString()
            val password: String = binding.PasswordText.text.toString()

            userViewModel.login(email,password) {success,message ->
                if(success) {
                    loadingUtils.dismiss()
                    Snackbar.make(binding.main, message, Snackbar.LENGTH_SHORT).show()
                }else{
                    loadingUtils.dismiss()
                    Snackbar.make(binding.main, message, Snackbar.LENGTH_SHORT).show()
                }
            }
        }

        binding.forgotPassText.setOnClickListener {
            loadingUtils.show()
            val email: String = binding.emailText.text.toString()

            userViewModel.forgetPassword(email) {success,message ->
                if(success) {
                    loadingUtils.dismiss()
                    Snackbar.make(binding.main, message, Snackbar.LENGTH_SHORT).show()
                }else{
                    loadingUtils.dismiss()
                    Snackbar.make(binding.main, message, Snackbar.LENGTH_SHORT).show()
                }
            }
        }


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}
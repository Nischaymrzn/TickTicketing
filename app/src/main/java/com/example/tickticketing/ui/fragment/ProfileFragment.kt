package com.example.tickticketing.ui.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.tickticketing.databinding.FragmentProfileBinding
import com.example.tickticketing.repository.UserRepositoryImpl
import com.example.tickticketing.ui.activity.DashboardActivity
import com.example.tickticketing.ui.activity.EditProfileActivity
import com.example.tickticketing.ui.activity.LoginActivity
import com.example.tickticketing.viewmodel.UserViewModel
import com.squareup.picasso.Picasso

class ProfileFragment : Fragment() {

    // Use a backing property for safe view binding
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var userViewModel: UserViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val repo = UserRepositoryImpl()
        userViewModel = UserViewModel(repo)

        val currentUser = userViewModel.getCurrentUser()
        currentUser?.uid?.let { uid ->
            userViewModel.getDataFromDatabase(uid)
        }

        userViewModel.userData.observe(viewLifecycleOwner) { user ->
            // Update profile information
            binding.fullName.text = user?.fullName
            binding.emailValue.text = user?.email
            binding.locationValue.text = user?.address ?: "Not specified"
            binding.usernameValue.text = user?.username ?: "Not specified"

            // Set user role if available
            user?.userRole?.let { role ->
                binding.roleValue.text = role.capitalize()
            } ?: run {
                binding.roleValue.text = "User"
            }

            // Load profile image
            if (!user?.imageUrl.isNullOrEmpty()) {
                Picasso.get()
                    .load(user?.imageUrl)
                    .placeholder(com.example.tickticketing.R.drawable.placeholder)
                    .into(binding.avatarImage)
            } else {
                binding.avatarImage.setImageResource(com.example.tickticketing.R.drawable.placeholder)
            }
        }

        binding.bookingLayout.setOnClickListener {
            (activity as? DashboardActivity)?.navigateToBookings()
        }

        binding.eventLayout.setOnClickListener{
            (activity as? DashboardActivity)?.navigateToEvents()
        }

        binding.logout.setOnClickListener {
            userViewModel.logout { success, message ->
                if (success) {
                    // Remove stored credentials
                    val sharedPreferences = requireContext().getSharedPreferences("userData", Context.MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    editor.remove("email")
                    editor.remove("password")
                    editor.apply()

                    Toast.makeText(requireContext(), "Logged out successfully", Toast.LENGTH_SHORT).show()
                    val intent = Intent(requireContext(), LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                } else {
                    Toast.makeText(requireContext(), "Logout failed: $message", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.edit.setOnClickListener {
            startActivity(Intent(requireContext(), EditProfileActivity::class.java))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
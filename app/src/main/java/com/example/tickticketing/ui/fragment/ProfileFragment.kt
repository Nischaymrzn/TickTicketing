package com.example.tickticketing.ui.fragment

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

        // Initialize the UserViewModel with the repository
        val repo = UserRepositoryImpl()
        userViewModel = UserViewModel(repo)

        // Get current user and fetch data if available
        val currentUser = userViewModel.getCurrentUser()
        currentUser?.uid?.let { uid ->
            userViewModel.getDataFromDatabase(uid)
        }

        // Observe user data changes to update UI
        userViewModel.userData.observe(viewLifecycleOwner) { user ->
            binding.fullName.text = user?.fullName
            binding.emailAddress.text = user?.email

            // Load avatar image using Picasso: if available, load from URL; otherwise, use placeholder.
            if (!user?.imageUrl.isNullOrEmpty()) {
                Picasso.get()
                    .load(user?.imageUrl)
                    .placeholder(com.example.tickticketing.R.drawable.placeholder)
                    .into(binding.avatarImage)
            } else {
                binding.avatarImage.setImageResource(com.example.tickticketing.R.drawable.placeholder)
            }
        }

        // When the bookings card is clicked, navigate to the MyBookingFragment via the parent activity
        binding.bookingLayout.setOnClickListener {
            (activity as? DashboardActivity)?.navigateToBookings()
        }

        binding.eventLayout.setOnClickListener{
            (activity as? DashboardActivity)?.navigateToEvents()
        }

        // Logout functionality
        binding.logout.setOnClickListener {
            userViewModel.logout { success, message ->
                if (success) {
                    Toast.makeText(requireContext(), "Logged out successfully", Toast.LENGTH_SHORT).show()
                    // Clear activity stack and go to LoginActivity
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

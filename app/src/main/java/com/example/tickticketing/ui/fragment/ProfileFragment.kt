package com.example.tickticketing.ui.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.tickticketing.databinding.FragmentProfileBinding
import com.example.tickticketing.repository.UserRepositoryImpl
import com.example.tickticketing.viewmodel.UserViewModel


class ProfileFragment : Fragment() {
    private lateinit var binding : FragmentProfileBinding
    private lateinit var userViewModel: UserViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        Toast.makeText(requireContext(),"Hello World",Toast.LENGTH_SHORT).show()

        val repo = UserRepositoryImpl()
        userViewModel = UserViewModel(repo)

        val currentUser = userViewModel.getCurrentUser()

        currentUser.let{
            userViewModel.getDataFromDatabase(currentUser?.uid.toString())
        }

        userViewModel.userData.observe(requireActivity()){users->
            binding.fullName.text=users?.fullName
            binding.emailAddress.text=users?.email
        }

        binding.bookingLayout.setOnClickListener {
            val intent = Intent(requireContext(), MyBookingFragment::class.java)
            startActivity(intent)
        }
    }

}
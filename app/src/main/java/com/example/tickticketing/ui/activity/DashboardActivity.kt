package com.example.tickticketing.ui.activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.tickticketing.R
import com.example.tickticketing.databinding.ActivityDashboardBinding
import com.example.tickticketing.ui.fragment.ExploreEventFragment
import com.example.tickticketing.ui.fragment.MyBookingFragment
import com.example.tickticketing.ui.fragment.MyEventsFragment
import com.example.tickticketing.ui.fragment.ProfileFragment

class DashboardActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDashboardBinding

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager: FragmentManager = supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.mainFrame, fragment)
        fragmentTransaction.commit()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        replaceFragment(ExploreEventFragment())

        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.exploreFragment -> replaceFragment(ExploreEventFragment())
                R.id.myBookingsFragment -> replaceFragment(MyBookingFragment())
                R.id.myEventsFragment -> replaceFragment(MyEventsFragment())
                R.id.profileFragment -> replaceFragment(ProfileFragment())
            }
            true
        }


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}
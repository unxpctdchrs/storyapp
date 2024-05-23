package com.noir.storyapp.ui.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.noir.storyapp.R
import com.noir.storyapp.databinding.ActivityMainBinding
import com.noir.storyapp.helper.ViewModelFactory
import com.noir.storyapp.ui.login.LoginActivity

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        navView.setupWithNavController(navController)

        viewModel.getSession().observe(this) { user ->
            Log.d("session", user.isLoggedIn.toString())
            if (!user.isLoggedIn) {
                startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                finish()
            }
        }
    }
}
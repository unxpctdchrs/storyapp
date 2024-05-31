package com.noir.storyapp.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.noir.storyapp.R
import com.noir.storyapp.databinding.ActivityMainBinding
import com.noir.storyapp.helper.ViewModelFactory
import com.noir.storyapp.ui.login.LoginActivity
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var navView: BottomNavigationView

    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private var backPressed: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navView = binding.navView
        navController = findNavController(R.id.nav_host_fragment_activity_main)
        navView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.navigation_add -> navView.visibility = View.GONE
                R.id.detailStoriesFragment -> navView.visibility = View.GONE
                else -> navView.visibility = View.VISIBLE
            }
        }

        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)

        viewModel.getSession().observe(this) { user ->
            if (!user.isLoggedIn) {
                startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                finish()
            }
        }
    }

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            when (navController.currentDestination?.id) {
                R.id.navigation_stories -> {
                    backPressed ++
                    if (backPressed == 1) {
                        Toast.makeText(this@MainActivity, "Press back once again to quit", Toast.LENGTH_SHORT).show()
                    } else if (backPressed == 2) {
                        exitProcess(1)
                    }
                }
                R.id.detailStoriesFragment -> {
                    navController.popBackStack(R.id.navigation_stories, false)
                }
                else -> {
                    navController.popBackStack(R.id.navigation_stories, false)
                }
            }
        }
    }
}
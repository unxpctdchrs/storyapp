package com.noir.storyapp.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.noir.storyapp.R
import com.noir.storyapp.data.local.pref.UserModel
import com.noir.storyapp.databinding.ActivityLoginBinding
import com.noir.storyapp.helper.ViewModelFactory
import com.noir.storyapp.ui.main.MainActivity
import com.noir.storyapp.ui.signup.SignUpActivity

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    private val loginViewModel by viewModels<LoginViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.tvSignup.setOnClickListener {
            startActivity(Intent(this@LoginActivity, SignUpActivity::class.java))
            finish()
        }

        binding.btnLogin.setOnClickListener {
            if (binding.edLoginEmail.text.toString().trim().isEmpty()) {
                binding.edLoginEmail.error = "Masukkan email anda"
            } else if (binding.edLoginPassword.text.toString().trim().isEmpty()) {
                binding.edLoginPassword.error = "Masukkan password anda"
            } else {
                loginViewModel.login(binding.edLoginEmail.text.toString().trim(), binding.edLoginPassword.text.toString().trim())
                loginViewModel.login.observe(this) { user ->
                    if (!user.error) {
                        val userSave = UserModel(
                            user.loginResult.userId,
                            user.loginResult.name,
                            user.loginResult.token,
                            true
                        )
                        loginViewModel.saveSession(userSave)
                    }
                }
            }
        }

        loginViewModel.getSession().observe(this) { user ->
            if (user.isLoggedIn) {
                startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                finish()
            }
        }

        loginViewModel.isLoading.observe(this) { isLoading ->
            loaderState(isLoading)
        }
    }

    private fun loaderState(isLoading: Boolean) {
        if (isLoading) {
            binding.btnLogin.text = ""
            binding.loader.visibility = View.VISIBLE
        } else {
            binding.btnLogin.text = this@LoginActivity.resources.getString(R.string.login)
            binding.loader.visibility = View.GONE
        }
    }
}
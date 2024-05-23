package com.noir.storyapp.ui.signup

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.noir.storyapp.R
import com.noir.storyapp.databinding.ActivitySignUpBinding
import com.noir.storyapp.helper.ViewModelFactory
import com.noir.storyapp.ui.login.LoginActivity

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding

    private val signUpViewModel by viewModels<SignUpViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.tvSignin.setOnClickListener {
            startActivity(Intent(this@SignUpActivity, LoginActivity::class.java))
            finish()
        }

        binding.btnSignup.setOnClickListener {
            signUpViewModel.register(
                binding.edRegisterName.text.toString().trim(),
                binding.edRegisterEmail.text.toString().trim(),
                binding.edRegisterPassword.text.toString().trim()
            )
        }

        signUpViewModel.isLoading.observe(this) { isLoading ->
            loaderState(isLoading)
            if (!isLoading) {
                startActivity(Intent(this@SignUpActivity, LoginActivity::class.java))
            }
        }

        signUpViewModel.register.observe(this) { register ->
            if(register.error) {
                Toast.makeText(this, register.message, Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, register.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loaderState(isLoading: Boolean) {
        if (isLoading) binding.loader.visibility = View.VISIBLE else binding.loader.visibility = View.GONE
    }
}
package com.bytesbridge.app.bughunter.activities.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.viewModels
import carbon.dialog.ProgressDialog
import com.bytesbridge.app.bughunter.R
import com.bytesbridge.app.bughunter.activities.ui.data.models.SignUpModel
import com.bytesbridge.app.bughunter.activities.ui.viewmodels.AuthViewModel
import com.bytesbridge.app.bughunter.activities.utils.LoadingUtils.Companion.loadingEnd
import com.bytesbridge.app.bughunter.activities.utils.LoadingUtils.Companion.loadingStart
import com.bytesbridge.app.bughunter.databinding.ActivitySignupBinding

class SignupActivity : AppCompatActivity() {
    lateinit var binding: ActivitySignupBinding
    val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initListener()

    }

    private fun initListener() {
        binding.btnSignup.setOnClickListener {
            validationCheck()

        }
        binding.tvLogin.setOnClickListener {
            finish()
        }
    }

    private fun validationCheck() {
        val name = binding.etName.text.toString()
        val etEmail = binding.etEmail.text.toString()
        val etUsername = binding.etUsername.text.toString()
        val etPassword = binding.etPassword.text.toString()
        val etConfirmPassword = binding.etConfirmPassword.text.toString()

        when {
            name.isEmpty() -> {
                binding.etName.error = "Please Enter Name"
                return
            }
            etEmail.isEmpty() -> {
                binding.etEmail.error = "Please Enter Email"
                return
            }
            etUsername.isEmpty() -> {
                binding.etUsername.error = "Please Enter Username"
                return
            }
            etPassword.isEmpty() -> {
                binding.etPassword.error = "Please Enter Password"
                return
            }
            etConfirmPassword.isEmpty() -> {
                binding.etConfirmPassword.error = "Please Enter Confirm Password"
                return
            }
            etConfirmPassword != etPassword -> {
                binding.etPassword.error = "Passwords are not same"
                binding.etConfirmPassword.error = "Passwords are not same"
                return
            }
            else -> {
                loadingStart(binding.btnSignup, binding.progress)
                authViewModel.registerUser(
                    SignUpModel(
                        binding.etName.text.toString(),
                        binding.etEmail.text.toString(),
                        binding.etUsername.text.toString(),
                        binding.etPassword.text.toString()
                    )
                ) { sts, msg ->
                    loadingEnd("Signup", binding.btnSignup, binding.progress)
                    finish()
                }
            }
        }

    }
}
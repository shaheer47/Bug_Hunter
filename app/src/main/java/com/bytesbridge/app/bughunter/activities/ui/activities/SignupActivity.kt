package com.bytesbridge.app.bughunter.activities.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import com.bytesbridge.app.bughunter.R
import com.bytesbridge.app.bughunter.activities.ui.data.models.SignUpModel
import com.bytesbridge.app.bughunter.activities.ui.viewmodels.AuthViewModel
import com.bytesbridge.app.bughunter.databinding.ActivitySignupBinding

class SignupActivity : AppCompatActivity() {
    lateinit var binding: ActivitySignupBinding
    val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSignup.setOnClickListener {
        authViewModel.registerUser(
            SignUpModel(
                binding.etName.text.toString(),
                binding.etEmail.text.toString(),
                binding.etUsername.text.toString(),
                binding.etPassword.text.toString()
            ), { sts, msg ->
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
            }
        )}

    }
}
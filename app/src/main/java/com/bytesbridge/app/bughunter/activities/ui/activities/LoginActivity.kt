package com.bytesbridge.app.bughunter.activities.ui.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.bytesbridge.app.bughunter.R
import com.bytesbridge.app.bughunter.activities.ui.data.models.LoginModel
import com.bytesbridge.app.bughunter.activities.ui.viewmodels.AuthViewModel
import com.bytesbridge.app.bughunter.activities.ui.viewmodels.MainViewModel
import com.bytesbridge.app.bughunter.activities.utils.SnackbarUtil
import com.bytesbridge.app.bughunter.activities.utils.SnackbarUtil.Companion.showSnackBar
import com.bytesbridge.app.bughunter.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    lateinit var binding: ActivityLoginBinding
    private val authViewModel: AuthViewModel by viewModels()
    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupClickListeners()

    }

    private fun setupClickListeners() {
        binding.apply {
            tvSkip.setOnClickListener {
                startActivity(Intent(this@LoginActivity, MainActivity::class.java))
            }

            tvRegister.setOnClickListener {
                startActivity(Intent(this@LoginActivity, SignupActivity::class.java))
            }
            tvForgetPassword.setOnClickListener {
                startActivity(Intent(this@LoginActivity, ForgetPasswordActivity::class.java))
            }

            etEmail.addTextChangedListener {
            }

            btnLogin.setOnClickListener {
                if (!etPassword.text.isNullOrEmpty()) {
                    etPassword.error = null
                    authViewModel.login(
                        LoginModel(
                            etEmail.text.toString(),
                            etPassword.text.toString()
                        )
                    )
                    { loginSuccess, message, userData ->
                        if (loginSuccess && userData != null) {
                            mainViewModel.saveUserDataLocally(this@LoginActivity, userData)
                            showSnackBar(binding.root, message)
                            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                        } else {
                            Toast.makeText(this@LoginActivity, message, Toast.LENGTH_SHORT)
                                .show()
                            if (userData == null) {
                                showSnackBar(binding.root, "User not found")
                            }
                        }
                    }

                } else {
                    etPassword.error = getString(R.string.empty_passord_field_error)
                }
            }
        }


    }
}
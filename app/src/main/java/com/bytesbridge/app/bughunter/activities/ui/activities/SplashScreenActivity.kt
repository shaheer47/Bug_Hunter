package com.bytesbridge.app.bughunter.activities.ui.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import carbon.dialog.ProgressDialog
import com.bytesbridge.app.bughunter.R
import com.bytesbridge.app.bughunter.activities.ui.data.models.UserModel
import com.bytesbridge.app.bughunter.activities.ui.viewmodels.AuthViewModel
import com.bytesbridge.app.bughunter.activities.utils.PaperDbUtils.Companion.user

class SplashScreenActivity : AppCompatActivity() {
    var currentUser: UserModel? = null
    private val viewModel: AuthViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)


        Handler(Looper.getMainLooper()).postDelayed({

            currentUser = user(this)
            currentUser?.let { user ->
                viewModel.getUserFromFirebase(user.userId) { updateUser ->
                    updateUser?.let {
                        user(this, updateUser)
                    }
                }
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } ?: run {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()

            }
        }, 3000)


    }


}
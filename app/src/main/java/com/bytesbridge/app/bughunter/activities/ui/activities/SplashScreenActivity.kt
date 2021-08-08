package com.bytesbridge.app.bughunter.activities.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.bytesbridge.app.bughunter.R
import com.bytesbridge.app.bughunter.activities.ui.data.models.UserModel
import com.bytesbridge.app.bughunter.activities.utils.PaperDbUtils.Companion.user

class SplashScreenActivity : AppCompatActivity() {
    var currentUser: UserModel? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        Handler(Looper.getMainLooper()).postDelayed({
            currentUser = user(this)
            currentUser?.let {
                startActivity(Intent(this, MainActivity::class.java))
            } ?: run {
                startActivity(Intent(this, LoginActivity::class.java))
            }
        }, 3000)


    }


}
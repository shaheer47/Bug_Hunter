package com.bytesbridge.app.bughunter.activities.ui.activities

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.Navigation.findNavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.bytesbridge.app.bughunter.R
import com.bytesbridge.app.bughunter.activities.ui.viewmodels.AuthViewModel
import com.bytesbridge.app.bughunter.activities.ui.viewmodels.MainViewModel
import com.bytesbridge.app.bughunter.activities.utils.PaperDbUtils.Companion.user
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    val authModel: AuthViewModel by viewModels()
    val mainViewModel: MainViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        upDateUser()

        bottomNavigationView.setupWithNavController(navController = nav_host_fragment.findNavController())
        bottomNavigationView.setOnItemReselectedListener {
//            Do nothing
        }
        nav_host_fragment.findNavController()
            .addOnDestinationChangedListener(NavController.OnDestinationChangedListener { _, destination, _ ->

            })


    }

    private fun upDateUser() {
        val userId = user(this)?.userId
        userId?.let {
            authModel.getUserFromFirebase(userId) {
                it?.let {
                    mainViewModel.saveUserDataLocally(this, it)
                }
            }
        }
    }


}



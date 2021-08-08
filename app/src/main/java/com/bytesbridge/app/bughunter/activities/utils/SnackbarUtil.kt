package com.bytesbridge.app.bughunter.activities.utils

import android.content.Context
import android.view.View
import com.google.android.material.snackbar.Snackbar

class SnackbarUtil {
    companion object{
        fun showSnackBar(view:View,message:String)
        {
            Snackbar.make(view,message,Snackbar.LENGTH_SHORT).show()
        }

    }
}
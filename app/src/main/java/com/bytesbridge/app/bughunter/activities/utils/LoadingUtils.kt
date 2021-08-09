package com.bytesbridge.app.bughunter.activities.utils

import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView

class LoadingUtils {
    companion object {
        fun loadingStart(button: Button, progress: ProgressBar) {
            button.text = ""
            button.isEnabled = false
            progress.visibility = View.VISIBLE
        }

        fun loadingEnd(text:String,button: Button, progress: ProgressBar) {
            button.text = text
            button.isEnabled = true
            progress.visibility = View.GONE
        }

        fun loadingStart(button: TextView, progress: ProgressBar) {
            button.text = ""
            button.isEnabled = false
            progress.visibility = View.VISIBLE
        }

        fun loadingEnd(text:String,button: TextView, progress: ProgressBar) {
            button.text = text
            button.isEnabled = true
            progress.visibility = View.GONE
        }

    }
}
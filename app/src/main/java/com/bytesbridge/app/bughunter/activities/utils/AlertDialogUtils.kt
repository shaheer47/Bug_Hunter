package com.bytesbridge.app.bughunter.activities.utils

import android.R
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface


class AlertDialogUtils {
    companion object {
        fun showDialog(context: Context,description:String,positive:(yes:Boolean)->Unit) {
            AlertDialog.Builder(context)
                .setTitle("Hunter Coins")
                .setMessage(description)
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton(
                    "Yes"
                ) { _, _ ->
                   positive(true)
                }   .setNegativeButton(
                    "No"
                ) { _, _ ->
                    positive(false)
                }
                .setIcon(R.drawable.ic_dialog_alert)
                .show()
        }
    }

}

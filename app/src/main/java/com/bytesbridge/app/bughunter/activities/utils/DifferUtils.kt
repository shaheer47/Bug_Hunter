package com.bytesbridge.app.bughunter.activities.utils

import android.annotation.SuppressLint
import androidx.recyclerview.widget.DiffUtil
import com.bytesbridge.app.bughunter.activities.ui.data.models.AnswerModel

object differUtils : DiffUtil.ItemCallback<Any>() {
    override fun areItemsTheSame(oldItem: Any, newItem: Any): Boolean {
        return oldItem == newItem
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: Any, newItem: Any): Boolean {
        return oldItem == newItem
    }

}
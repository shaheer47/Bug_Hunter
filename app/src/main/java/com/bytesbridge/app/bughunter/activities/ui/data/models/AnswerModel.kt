package com.bytesbridge.app.bughunter.activities.ui.data.models

import java.io.Serializable

data class AnswerModel(
    val answer_id: String = "",
    val question_id: String = "",
    val answer_detail: String = "",
    var hunter_coins_rewarded: Long = 0,
    var user_image: String="",
    val created_at: String = "",
    val updated_at: String = "",
    val answer_user_id: String = "",
    val question_user_id: String = "",
    val answer_user_name: String = ""
)
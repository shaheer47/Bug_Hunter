package com.bytesbridge.app.bughunter.activities.ui.data.models

import java.io.Serializable

data class QuestionModel(
    val question_id: String = "",
    val number_of_answers: String = "",
    val question_title: String = "",
    val question_detail: String = "",
    val hunter_coins_offer: Long = 0,
    val created_at: String = "",
    val updated_at: String = "",
    val user_id: String = "",
    var views: Long = 0,
    var question_title_for_search: String = "",
    val question_user_name: String = ""
) : Serializable
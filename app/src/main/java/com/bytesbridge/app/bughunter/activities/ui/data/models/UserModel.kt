package com.bytesbridge.app.bughunter.activities.ui.data.models

data class UserModel(
    var userId: String = "",
    var userName: String = "",
    var name: String = "",
    var email: String = "",
    var numberOfQuestions: Long = 0,
    var numberOfAnswers: Long = 0,
    var hunterCoins: Long = 0,
    var user_image: String = "",
    var helpfulHunts: Long = 0,
    var createdAt: String = "",
    var updatedAt: String = "",
)
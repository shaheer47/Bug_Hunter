package com.bytesbridge.app.bughunter.activities.utils

class Constants {
    companion object {

        const val GOOGLE_SEARCH_BASE_URL = "https://google-search3.p.rapidapi.com/api/v1/"
        const val STACK_OVERFLOW_BASE_URL = "https://api.stackexchange.com/2.3/"
        const val STACK_OVERFLOW_API = ""

        //        Endpoints
        const val SEARCH_END_POINT = "search/"
        const val ANSWERS_OF_QUESTION_END_POINT = "questions/{question_id}/answers"

        //---------------------- Firebase Paths ----------------------
        const val PATH_FIREBASE_USERS = "users"
        const val PATH_FIREBASE_QUESTIONS = "questions"
        const val PATH_FIREBASE_ANSWERS = "answers"

        const val FIREBASE_USER_ID = "user_id"
        const val FIREBASE_QUESTION_ID = "question_id"
        const val FIREBASE_ANSWER_ID = "answer_id"
        const val FIREBASE_HUNTER_COINS_REWARDED = "hunter_coins_rewarded"


        const val FIREBASE_ANSWER_USER_NAME = "answer_user_name"
        const val FIREBASE_USER_IMAGE = "user_image"
        const val FIREBASE_QUESTIONS_VIEW = "views"
        const val FIREBASE_HUNTER_COINS = "hunterCoins"
        const val FIREBASE_NUMBER_OF_ANSWERS = "numberOfAnswers"
        const val FIREBASE_NUMBER_OF_QUESTIONS_ASKED = "numberOfQuestions"
        const val FIREBASE_NUMBER_OF_SUCCESSFUL_HUNTS = "numberOfSuccessfulHunts"

        const val MY_QUESTIONS=0
        const val ALL_QUESTIONS=1


        //---------------------- Firebase Paths ----------------------

        const val CONTAINS_STACKOVERFLOW_LINK = "stackoverflow.com/questions/"
        const val STACK_OVERFLOW = "stack overflow"

        //---------------------- Strings ----------------------
        const val Question = "question"
//        const val Question = "question"


    }
}
package com.bytesbridge.app.bughunter.activities.`interface`

import com.bytesbridge.app.bughunter.activities.ui.data.models.responces.StackOverflowAnswersResponse
import com.bytesbridge.app.bughunter.activities.ui.data.models.responces.StackOverflowQuestionResponse
import com.bytesbridge.app.bughunter.activities.utils.Constants.Companion.ANSWERS_OF_QUESTION_END_POINT
import com.bytesbridge.app.bughunter.activities.utils.Constants.Companion.SEARCH_END_POINT
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface IBackendApiStackOverflow {
    @GET(SEARCH_END_POINT)
    fun searchQuestionWithTitle(
        @Query("intitle") intitle: String,
        @Query("order") order: String = "asc",
        @Query("sort") sort: String = "activity",
        @Query("site") site: String = "stackoverflow",
        @Query("pagesize") pagesize: Int = 12,
        @Query("filter") filter: String = "!nKzQUR3Egv"
    ): Call<StackOverflowQuestionResponse>

    @GET(ANSWERS_OF_QUESTION_END_POINT)
    fun answersOfQuestion(
        @Path("question_id") question_id: Int,
        @Query("order") order: String = "asc",
        @Query("sort") sort: String = "activity",
        @Query("site") site: String = "stackoverflow",
        @Query("filter") filter: String = "!nKzQURF6Y5"
    ): Call<StackOverflowAnswersResponse>
}


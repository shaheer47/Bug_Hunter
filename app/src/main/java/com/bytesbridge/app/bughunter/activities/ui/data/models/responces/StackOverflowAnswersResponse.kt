package com.bytesbridge.app.bughunter.activities.ui.data.models.responces

import com.google.gson.annotations.SerializedName


data class StackOverflowAnswersResponse(
    @SerializedName("items") val items: List<StackOverflowAnswers>,
    @SerializedName("has_more") val has_more: Boolean,
    @SerializedName("quota_max") val quota_max: Int,
    @SerializedName("quota_remaining") val quota_remaining: Int
)
data class StackOverflowAnswers (

    @SerializedName("owner") val owner : Owner,
    @SerializedName("is_accepted") val is_accepted : Boolean,
    @SerializedName("score") val score : Int,
    @SerializedName("last_activity_date") val last_activity_date : Int,
    @SerializedName("creation_date") val creation_date : Int,
    @SerializedName("answer_id") val answer_id : Int,
    @SerializedName("question_id") val question_id : Int,
    @SerializedName("content_license") val content_license : String,
    @SerializedName("body") val body : String
)


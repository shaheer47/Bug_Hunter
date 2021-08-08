package com.bytesbridge.app.bughunter.activities.ui.data.models.responces

import com.google.gson.annotations.SerializedName

data class SearchResponse(

    @SerializedName("results") val results: List<Results>,
    @SerializedName("image_results") val image_results: List<String>,
    @SerializedName("total") val total: Int,
    @SerializedName("answers") val answers: List<String>,
    @SerializedName("ts") val ts: Double
)

data class Results(

    @SerializedName("title") val title: String,
    @SerializedName("link") val link: String,
    @SerializedName("description") val description: String,
    @SerializedName("additional_links") val additional_links: List<AdditionalLinks>,
    @SerializedName("cite") val cite: Cite
)

data class Cite(

    @SerializedName("domain") val domain: String,
    @SerializedName("span") val span: String
)

data class AdditionalLinks(

    @SerializedName("text") val text: String,
    @SerializedName("href") val href: String
)



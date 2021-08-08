package com.bytesbridge.app.bughunter.activities.`interface`

import com.bytesbridge.app.bughunter.activities.ui.data.models.responces.SearchResponse
import com.bytesbridge.app.bughunter.activities.utils.Constants.Companion.SEARCH_END_POINT
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface IBackendApiSearch {
    @GET("${SEARCH_END_POINT}q={query}&num=5")
    fun searchQuestion(@Path("query") query:String): Call<SearchResponse>

}
package com.dicoding.asclepius.newsapi

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApiService {
    @GET("top-headlines")
    fun getNews(
        @Query("q") query: String,
        @Query("category") category: String,
        @Query("language") language: String,
        @Query("apiKey") apiKey: String
    ): Call<NewsResponse>
}

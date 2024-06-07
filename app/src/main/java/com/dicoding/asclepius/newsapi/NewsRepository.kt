package com.dicoding.asclepius.newsapi

import com.dicoding.asclepius.newsapi.NewsApiConfig.API_KEY
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.dicoding.asclepius.newsapi.NewsApiClient.instance as apiClient

class NewsRepository {
    fun getNews(
        onSuccess: (List<NewsItem>) -> Unit,
        onFailure: (String) -> Unit
    ) {
        apiClient.getNews("cancer", "health", "en", API_KEY)
            .enqueue(object : Callback<NewsResponse> {
                override fun onResponse(
                    call: Call<NewsResponse>,
                    response: Response<NewsResponse>
                ) {
                    if (response.isSuccessful) {
                        val articles = response.body()?.articles ?: emptyList()
                        val newsList = articles.mapNotNull { article ->
                            if (!article.title.isNullOrEmpty() && !article.urlToImage.isNullOrEmpty() && !article.url.isNullOrEmpty()) {
                                NewsItem(article.title, article.urlToImage, article.description)
                            } else {
                                null
                            }
                        }
                        onSuccess(newsList)
                    } else {
                        onFailure("fetch news failed")
                    }
                }

                override fun onFailure(call: Call<NewsResponse>, t: Throwable) {
                    onFailure(t.message ?: "error")
                }
            })
    }
}

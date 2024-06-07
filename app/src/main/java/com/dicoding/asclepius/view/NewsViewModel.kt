package com.dicoding.asclepius.view


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.asclepius.newsapi.NewsItem
import com.dicoding.asclepius.newsapi.NewsRepository
import kotlinx.coroutines.launch

class NewsViewModel : ViewModel() {
    private val repository = NewsRepository()
    val news = MutableLiveData<List<NewsItem>>()
    private val errorMessage = MutableLiveData<String>()

    fun getNews() {
        viewModelScope.launch {
            repository.getNews(
                onSuccess = { newsList ->
                    news.postValue(newsList)
                },
                onFailure = { error ->
                    errorMessage.postValue(error)
                }
            )
        }
    }
}
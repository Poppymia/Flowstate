package com.example.flowstate.models

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flowstate.remote.RetrofitInstance
import kotlinx.coroutines.launch

class QuoteViewModel : ViewModel() {

    private val _quote = mutableStateOf("")
    val quote: State<String> = _quote

    private val _author = mutableStateOf("")
    val author: State<String> = _author

    init {
        fetchQuote()
    }

    private fun fetchQuote() {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.getRandomQuote()

                //call returns a LIST with 1 item!!
                val first = response.first()

                _quote.value = first.quote
                _author.value = first.author

            } catch (e: Exception) {
                //double check api call works
                _quote.value = "Failed to load motivation"
                _author.value = ""
            }
        }
    }
}

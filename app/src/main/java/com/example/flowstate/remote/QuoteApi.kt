package com.example.flowstate.remote

import retrofit2.http.GET
import com.example.flowstate.models.Quote

interface QuoteApi {
    @GET("api/random")
    suspend fun getRandomQuote(): List<Quote>
}

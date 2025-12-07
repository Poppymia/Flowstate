package com.example.flowstate.models

import com.google.gson.annotations.SerializedName
//@SerializedName is used to map the JSON keys to the properties of the data class

data class Quote(
    @SerializedName("q") val quote: String,
    @SerializedName("a") val author: String
)

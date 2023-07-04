package com.example.remak.network.model

data class AuthEmailRequestBody (
    val email : String,
        )

data class AuthEmailErrorResponseBody (
    val statusCode : Int,
    val message : Any,
    val error : String
        )


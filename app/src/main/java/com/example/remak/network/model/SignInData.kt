package com.example.remak.network.model

class SignInData {

    data class RequestBody(val email: String, val password: String)
    data class ResponseBody(val accessToken: String)
    data class ErrorResponse(val statusCode: Int, val message: List<String>, val error: String)

}
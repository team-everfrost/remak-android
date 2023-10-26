package com.everfrost.remak.network.model

class SignInData {

    data class RequestBody(val email: String, val password: String)
    data class ResponseBody(
        val message: String,
        val data: TokenData?
    )

    data class TokenData(
        val accessToken: String
    )

    data class CheckEmailRequest(
        val email: String
    )

    data class CheckEmailResponse(
        val message: String,
        val data: String?
    )

}
package com.everfrost.remak.network.model

class SignUpData {

    data class GetVerifyRequestBody(
        val email: String
    )

    data class GetVerifyResponseBody(
        val message: String,
        val data: Any?

    )

    data class CheckVerifyRequestBody(
        val code: String,
        val email: String
    )

    data class CheckVerifyResponseBody(
        val message: String,
        val data: Any?
    )

    data class SignUpRequestBody(
        val email: String,
        val password: String,
    )

    data class SignUpResponseBody(
        val message: String,
        val data: TokenData?
    )

    data class TokenData(
        val accessToken: String
    )

    data class WithdrawVerifyRequestBody(
        val code: String
    )

    data class WithdrawVerifyResponseBody(
        val message: String,
        val data: Any?
    )
}


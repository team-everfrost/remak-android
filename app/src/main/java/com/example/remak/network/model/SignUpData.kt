package com.example.remak.network.model

import com.google.gson.annotations.SerializedName


class SignUpData {

    data class GetVerifyRequestBody (
        val email : String
            )

    data class GetVerifyResponseBody (
        val message : String,
        val data : Any?

            )

    data class CheckVerifyRequestBody (
        val signupCode : String,
        val email : String
            )
    data class CheckVerifyResponseBody (
        val message : String,
        val data : Any?
            )
}


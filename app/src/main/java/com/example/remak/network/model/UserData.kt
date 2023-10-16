package com.example.remak.network.model

class UserData {
    data class Response(
        val message: String,
        val data: Data
    )

    data class Data(
        val name: String,
        val email: String,
        val imageUrl: String?,
        val role: String
    )

    data class StorageData(
        val message: String,
        val data: Int
    )
}
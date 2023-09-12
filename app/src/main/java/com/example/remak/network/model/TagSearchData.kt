package com.example.remak.network.model

class TagSearchData {

    data class Response(
        val message: String,
        val data: List<Data>
    )

    data class Data(
        val name: String,
        val count: Int,
    )
}
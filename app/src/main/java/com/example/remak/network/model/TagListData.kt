package com.example.remak.network.model

class TagListData {

    data class Response(
        val message: String,
        var data: List<Data>
    )

    data class Data(
        val name: String,
        val count: Int,
        var type: String?
    )
}
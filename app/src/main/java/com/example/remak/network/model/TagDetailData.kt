package com.example.remak.network.model


class TagDetailData {

    data class Response(
        val message: String,
        val data: List<Data>
    )

    data class Data(
        val docId: String?,
        val title: String?,
        val type: String?,
        var url: String?,
        var content: String?,
        val summary: String?,
        val status: String?,
        val thumbnailUrl: String?,
        val createdAt: String?,
        var updatedAt: String?,
        val tags: List<String?>,
    )

}
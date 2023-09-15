package com.example.remak.network.model

class MainListData {

    data class Response(
        val message: String,
        val data: List<Data>
    )

    data class DetailResponse(
        val message: String,
        val data: Data
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
        var isSelected: Boolean = false,
        var header: String? = null,
    )

}
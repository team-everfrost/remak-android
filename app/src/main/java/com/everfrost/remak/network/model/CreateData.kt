package com.everfrost.remak.network.model

class CreateData {

    data class MemoRequestBody(
        val content: String,
    )

    data class MemoResponseBody(
        val message: String,
        val data: MemoData

    )

    data class MemoData(
        val docId: String,
        val title: String?,
        val type: String,
        val url: String?,
        val content: String,
        val summary: String?,
        val status: String,
        val thumbnailUrl: String?,
        val createdAt: String,
        val updatedAt: String,
        val tags: List<String>

    )

    data class WebPageRequestBody(
        val title: String,
        val url: String,
        val content: String
    )

    data class WebPageResponseBody(
        val message: String,
        val data: WebPageData

    )

    data class WebPageData(
        val docId: String,
        val title: String,
        val type: String,
        val url: String,
        val content: String,
        val summary: String?,
        val status: String,
        val thumbnailUrl: String?,
        val createdAt: String,
        val updatedAt: String,
        val tags: List<String>
    )

}
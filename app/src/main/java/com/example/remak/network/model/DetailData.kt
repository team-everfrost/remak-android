package com.example.remak.network.model

class DetailData {

    data class ResponseBody(
        val message : String,
        val data : Data
    )

    data class Data(
        val docId: String,
        val title: String?,
        val type: String,
        val url: String?,
        val content: String,
        val summary: String?,
        val thumbnailUrl: String?,
        val status: String,
        val createdAt: String,
        val updatedAt: String,
        var tags: List<String>

    )

    //{
    //  "docId": "f7a3d8e0-9a9b-4c4b-9b9b-9b9b9b9b9b9b",
    //  "title": "Title",
    //  "type": "WEBPAGE",
    //  "url": "https://www.google.com",
    //  "content": "Content",
    //  "summary": "Summary",
    //  "status": "PENDING",
    //  "createdAt": "2021-08-01T00:00:00.000Z",
    //  "updatedAt": "2021-08-01T00:00:00.000Z",
    //  "tags": [
    //    "tag1",
    //    "tag2"
    //  ]
    //}
}
package com.example.remak.network.model

class UploadFileData {


    data class RequestBody(
        val files : List<Any?>
    )

    data class ResponseBody(
        val message : String,
        val data : List<Data>
    )


    data class Data (
        val docId : String,
        val title : String?,
        val type : String,
        val url : String?,
        val content : String?,
        val summary : String?,
        val status : String,
        val createdAt : String,
        val updatedAt : String,
        val tags : List<String>
            )

}
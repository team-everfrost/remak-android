package com.example.remak.network.model

class MainListData {

    data class Response(
        val message : String,
        val data : List<Data>
    )

    data class Data(
        val docId : String?,
        val title : String?,
        val type : String?,
        var url : String?,
        var content : String?,
        val summary : String?,
        val status : String?,
        val createdAt : String?,
        val updatedAt : String?,
        val tags : List<String?>,
        var isSelected : Boolean = false,
        var header : String? = null,

        )




}
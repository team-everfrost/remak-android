package com.everfrost.remak.network.model

class CreateCollectionData {

    data class RequestBody(
        val name: String,
        val description: String?,
    )

    data class ResponseBody(
        val message: String,
        val data: Data
    )

    data class Data(
        val name: String,
        val description: String?,
        val count: Int,
    )
}
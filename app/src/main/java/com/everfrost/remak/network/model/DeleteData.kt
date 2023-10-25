package com.everfrost.remak.network.model

class DeleteData {

    data class ResponseBody(
        val message: String,
        val data: Any?
    )
}
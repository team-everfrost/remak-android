package com.everfrost.remak.network.model

class DownloadData {
    data class ResponseBody(
        val message: String,
        val data: String?
    )
}
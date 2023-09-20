package com.example.remak.network.model

class AddDataInCollectionData {
    data class AddRequestBody(
        val docIds: List<String>
    )

    data class AddResponse(
        val message: String,
        val data: String?
    )

    data class RemoveRequestBody(
        val removedDocIds: List<String>
    )

    data class RemoveResponse(
        val message: String,
        val data: Data
    )

    data class UpdateCollectionRequestBody(
        val newName: String,
        val description: String?
    )

    data class Data(
        val name: String,
        val description: String?,
        val count: Int,
    )

}
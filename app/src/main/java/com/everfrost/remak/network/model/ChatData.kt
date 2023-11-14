package com.everfrost.remak.network.model

class ChatData {

    data class ChatMessage(
        val message: String,
        val role: Role
    )

    enum class Role {
        USER, BOT
    }
}
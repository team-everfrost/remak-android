package com.everfrost.remak.view.search

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.everfrost.remak.dataStore.TokenRepository
import com.everfrost.remak.network.model.ChatData
import com.everfrost.remak.repository.NetworkRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.sse.EventSource
import okhttp3.sse.EventSourceListener
import okhttp3.sse.EventSources
import javax.inject.Inject


@HiltViewModel
class ChatBotViewModel @Inject constructor(
    private val tokenRepository: TokenRepository,
    private val networkRepository: NetworkRepository
) : ViewModel() {
    private lateinit var token: String

    private val _chatMessages = MutableLiveData<MutableList<ChatData.ChatMessage>>().apply {
        value = mutableListOf()
    }
    val chatMessages: LiveData<MutableList<ChatData.ChatMessage>> = _chatMessages

    private val _isBotTyping = MutableLiveData<Boolean>().apply {
        value = false
    }
    val isBotTyping: LiveData<Boolean> = _isBotTyping

    private val _isChatTimeout = MutableLiveData<Boolean>().apply {
        value = false
    }
    val isChatTimeout: LiveData<Boolean> = _isChatTimeout


    private var eventSource: EventSource? = null
    private var currentMessageContent = StringBuilder()


    init {
        viewModelScope.launch {
            token = tokenRepository.fetchTokenData()?.accessToken ?: ""
        }

        _chatMessages.value = mutableListOf(
            ChatData.ChatMessage(
                "안녕하세요 Genine입니다. \n" +
                        "무엇이든 물어보세요 제가 도와드릴게요", ChatData.Role.BOT
            )
        )
    }

    fun resetTimeout() {
        _isChatTimeout.value = false
    }

    fun startChat(query: String) {
        _isBotTyping.value = true
        val encodedQuery = Uri.encode(query)
        val url = "https://api-dev.remak.io/chat/rag?query=$encodedQuery"

        val userMessage = ChatData.ChatMessage(
            query,
            ChatData.Role.USER
        )
        _chatMessages.value?.add(userMessage)
        val newMessage = ChatData.ChatMessage(
            "",
            ChatData.Role.BOT
        )
        _chatMessages.value?.add(newMessage)
        val request = Request.Builder().url(url)
            .addHeader("Authorization", "Bearer $token").build()
        val client = OkHttpClient.Builder().build()
        EventSources.createFactory(client).newEventSource(request, object : EventSourceListener() {
            override fun onEvent(
                eventSource: EventSource,
                id: String?,
                type: String?,
                data: String
            ) {
                currentMessageContent.append(data)

                // 현재 수신 중인 메시지를 업데이트하기 위한 임시 리스트 생성
                val updatedMessages = _chatMessages.value.orEmpty().toMutableList()
                val lastMessageIndex = updatedMessages.lastIndex
                if (lastMessageIndex >= 0) {
                    // 마지막 메시지의 내용을 업데이트합니다.
                    updatedMessages[lastMessageIndex] =
                        ChatData.ChatMessage(currentMessageContent.toString(), ChatData.Role.BOT)
                    _chatMessages.postValue(updatedMessages) // LiveData 업데이트
                }


            }

            override fun onClosed(eventSource: EventSource) {
                currentMessageContent.clear()
                viewModelScope.launch {
                    _isBotTyping.value = false
                }
            }

            override fun onFailure(eventSource: EventSource, t: Throwable?, response: Response?) {
                super.onFailure(eventSource, t, response)
                Log.d("ChatBotViewModel", "onFailure: $t")
                viewModelScope.launch {
                    _isChatTimeout.value = true
                }
            }

        })
    }
}


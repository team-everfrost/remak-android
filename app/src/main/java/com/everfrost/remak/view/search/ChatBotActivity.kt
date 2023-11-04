package com.everfrost.remak.view.search

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.everfrost.remak.adapter.TestChatBot
import com.everfrost.remak.adapter.TestChatBotRVAdapter
import com.everfrost.remak.databinding.SearchChatBotActivityBinding

class ChatBotActivity : AppCompatActivity() {
    private lateinit var binding: SearchChatBotActivityBinding
    private lateinit var adapter: TestChatBotRVAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SearchChatBotActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val data: TestChatBot.TestChatBotData =
            TestChatBot.TestChatBotData("BOT", "안녕하세요zzzzㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋ")
        val userData = TestChatBot.TestChatBotData("USER", "안녕하세요ㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋ")


        adapter = TestChatBotRVAdapter(listOf(data, userData, data, userData))
        binding.chatRv.adapter = adapter
        binding.chatRv.layoutManager = LinearLayoutManager(this)

    }


}
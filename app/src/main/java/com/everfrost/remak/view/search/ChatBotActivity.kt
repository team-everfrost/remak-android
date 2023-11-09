package com.everfrost.remak.view.search

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.everfrost.remak.R
import com.everfrost.remak.UtilitySystem
import com.everfrost.remak.adapter.ChatBotItemOffsetDecoration
import com.everfrost.remak.adapter.TestChatBotRVAdapter
import com.everfrost.remak.dataStore.TokenRepository
import com.everfrost.remak.databinding.SearchChatBotActivityBinding

class ChatBotActivity : AppCompatActivity() {
    private lateinit var binding: SearchChatBotActivityBinding
    private lateinit var adapter: TestChatBotRVAdapter
    private val viewModel: ChatBotViewModel by viewModels { ChatBotViewModelFactory(tokenRepository) }
    lateinit var tokenRepository: TokenRepository
    private var botTyping = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tokenRepository = TokenRepository((this.application as com.everfrost.remak.App).dataStore)
        binding = SearchChatBotActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)



        viewModel.chatMessages.observe(this) {
            adapter.dataSet = it
            adapter.notifyDataSetChanged()
            binding.chatRv.scrollToPosition(it.size - 1)
        }
        adapter = TestChatBotRVAdapter(listOf())
        val itemDecoration = ChatBotItemOffsetDecoration(40)
        binding.chatRv.adapter = adapter
        binding.chatRv.addItemDecoration(itemDecoration)
        binding.chatRv.layoutManager = LinearLayoutManager(this)

        viewModel.isBotTyping.observe(this) {
            botTyping = it
        }

        binding.userInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s.toString().isNotEmpty() && !botTyping) {
                    binding.searchBtn.isEnabled = true
                    binding.searchBtn.setImageResource(R.drawable.chat_bot_search_available_icon)
                } else {
                    binding.searchBtn.isEnabled = false
                    binding.searchBtn.setImageResource(R.drawable.chat_bot_search_unavailable_icon)
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                //do nothing
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                //do nothing
            }
        })

        binding.searchBtn.setOnClickListener {
            val userInput = binding.userInput.text.toString()
            viewModel.startChat(userInput)
            binding.userInput.text!!.clear()
            UtilitySystem.hideKeyboard(this)
            adapter.notifyDataSetChanged()
        }

        binding.backButton.setOnClickListener {
            finish()
        }
    }
}
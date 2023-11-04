package com.everfrost.remak.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.everfrost.remak.R


class TestChatBot {
    data class TestChatBotData(
        val role: String,
        val message: String
    )
}

class TestChatBotRVAdapter(
    var dataSet: List<TestChatBot.TestChatBotData>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val BOT = "BOT"
        private const val USER = "USER"
        private const val BOT_VIEW_TYPE = 0
        private const val USER_VIEW_TYPE = 1
    }

    inner class BotViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val message: TextView = view.findViewById(R.id.botMessage)
    }

    inner class UserViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val message: TextView = view.findViewById(R.id.userMessage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            BOT_VIEW_TYPE -> {
                val inflater = LayoutInflater.from(parent.context)
                val view = inflater.inflate(R.layout.item_genine_chat, parent, false)
                BotViewHolder(view)
            }

            USER_VIEW_TYPE -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_user_chat, parent, false)
                UserViewHolder(view)
            }

            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun getItemViewType(position: Int): Int = when (dataSet[position].role) {
        BOT -> BOT_VIEW_TYPE
        USER -> USER_VIEW_TYPE
        else -> throw IllegalArgumentException("Invalid type of data $position")
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is BotViewHolder -> {
                holder.message.text = dataSet[position].message
            }

            is UserViewHolder -> {
                holder.message.text = dataSet[position].message
            }
        }
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

}
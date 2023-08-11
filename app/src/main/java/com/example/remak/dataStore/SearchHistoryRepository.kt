package com.example.remak.dataStore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.IOException

class SearchHistoryRepository(private val dataStore: DataStore<Preferences>) {



    object PreferencesKeys {
        val SEARCH_HISTORY = stringPreferencesKey("search_history")
    }


    fun addSearchHistory(history: String, currentList: List<String>): List<String> {
        val newList = mutableListOf<String>()
        newList.addAll(currentList)

        // 이미 리스트에 해당 검색 기록이 있다면 제거
        newList.remove(history)

        // 맨 앞에 검색 기록 추가
        newList.add(0, history)

        return newList
    }



    // 검색 기록 삭제
    suspend fun deleteSearchQuery(query: String) {
        dataStore.edit { preferences ->
            val json = preferences[PreferencesKeys.SEARCH_HISTORY] ?: ""
            val currentHistory = if (json.isEmpty()) {
                emptyList()
            } else {
                jsonToList(json)
            }

            val updatedHistory = currentHistory.filter { it != query }
            preferences[PreferencesKeys.SEARCH_HISTORY] = listToJson(updatedHistory)
        }
    }


    // Gson 인스턴스 초기화
    val gson = Gson()

    // List -> JSON 문자열
    fun listToJson(list: List<String>): String {
        return gson.toJson(list)
    }

    // JSON 문자열 -> List
    fun jsonToList(json: String): List<String> {
        return gson.fromJson(json, object : TypeToken<List<String>>() {}.type)
    }


    suspend fun saveSearchHistory(history: String) {
        val currentHistory = fetchSearchHistory()
        val updatedHistory = addSearchHistory(history, currentHistory)

        dataStore.edit { preferences ->
            preferences[PreferencesKeys.SEARCH_HISTORY] = listToJson(updatedHistory)
        }
    }

    suspend fun fetchSearchHistory(): List<String> {
        val json = dataStore.data.first()[PreferencesKeys.SEARCH_HISTORY] ?: ""
        return if (json.isEmpty()) {
            emptyList()
        } else {
            jsonToList(json)
        }
    }

}
package com.example.remak.signIn

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.remak.data.TokenData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map


class SignInRepository(private val dataStore: DataStore<Preferences>) {
    object PreferencesKeys {
        val ACCESS_TOKEN = stringPreferencesKey("access_token")
        val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
    }

    suspend fun saveUser(user : TokenData) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.ACCESS_TOKEN] = user.accessToken
            preferences[PreferencesKeys.REFRESH_TOKEN] = user.refreshToken
        }
    }

    suspend fun fetchTokenData () : TokenData? {
        var tokenData : TokenData? = null
        user.collect {data ->
            tokenData = data
        }
        return tokenData
    }


    val user : Flow<TokenData?> = dataStore.data
        .catch { exception ->
            if (exception is Exception) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            val accessToken = preferences[PreferencesKeys.ACCESS_TOKEN] ?: ""
            val refreshToken = preferences[PreferencesKeys.REFRESH_TOKEN] ?: ""
            if (accessToken.isNotEmpty() && refreshToken.isNotEmpty()) {
                TokenData(accessToken, refreshToken)
            } else {
                null
            }
        }

}
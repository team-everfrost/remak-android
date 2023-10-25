package com.everfrost.remak.dataStore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.everfrost.remak.model.TokenData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class TokenRepository(private val dataStore: DataStore<Preferences>) {
    object PreferencesKeys {
        val ACCESS_TOKEN = stringPreferencesKey("access_token")
        val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
    }

    //토큰 정보를 저장하는 함수
    suspend fun saveUser(user: TokenData) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.ACCESS_TOKEN] = user.accessToken
//            preferences[PreferencesKeys.REFRESH_TOKEN] = user.refreshToken
        }
    }

    //토큰 정보를 삭제하는 함수
    suspend fun deleteUser() {
        dataStore.edit { preferences ->
            preferences.remove(PreferencesKeys.ACCESS_TOKEN)
        }
    }

    //저장된 토큰 정보 가져오기
    suspend fun fetchTokenData(): TokenData? {
        return user.first()
    }

    //토큰유무 확인
    suspend fun checkToken(): Boolean {
        return fetchTokenData() != null
    }

    //토큰정보를 갖는 변수
    val user: Flow<TokenData?> = dataStore.data
        .catch { exception ->
            if (exception is Exception) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            val accessToken = preferences[PreferencesKeys.ACCESS_TOKEN] ?: ""
//            val refreshToken = preferences[PreferencesKeys.REFRESH_TOKEN] ?: ""
            if (accessToken.isNotEmpty()) {
//                TokenData(accessToken, refreshToken)
                TokenData(accessToken)

            } else {
                null
            }
        }

}
package com.everfrost.remak.network

import com.everfrost.remak.dataStore.TokenRepository
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

class AuthInterceptor @Inject constructor(private val tokenRepository: TokenRepository) :
    Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()

        runBlocking {
            val tokenData = tokenRepository.fetchTokenData()
            if (tokenData != null) {
                request = request.newBuilder()
                    .addHeader("Authorization", "Bearer ${tokenData.accessToken}")
                    .build()
            }
        }

        val response = chain.proceed(request)
        if (response.code == 401) {
            EventBus.getDefault().post(TokenExpiredEvent())
            GlobalScope.launch { tokenRepository.deleteUser() }
            response.close()  // 응답을 닫아야 하는 경우에만 닫습니다.
        }

        return response  // 응답을 반환합니다.

//        return chain.proceed(request)
    }

}


class TokenExpiredEvent
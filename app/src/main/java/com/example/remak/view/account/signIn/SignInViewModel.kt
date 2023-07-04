package com.example.remak.view.account.signIn

import android.app.Activity
import android.net.Network
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.remak.dataStore.SignInRepository
import com.example.remak.network.Api
import com.example.remak.network.model.SignInData
import com.example.remak.repository.NetworkRepository
import com.google.gson.Gson
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignInViewModel(private val signInRepository: SignInRepository): ViewModel(){

    private val networkRepository = NetworkRepository()
    private val _loginResponse = MutableLiveData<SignInData.ResponseBody>()
    val loginResponse: LiveData<SignInData.ResponseBody> = _loginResponse
    private val _errorResponse = MutableLiveData<SignInData.ErrorResponse>()
    val errorResponse: LiveData<SignInData.ErrorResponse> = _errorResponse


    fun emailLogin(email : String, password : String) = viewModelScope.launch {
        try {
            val response = networkRepository.SingIn(email, password)
            if (response.isSuccessful) {
                _loginResponse.value = response.body()
                Log.d("success", response.body().toString())
            } else {
                Log.d("fail", response.errorBody().toString())
                Log.d("fail", Gson().fromJson(response.errorBody()?.string(), SignInData.ErrorResponse::class.java).toString())
            }
        } catch (e: Exception) {
            Log.d("networkError", e.toString())
        }
    }



    fun kakaoLogin(context: Activity) {

        // 카카오계정으로 로그인 공통 callback 구성
        // 카카오톡으로 로그인 할 수 없어 카카오계정으로 로그인할 경우 사용됨
        val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
            if (error != null) {
                Log.e("kakao", "카카오계정으로 로그인 실패", error)
            } else if (token != null) {
                Log.i("kakao", "카카오계정으로 로그인 성공 ${token.accessToken}")
            }
        }


        // 카카오톡이 설치되어 있으면 카카오톡으로 로그인, 아니면 카카오계정으로 로그인
        if (UserApiClient.instance.isKakaoTalkLoginAvailable(context)) {
            UserApiClient.instance.loginWithKakaoTalk(context) { token, error ->
                if (error != null) {
                    Log.e("kakao", "카카오톡으로 로그인 실패", error)

                    // 사용자가 카카오톡 설치 후 디바이스 권한 요청 화면에서 로그인을 취소한 경우,
                    // 의도적인 로그인 취소로 보고 카카오계정으로 로그인 시도 없이 로그인 취소로 처리 (예: 뒤로 가기)
                    if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                        return@loginWithKakaoTalk
                    }


                    // 카카오톡에 연결된 카카오계정이 없는 경우, 카카오계정으로 로그인 시도
                    UserApiClient.instance.loginWithKakaoAccount(context, callback = callback)
                } else if (token != null) {
                    Log.i("kakao", "카카오톡으로 로그인 성공 ${token.accessToken}")
                }
            }
        } else {
            UserApiClient.instance.loginWithKakaoAccount(context, callback = callback)
            Log.d("kakao", "설치X")
        }

    }

    //로그인 시 토큰을 불러오는 함수
    fun getToken() {
        viewModelScope.launch {
            val tokenData = signInRepository.fetchTokenData()

            if (tokenData != null) {
                val accessToken = tokenData.accessToken
                val refreshToken = tokenData.refreshToken

            }
        }
    }

}

class SignInViewModelFactory(private val signInRepository: SignInRepository) : ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SignInViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SignInViewModel(signInRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
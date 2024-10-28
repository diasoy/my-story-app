package com.example.mystoryapp.view.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mystoryapp.data.model.LoginDataAccount
import com.example.mystoryapp.data.model.RegisterDataAccount
import com.example.mystoryapp.data.model.ResponseLogin
import com.example.mystoryapp.data.retrofit.ApiConfig
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    private val _isLoadingLogin = MutableLiveData<Boolean>()
    val isLoadingLogin: LiveData<Boolean> = _isLoadingLogin

    private val _isErrorLogin = MutableLiveData<Boolean>()
    val isErrorLogin: LiveData<Boolean> = _isErrorLogin

    private val _messageLogin = MutableLiveData<String>()
    val messageLogin: LiveData<String> = _messageLogin

    private val _userLogin = MutableLiveData<ResponseLogin>()
    val userLogin: LiveData<ResponseLogin> = _userLogin

    private val _isLoadingRegist = MutableLiveData<Boolean>()
    val isLoadingRegist: LiveData<Boolean> = _isLoadingRegist

    private val _isErrorRegist = MutableLiveData<Boolean>()
    val isErrorRegist: LiveData<Boolean> = _isErrorRegist

    private val _messageRegist = MutableLiveData<String>()
    val messageRegist: LiveData<String> = _messageRegist

    fun getResponseLogin(loginDataAccount: LoginDataAccount) {
        viewModelScope.launch {
            _isLoadingLogin.value = true
            try {
                val response = ApiConfig.getApiService("").login(loginDataAccount)
                _userLogin.value = response
                _isErrorLogin.value = false
                _messageLogin.value = "Login berhasil!"
            } catch (e: Exception) {
                _isErrorLogin.value = true
                _messageLogin.value = "Email atau password yang anda masukan salah, silahkan coba lagi!"
            } finally {
                _isLoadingLogin.value = false
            }
        }
    }

    fun getResponseRegister(registerDataAccount: RegisterDataAccount) {
        viewModelScope.launch {
            _isLoadingRegist.value = true
            try {
                val response = ApiConfig.getApiService("").register(registerDataAccount)
                _isErrorRegist.value = false
                _messageRegist.value = "Registrasi berhasil!"
            } catch (e: Exception) {
                _isErrorRegist.value = true
                _messageRegist.value = "Registrasi gagal: ${e.message}"
            } finally {
                _isLoadingRegist.value = false
            }
        }
    }
}





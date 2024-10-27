package com.example.mystoryapp.data.retrofit

import com.example.mystoryapp.data.model.LoginDataAccount
import com.example.mystoryapp.data.model.RegisterDataAccount
import com.example.mystoryapp.data.model.ResponseDetail
import com.example.mystoryapp.data.model.ResponseLogin
import com.example.mystoryapp.data.model.ResponseStory
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part


interface ApiService {
    @POST("register")
     fun register(@Body requestRegister: RegisterDataAccount): Call<ResponseDetail>

    @POST("login")
     fun login(@Body requestLogin: LoginDataAccount): Call<ResponseLogin>

    @GET("stories")
     fun fetchAllStories(
        @Header("Authorization") token: String,
    ): Call<ResponseStory>

    @Multipart
    @POST("stories")
     fun uploadStory(
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Header("Authorization") token: String
    ): Call<ResponseDetail>
}
package com.example.mystoryapp.data.retrofit

import com.example.mystoryapp.data.model.LoginDataAccount
import com.example.mystoryapp.data.model.RegisterDataAccount
import com.example.mystoryapp.data.model.ResponseDetail
import com.example.mystoryapp.data.model.ResponseLogin
import com.example.mystoryapp.data.model.ResponseStory
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface ApiService {
    @POST("register")
    suspend fun register(@Body requestRegister: RegisterDataAccount): ResponseDetail

    @POST("login")
    suspend fun login(@Body requestLogin: LoginDataAccount): ResponseLogin

    @GET("stories")
    suspend fun fetchAllStories(
        @Query("page") page: Int = 1,
        @Query("size") size: Int = 20
    ): ResponseStory

    @Multipart
    @POST("stories")
    suspend fun uploadStory(
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
    ): ResponseDetail

    @GET("stories")
    suspend fun getStoriesWithLocation(
        @Query("location") location: Int = 1,
    ): ResponseStory
}

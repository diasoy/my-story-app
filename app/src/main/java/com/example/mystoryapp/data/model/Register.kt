package com.example.mystoryapp.data.model

import com.google.gson.annotations.SerializedName

data class RegisterDataAccount(
    @field:SerializedName("name")
    val name: String,

    @field:SerializedName("email")
    val email: String,

    @field:SerializedName("password")
    val password: String
)
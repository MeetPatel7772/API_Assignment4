package com.example.api_assignment4

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LoginResponse(
    @Json(name = "_id") val id: String?,
    @Json(name = "firstname") val firstname: String?,
    @Json(name = "lastname") val lastname: String?,
    @Json(name = "email") val email: String?,
    @Json(name = "token") val token: String?,
    @Json(name = "error") val error: String?
)

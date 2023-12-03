package com.example.api_assignment4

import android.net.wifi.hotspot2.pps.Credential
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
interface ApiService {

    @POST("login")
    fun loginUser(@Body credential: Map<String, String>) : Call<LoginResponse>


}
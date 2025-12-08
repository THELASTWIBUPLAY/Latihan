package com.example.latihan.repository

import com.example.latihan.entities.Catatan
import com.example.latihan.entities.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface CatatanRepository {
    @POST("catatan")
    suspend fun createCatatan(@Body catatan: Catatan): Response<Catatan>

    @GET("users")
    suspend fun getUsers(): Response<List<User>>

    @GET("catatan")
    suspend fun getCatatan(): Response<List<Catatan>>
}
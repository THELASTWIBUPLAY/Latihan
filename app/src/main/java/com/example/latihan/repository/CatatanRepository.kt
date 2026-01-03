package com.example.latihan.repository

import com.example.latihan.entities.Catatan
import com.example.latihan.entities.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface CatatanRepository {
    @POST("catatan")
    suspend fun createCatatan(@Body catatan: Catatan): Response<Catatan>

    @GET("users")
    suspend fun getUsers(): Response<List<User>>

    @GET("catatan")
    suspend fun getCatatan(): Response<List<Catatan>>

    @GET("catatan/{id}")
    suspend fun getCatatan(@Path("id")id: Int):Response<Catatan>

    @PUT("catatan/{id}")
    suspend fun editCatatan(@Path("id")id:Int, @Body catatan: Catatan): Response<Catatan>

    @DELETE("catatan/{id}")
    suspend fun deleteCatatan(@Path("id") id: Int): Response<Void>
}
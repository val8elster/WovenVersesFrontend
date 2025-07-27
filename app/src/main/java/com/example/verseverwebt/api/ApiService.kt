package com.example.verseverwebt.api

import com.example.verseverwebt.valueobjects.User
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    //all api calls / mappings from the backend with Path and Body and Return type
    @GET("users")
    fun getUsers(): Call<List<User>>

    @POST("users")
    fun createUser(@Body user: User): Call<Unit>

    @GET("users/rankings")
    fun getRankedUsers(): Call<List<User>>

    @PUT("users/calculate-rankings")
    fun calculateRankings(): Call<Unit>

    @PUT("users/{id}/chapter/{chapter}/time")
    fun updateChapterTime(@Path("id") id: Long, @Path("chapter") chapter: Int, @Body time: Float): Call<Unit>

    @GET("users/{id}/chapter/{chapter}/time")
    fun getChapterTime(@Path("id") id: Long, @Path("chapter") chapter: Int): Call<Float>

    @PUT("users/{id}/intro")
    fun updateIntroCompleted(@Path("id") id: Long): Call<Unit>

    @GET("users/{id}/intro")
    fun getIntroCompleted(@Path("id") id: Long): Call<Boolean>

    @GET("users/name/{name}")
    fun checkIfExistsName(@Path("name") name: String): Call<Boolean>

    @GET("users/mail/{mail}")
    fun checkIfExistsMail(@Path("mail") mail: String): Call<Boolean>

}

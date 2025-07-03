package com.abdulkadirkara.paginationsimple.data.service

import com.abdulkadirkara.paginationsimple.data.model.UserResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("api/")
    suspend fun getUsers(
        @Query("page") page: Int,
        @Query("results") results: Int,
        @Query("gender") gender: String? = null,
        @Query("nat") nat: String? = null,
    ): UserResponse
}
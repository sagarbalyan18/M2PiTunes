package com.example.m2p.data.remote

import com.example.m2p.data.models.SongResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("search")
    suspend fun getMovies(@Query("term") term: String) : Response<SongResponse>

}
package com.example.m2p.data.repository

import com.example.m2p.data.remote.ApiService
import javax.inject.Inject

//repo takes dao(for local) and apiservice(for remote) as its constructor parameter
class SongRepository @Inject constructor(val apiService: ApiService) {

    suspend fun getMovies(term: String) = apiService.getMovies(term)

}
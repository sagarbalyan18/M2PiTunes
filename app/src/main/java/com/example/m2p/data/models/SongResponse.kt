package com.example.m2p.data.models


import com.google.gson.annotations.SerializedName

data class SongResponse(
    @SerializedName("resultCount")
    val resultCount: Int,
    @SerializedName("results")
    val songResults: List<SongResult>
)
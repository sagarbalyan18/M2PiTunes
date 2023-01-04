package com.example.m2p.ui.screens.song_list

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.m2p.data.models.SongResult
import com.example.m2p.data.repository.SongRepository
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

//viewmodel takes repo as its constructor parameter
//so that it can fetch data from repo
@HiltViewModel
class SongViewModel @Inject constructor(
    val repository: SongRepository
) : ViewModel(){
    private val TAG = "sagar"

    private val _response = MutableLiveData<List<SongResult>>()
    val response : LiveData<List<SongResult>>
        get() = _response

    private val _selectedSong = MutableLiveData<SongResult>()
    val selectedSong : LiveData<SongResult>
        get() = _selectedSong

    private val _viewType = MutableLiveData<Int>()
    val viewType: LiveData<Int>
        get() = _viewType

    private val _searchTerm = MutableLiveData<String>()
    val searchTerm : LiveData<String>
        get() = _searchTerm

    init {
        getAllMovies()
    }

    fun setSelectedSong(song: SongResult){
        _selectedSong.value = song
    }

    fun setViewType(viewType : Int){
        _viewType.value = viewType
    }

    fun searchTerm(term: String){
        _searchTerm.value = term
        getAllMovies()
    }

    private fun getAllMovies() = viewModelScope.launch {
        val text = searchTerm.value ?: ""
        repository.getMovies(text).let {
            if(it.isSuccessful){
                Log.d(TAG, "getAllMovies: Success")
                Log.d(TAG, "getAllMovies: ${Gson().toJson(it.body())}")
                _response.value = it.body()?.songResults
            } else {
                Log.d(TAG, "getAllMovies: Some Error occured")
            }
        }
    }

}
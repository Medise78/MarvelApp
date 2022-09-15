package com.medise.marvelapp.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medise.marvelapp.data.model.character.CharacterModelResponse
import com.medise.marvelapp.repository.MarvelRepository
import com.medise.marvelapp.ui.state.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import okio.IOException
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class SearchCharacterViewModel @Inject constructor(
    private val repository: MarvelRepository
) : ViewModel() {

    private val _searchCharacter =
        MutableStateFlow<Resource<CharacterModelResponse>>(Resource.Empty())
    val searchCharacter: StateFlow<Resource<CharacterModelResponse>> get() = _searchCharacter

    fun fetch(search:String){
        viewModelScope.launch {
            safeFetch(search)
        }
    }

    private suspend fun safeFetch(search:String) {
        _searchCharacter.value = Resource.Loading()
        try {
            val result = repository.getList(search)
            _searchCharacter.value = handleResponse(result)
        }catch (t:Throwable){
            when(t){
                is IOException -> _searchCharacter.value = Resource.Error("Internet Error")
                else -> _searchCharacter.value = Resource.Error("Error")
            }
        }
    }

    private fun handleResponse(response: Response<CharacterModelResponse>): Resource<CharacterModelResponse> {
        if (response.isSuccessful) {
            response.body()?.let {
                return Resource.Success(it)
            }
        }
        return Resource.Error(response.message())
    }
}
package com.medise.marvelapp.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medise.marvelapp.data.model.character.CharacterModelResponse
import com.medise.marvelapp.repository.MarvelRepository
import com.medise.marvelapp.ui.state.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okio.IOException
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class ListCharacterViewModel @Inject constructor(
    private val repository: MarvelRepository
) : ViewModel() {

    private val _list = MutableStateFlow<Resource<CharacterModelResponse>>(Resource.Loading())
    val list: StateFlow<Resource<CharacterModelResponse>> get() = _list

    init {
        viewModelScope.launch {
            safeFetch()
        }
    }

    private suspend fun safeFetch() {
        try {
            val response = repository.getList()
            _list.value = handleResponse(response)
        } catch (t: Throwable) {
            when (t) {
                is IOException -> _list.value = Resource.Error("Internet Error")
                else -> _list.value = Resource.Error("Error")
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
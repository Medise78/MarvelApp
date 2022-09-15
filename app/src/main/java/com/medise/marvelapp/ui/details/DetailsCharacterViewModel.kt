package com.medise.marvelapp.ui.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medise.marvelapp.data.model.character.CharacterModel
import com.medise.marvelapp.data.model.comic.ComicModelResponse
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
class DetailsCharacterViewModel @Inject constructor(
    private val repository: MarvelRepository
) : ViewModel() {

    private val _details = MutableStateFlow<Resource<ComicModelResponse>>(Resource.Loading())
    val details:StateFlow<Resource<ComicModelResponse>> get() = _details

    fun fetch(characterId: Int) = viewModelScope.launch {
        safeArgs(characterId)
    }

    private suspend fun safeArgs(characterId:Int){
        try {
            val result = repository.getCharacterComic(characterId)
            _details.value = response(result)
        }catch (t:Throwable){
            when(t){
                is IOException -> _details.value = Resource.Error("Internet Error")
                else -> _details.value = Resource.Error("Error")
            }
        }
    }

    fun insert(characterModel: CharacterModel){
        viewModelScope.launch {
            repository.insert(characterModel)
        }
    }

    private fun response(response:Response<ComicModelResponse>):Resource<ComicModelResponse>{
        if (response.isSuccessful){
            response.body()?.let {
                return Resource.Success(it)
            }
        }
        return Resource.Error(response.message())
    }
}
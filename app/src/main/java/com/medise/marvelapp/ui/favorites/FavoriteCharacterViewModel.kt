package com.medise.marvelapp.ui.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medise.marvelapp.data.model.character.CharacterModel
import com.medise.marvelapp.repository.MarvelRepository
import com.medise.marvelapp.ui.state.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteCharacterViewModel @Inject constructor(
    private val repository: MarvelRepository,
) : ViewModel() {

    private val _favorites = MutableStateFlow<Resource<List<CharacterModel>>>(Resource.Empty())
    val favorites: StateFlow<Resource<List<CharacterModel>>> get() = _favorites

    init {
        fetch()
    }

    private fun fetch() = viewModelScope.launch {
        repository.getAll().collectLatest {
            if (it.isNullOrEmpty()){
                _favorites.value = Resource.Empty()
            }else{
                _favorites.value = Resource.Success(it.toList())
            }
        }
    }

    fun delete(characterModel: CharacterModel) {
        viewModelScope.launch {
            repository.delete(characterModel)
        }
    }
}
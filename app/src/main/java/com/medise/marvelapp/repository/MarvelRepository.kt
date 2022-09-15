package com.medise.marvelapp.repository

import com.medise.marvelapp.data.local.MarvelDao
import com.medise.marvelapp.data.model.character.CharacterModel
import com.medise.marvelapp.data.model.character.CharacterModelResponse
import com.medise.marvelapp.data.remote.ApiService
import retrofit2.Response
import javax.inject.Inject

class MarvelRepository @Inject constructor(
    private val apiService: ApiService,
    private val marvelDao: MarvelDao
) {
    suspend fun getList(nameStartsWith: String? = null) :Response<CharacterModelResponse>{
        return apiService.list(nameStartsWith)
    }

    suspend fun getCharacterComic(characterId: Int) =
        apiService.getComics(characterId)

    suspend fun insert(characterModel: CharacterModel) =
        marvelDao.insertCharacter(characterModel)

    fun getAll() = marvelDao.getAllCharacter()

    suspend fun delete(characterModel: CharacterModel) =
        marvelDao.deleteCharacter(characterModel)
}
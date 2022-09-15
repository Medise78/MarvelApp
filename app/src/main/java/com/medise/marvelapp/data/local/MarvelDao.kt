package com.medise.marvelapp.data.local

import androidx.room.*
import com.medise.marvelapp.data.model.character.CharacterModel
import kotlinx.coroutines.flow.Flow
import retrofit2.http.DELETE

@Dao
interface MarvelDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCharacter(characterModel: CharacterModel):Long

    @Query("SELECT * FROM characterModel ORDER BY id")
    fun getAllCharacter():Flow<List<CharacterModel>>

    @Delete
    suspend fun deleteCharacter(characterModel: CharacterModel)
}
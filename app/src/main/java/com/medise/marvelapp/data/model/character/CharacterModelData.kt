package com.medise.marvelapp.data.model.character

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class CharacterModelData(
    @SerializedName("results")
    val results: List<CharacterModel>
): Serializable

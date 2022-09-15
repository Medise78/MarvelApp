package com.medise.marvelapp.data.local

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.medise.marvelapp.data.model.ThumbnailModel

class MarvelConverters {

    @TypeConverter
    fun fromThumbnail(thumbnailModel: ThumbnailModel): String {
        return Gson().toJson(thumbnailModel)
    }

    @TypeConverter
    fun toThumbnail(thumbnailModel: String): ThumbnailModel {
        return Gson().fromJson(thumbnailModel, ThumbnailModel::class.java)
    }
}
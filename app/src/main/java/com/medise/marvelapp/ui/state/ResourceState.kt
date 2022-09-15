package com.medise.marvelapp.ui.state

sealed class Resource<T>(val message: String? = null, val data: T? = null) {
    class Success<T>(data: T) : Resource<T>(data = data)
    class Error<T>(message: String) : Resource<T>(message = message)
    class Loading<T>(data: T? = null) : Resource<T>(data = data)
    class Empty<T>() : Resource<T>()
}

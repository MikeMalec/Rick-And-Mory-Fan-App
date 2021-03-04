package com.example.rick_and_morty.data.utils

import android.util.Log
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException

suspend fun <T> safeApiCall(
    dispatcher: CoroutineDispatcher,
    apiCall: suspend () -> T?
): Resource<T?> {
    return withContext(dispatcher) {
        try {
            withTimeout(6000) {
                Resource.Success(apiCall())
            }
        } catch (throwable: Throwable) {
            Log.d("XXX", "$throwable")
            throwable.printStackTrace()
            when (throwable) {
                is TimeoutCancellationException -> {
                    Resource.Error("Error Timeout")
                }
                is IOException -> {
                    Resource.Error()
                }
                is HttpException -> {
                    val errorResponse = convertErrorBody(throwable)
                    Resource.Error(
                        errorResponse
                    )
                }
                else -> {
                    Resource.Error(
                        "Error Unknown"
                    )
                }
            }
        }
    }
}

suspend fun <T> safeCacheCall(
    dispatcher: CoroutineDispatcher,
    cacheCall: suspend () -> T
): Resource<T?> {
    return withContext(dispatcher) {
        try {
            withTimeout(6000) {
                Resource.Success(cacheCall.invoke())
            }
        } catch (throwable: Throwable) {
            throwable.printStackTrace()
            when (throwable) {
                is TimeoutCancellationException -> {
                    Resource.Error("Error Timeout")
                }
                else -> {
                    Resource.Error("Error Unknown")
                }
            }
        }
    }
}

private fun convertErrorBody(throwable: HttpException): String? {
    return try {
        val json = JSONObject(throwable.response()?.errorBody()?.string())
        json.getString("error")
    } catch (exception: Exception) {
        "Error Unknown"
    }
}

private fun convertErrorBody(error: String?): String? {
    return try {
        val json = JSONObject(error)
        return json.getString("error")
    } catch (exception: Exception) {
        "Error Unknown"
    }
}
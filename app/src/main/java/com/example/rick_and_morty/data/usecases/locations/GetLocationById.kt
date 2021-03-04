package com.example.rick_and_morty.data.usecases.locations

import com.example.rick_and_morty.data.locations.Location
import com.example.rick_and_morty.data.locations.LocationRemoteDataSource
import com.example.rick_and_morty.data.utils.Resource
import com.example.rick_and_morty.data.utils.safeApiCall
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetLocationById @Inject constructor(val locationRemoteDataSource: LocationRemoteDataSource) {
    fun getLocationById(id: Int): Flow<Location> = flow {
        val response = safeApiCall(IO) { locationRemoteDataSource.getLocationById(id) }
        if (response is Resource.Success) {
            response.data?.also {
                emit(it)
            }
        }
    }
}
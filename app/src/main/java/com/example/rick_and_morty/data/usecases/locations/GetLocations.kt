package com.example.rick_and_morty.data.usecases.locations

import com.example.rick_and_morty.data.locations.LocationRemoteDataSource
import com.example.rick_and_morty.data.locations.LocationsResponse
import com.example.rick_and_morty.data.utils.Resource
import com.example.rick_and_morty.data.utils.Resource.Loading
import com.example.rick_and_morty.data.utils.safeApiCall
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetLocations @Inject constructor(val locationRemoteDataSource: LocationRemoteDataSource) {
    fun getLocations(page: Int): Flow<Resource<LocationsResponse?>> = flow {
        emit(Loading())
        val response = safeApiCall(IO) { locationRemoteDataSource.getLocations(page) }
        emit(response)
    }
}
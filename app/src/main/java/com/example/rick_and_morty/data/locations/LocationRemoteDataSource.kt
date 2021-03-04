package com.example.rick_and_morty.data.locations

import com.example.rick_and_morty.data.RickAndMortyApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationRemoteDataSource @Inject constructor(val rickAndMortyApi: RickAndMortyApi) {
    suspend fun getLocations(page: Int) = rickAndMortyApi.getLocation(page)

    suspend fun getLocationCharacters(characters: List<Int>) =
        rickAndMortyApi.getLocationCharacters(characters)

    suspend fun getLocationByName(name: String) = rickAndMortyApi.getLocationByName(name)

    suspend fun getLocationById(id: Int) = rickAndMortyApi.getLocationById(id)
}
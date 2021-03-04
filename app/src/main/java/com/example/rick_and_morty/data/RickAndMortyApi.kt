package com.example.rick_and_morty.data

import com.example.rick_and_morty.data.characters.CharacterEpisodesResponse
import com.example.rick_and_morty.data.characters.CharactersResponse
import com.example.rick_and_morty.data.locations.LocationCharactersResponse
import com.example.rick_and_morty.data.locations.LocationsResponse
import com.example.rick_and_morty.data.episodes.EpisodesResponse
import com.example.rick_and_morty.data.locations.Location
import com.example.rick_and_morty.data.locations.Result
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RickAndMortyApi {
    @GET("character")
    suspend fun getCharacters(@Query("page") page: Int): CharactersResponse

    @GET("episode/{episodes}")
    suspend fun getCharacterEpisodes(@Path("episodes") episodes: List<Int>): CharacterEpisodesResponse

    @GET("location")
    suspend fun getLocation(@Query("page") page: Int): LocationsResponse

    @GET("character/{characters}")
    suspend fun getLocationCharacters(@Path("characters") characters: List<Int>): LocationCharactersResponse

    @GET("episode")
    suspend fun getEpisodes(@Query("page") page: Int): EpisodesResponse

    @GET("character")
    suspend fun getCharactersByName(@Query("name") name: String): CharactersResponse

    @GET("location")
    suspend fun getLocationByName(@Query("name") name: String): LocationsResponse

    @GET("location/{id}")
    suspend fun getLocationById(@Path("id") id: Int): Location
}
package com.example.rick_and_morty.data.characters

import android.util.Log
import com.example.rick_and_morty.data.RickAndMortyApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CharactersRemoteDataSource @Inject constructor(val rickAndMortyApi: RickAndMortyApi) {
    suspend fun getCharacters(page: Int): CharactersResponse = rickAndMortyApi.getCharacters(page)

    suspend fun getEpisodesById(ids: List<Int>): CharacterEpisodesResponse {
        return rickAndMortyApi.getCharacterEpisodes(ids)
    }

    suspend fun getCharacterByName(name: String) = rickAndMortyApi.getCharactersByName(name)
}
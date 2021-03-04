package com.example.rick_and_morty.data.usecases.characters

import com.example.rick_and_morty.data.characters.CharacterEpisodesResponse
import com.example.rick_and_morty.data.characters.CharactersRemoteDataSource
import com.example.rick_and_morty.data.utils.Resource
import com.example.rick_and_morty.data.utils.Resource.Loading
import com.example.rick_and_morty.data.utils.safeApiCall
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetCharacterEpisodes @Inject constructor(val charactersRemoteDataSource: CharactersRemoteDataSource) {
    fun getCharacterEpisodes(ids: List<Int>): Flow<Resource<CharacterEpisodesResponse?>> = flow {
        emit(Loading())
        val response = safeApiCall(IO) { charactersRemoteDataSource.getEpisodesById(ids) }
        emit(response)
    }
}
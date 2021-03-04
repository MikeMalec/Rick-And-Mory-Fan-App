package com.example.rick_and_morty.data.usecases.characters

import com.example.rick_and_morty.data.characters.CharactersRemoteDataSource
import com.example.rick_and_morty.data.characters.CharactersResponse
import com.example.rick_and_morty.data.utils.Resource
import com.example.rick_and_morty.data.utils.Resource.Loading
import com.example.rick_and_morty.data.utils.safeApiCall
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetCharacters @Inject constructor(
    val charactersRemoteDataSource: CharactersRemoteDataSource
) {
    fun getCharacters(page: Int): Flow<Resource<CharactersResponse?>> = flow {
        emit(Loading())
        val response = safeApiCall(IO) { charactersRemoteDataSource.getCharacters(page) }
        emit(response)
    }
}
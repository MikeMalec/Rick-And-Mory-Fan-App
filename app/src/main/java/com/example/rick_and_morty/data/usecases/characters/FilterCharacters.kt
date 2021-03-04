package com.example.rick_and_morty.data.usecases.characters

import android.util.Log
import com.example.rick_and_morty.data.characters.CharactersRemoteDataSource
import com.example.rick_and_morty.data.characters.CharactersResponse
import com.example.rick_and_morty.data.utils.Resource
import com.example.rick_and_morty.data.utils.Resource.Loading
import com.example.rick_and_morty.data.utils.safeApiCall
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class FilterCharacters @Inject constructor(
    val charactersRemoteDataSource: CharactersRemoteDataSource
) {
    fun filterCharacters(name: String): Flow<Resource<CharactersResponse?>> = flow {
        emit(Loading())
        val response = safeApiCall(IO) {
            charactersRemoteDataSource.getCharacterByName(name)
        }
        emit(response)
    }
}
package com.example.rick_and_morty.data.usecases.episodes

import com.example.rick_and_morty.data.episodes.EpisodeRemoteDataSource
import com.example.rick_and_morty.data.episodes.EpisodesResponse
import com.example.rick_and_morty.data.episodes.Result
import com.example.rick_and_morty.data.utils.Resource
import com.example.rick_and_morty.data.utils.Resource.Loading
import com.example.rick_and_morty.data.utils.safeApiCall
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetEpisodes @Inject constructor(val episodeRemoteDataSource: EpisodeRemoteDataSource) {
    suspend fun getEpisodes(): Flow<Resource<List<Result>?>> = flow {
        emit(Loading())
        val firstPage: Deferred<List<Result>?> = CoroutineScope(IO).async {
            val response = safeApiCall(IO) {
                episodeRemoteDataSource.getEpisodes(1)
            }
            if (response is Resource.Success && response.data != null) {
                return@async response.data.results
            } else {
                return@async null
            }
        }
        val secondPage: Deferred<List<Result>?> = CoroutineScope(IO).async {
            val response = safeApiCall(IO) { episodeRemoteDataSource.getEpisodes(2) }
            if (response is Resource.Success && response.data != null) {
                return@async response.data.results
            } else {
                return@async null
            }
        }
        val thirdPage: Deferred<List<Result>?> = CoroutineScope(IO).async {
            val response = safeApiCall(IO) { episodeRemoteDataSource.getEpisodes(3) }
            if (response is Resource.Success && response.data != null) {
                return@async response.data.results
            } else {
                return@async null
            }
        }
        val firstPageResponse = firstPage.await()
        val secondPageResponse = secondPage.await()
        val thirdPageResponse = thirdPage.await()
        val episodes = mutableListOf<Result>()
        firstPageResponse?.also { episodes.addAll(it) }
        secondPageResponse?.also { episodes.addAll(it) }
        thirdPageResponse?.also { episodes.addAll(it) }
        if (episodes.isEmpty()) {
            emit(Resource.Error("Something went wrong"))
        } else {
            emit(Resource.Success(episodes))
        }
    }
}
package com.example.rick_and_morty.data.episodes

import com.example.rick_and_morty.data.RickAndMortyApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EpisodeRemoteDataSource @Inject constructor(val rickAndMortyApi: RickAndMortyApi) {
    suspend fun getEpisodes(page: Int) = rickAndMortyApi.getEpisodes(page)
}
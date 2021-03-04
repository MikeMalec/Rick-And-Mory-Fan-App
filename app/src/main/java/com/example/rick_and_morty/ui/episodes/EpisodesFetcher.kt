package com.example.rick_and_morty.ui.episodes

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.rick_and_morty.data.episodes.Episode
import com.example.rick_and_morty.data.episodes.Result
import com.example.rick_and_morty.data.episodes.Season
import com.example.rick_and_morty.data.usecases.episodes.GetEpisodes
import com.example.rick_and_morty.data.utils.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class EpisodesFetcher(
    private val viewModelScope: CoroutineScope,
    private val getEpisodes: GetEpisodes
) {
    val episodeRequestState = Channel<Resource<List<Result>?>>()
    val seasons = MutableLiveData<List<Season>>()

    @RequiresApi(Build.VERSION_CODES.N)
    fun getEpisodes() {
        if (seasons.value == null) {
            viewModelScope.launch(Dispatchers.IO) {
                getEpisodes.getEpisodes().collect {
                    episodeRequestState.send(it)
                    if (it is Resource.Success) {
                        it.data?.also { eps -> dispatchEpisodesToSeasons(eps) }
                    }
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun dispatchEpisodesToSeasons(episodes: List<Result>) {
        episodes.forEach {
            val splittedSeason = it.episode.split('E')
            it.episodeNumber = splittedSeason[1].toInt()
            val seasonNumber = splittedSeason[0].substring(1, splittedSeason[0].length).toInt()
            it.seasonNumber = seasonNumber
        }
        val seasons: MutableMap<Int, List<Episode>> =
            episodes.groupBy { it.seasonNumber!! }.toMutableMap()
        val sortedSeasons = seasons.toSortedMap()
        sortedSeasons.forEach { i, list: List<Episode> ->
            sortedSeasons[i] = list.sortedBy { it.episodeNumber }
        }
        this.seasons.postValue(sortedSeasons.map { (key, value) -> Season(key, value) })
    }
}
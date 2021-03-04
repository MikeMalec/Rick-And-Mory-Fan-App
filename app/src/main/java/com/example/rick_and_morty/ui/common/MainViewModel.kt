package com.example.rick_and_morty.ui.common

import android.os.Build
import android.os.Parcelable
import androidx.annotation.RequiresApi
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rick_and_morty.data.characters.Character
import com.example.rick_and_morty.data.characters.CharactersResponse
import com.example.rick_and_morty.data.episodes.Episode
import com.example.rick_and_morty.data.episodes.Result
import com.example.rick_and_morty.data.episodes.Season
import com.example.rick_and_morty.data.locations.Location
import com.example.rick_and_morty.data.usecases.characters.GetCharacters
import com.example.rick_and_morty.data.locations.LocationsResponse
import com.example.rick_and_morty.data.usecases.characters.FilterCharacters
import com.example.rick_and_morty.data.usecases.episodes.GetEpisodes
import com.example.rick_and_morty.data.usecases.locations.FilterLocations
import com.example.rick_and_morty.data.usecases.locations.GetLocations
import com.example.rick_and_morty.data.utils.Resource
import com.example.rick_and_morty.ui.characters.CharactersPaginator
import com.example.rick_and_morty.ui.episodes.EpisodesFetcher
import com.example.rick_and_morty.ui.locations.LocationsPaginator
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


class MainViewModel @ViewModelInject constructor(
    private val getCharacters: GetCharacters,
    private val filterCharacters: FilterCharacters,
    private val getLocations: GetLocations,
    private val filterLocations: FilterLocations,
    private val getEpisodes: GetEpisodes
) : ViewModel() {
    // CHARACTERS
    val charactersPaginator = CharactersPaginator(viewModelScope, getCharacters, filterCharacters)

    val characters = charactersPaginator.characters
    val charactersRequestState = charactersPaginator.charactersRequestState

    val filteredCharacters = charactersPaginator.filteredCharacters
    val filteredCharactersRequestState = charactersPaginator.filteredCharactersRequestState

    var charactersScrollState: Parcelable? = null

    fun filteringCharacters(): Boolean {
        return charactersPaginator.query != null
    }

    // LOCATIONS
    val locationsPaginator = LocationsPaginator(viewModelScope, getLocations, filterLocations)

    val locations = locationsPaginator.locations
    val locationsRequestState = locationsPaginator.locationRequestState

    val filteredLocations = locationsPaginator.filteredLocations
    val filteredLocationsRequestState = locationsPaginator.filterLocationRequestState

    var locationsScrollState: Parcelable? = null

    fun filteringLocations(): Boolean {
        return locationsPaginator.query != null
    }

    // EPISODES
    val episodesFetcher = EpisodesFetcher(viewModelScope, getEpisodes)
    val seasons = episodesFetcher.seasons

    var episodesScrollState: Parcelable? = null
}
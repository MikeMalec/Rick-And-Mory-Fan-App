package com.example.rick_and_morty.ui.characters

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rick_and_morty.data.characters.CharacterEpisodesResponse
import com.example.rick_and_morty.data.characters.CharacterEpisodesResponseItem
import com.example.rick_and_morty.data.characters.Result
import com.example.rick_and_morty.data.locations.Location
import com.example.rick_and_morty.data.usecases.characters.GetCharacterEpisodes
import com.example.rick_and_morty.data.usecases.locations.GetLocationById
import com.example.rick_and_morty.data.utils.Resource
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class CharacterViewModel @ViewModelInject constructor(
    private val getCharacterEpisodes: GetCharacterEpisodes,
    private val getLocationById: GetLocationById
) : ViewModel() {

    val episodes = MutableLiveData<List<CharacterEpisodesResponseItem>>()
    val episodesRequestState = Channel<Resource<CharacterEpisodesResponse?>>()

    fun getEpisodes(result: Result) {
        val ids = result.episode.map { it.split("/episode/")[1] }.map { it.toInt() }
        viewModelScope.launch(IO) {
            getCharacterEpisodes.getCharacterEpisodes(ids).collect {
                episodesRequestState.send(it)
                if (it is Resource.Success) {
                    it.data?.also { eps ->
                        episodes.postValue(eps)
                    }
                }
            }
        }
    }

    var location: Location? = null

    fun getLocation(location: String) {
        val id = location.split("location/")[1].toInt()
        viewModelScope.launch(IO) {
            getLocationById.getLocationById(id).collect {
                this@CharacterViewModel.location = it
            }
        }
    }
}
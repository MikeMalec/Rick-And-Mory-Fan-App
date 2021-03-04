package com.example.rick_and_morty.ui.locations

import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rick_and_morty.data.locations.LocationCharactersResponse
import com.example.rick_and_morty.data.usecases.locations.GetSpecificCharacters
import com.example.rick_and_morty.data.utils.Resource
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class CharactersViewModel @ViewModelInject constructor(val getSpecificCharacters: GetSpecificCharacters) :
    ViewModel() {
    val charactersRequestState = Channel<Resource<LocationCharactersResponse?>>(Channel.CONFLATED)
    val characters = MutableLiveData<LocationCharactersResponse>()

    fun getEntityCharacters(charactersLinks: List<String>) {
        if (characters.value == null) {
            val ids = charactersLinks.map { it.split("character/")[1].toInt() }
            viewModelScope.launch(IO) {
                getSpecificCharacters.getLocationCharacters(ids).collect {
                    charactersRequestState.send(it)
                    if (it is Resource.Success) {
                        it.data?.also { locationCharactersResponse: LocationCharactersResponse ->
                            characters.postValue(locationCharactersResponse)
                        }
                    }
                }
            }
        }
    }
}
package com.example.rick_and_morty.ui.characters

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.rick_and_morty.data.characters.Character
import com.example.rick_and_morty.data.characters.CharactersResponse
import com.example.rick_and_morty.data.usecases.characters.FilterCharacters
import com.example.rick_and_morty.data.usecases.characters.GetCharacters
import com.example.rick_and_morty.data.utils.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class CharactersPaginator(
    private val viewModelScope: CoroutineScope,
    private val getCharacters: GetCharacters,
    private val filterCharacters: FilterCharacters
) {
    val characters = MutableLiveData<List<Character>>()
    private var fetchedCharacters = listOf<Character>()
    var charactersRequestState = Channel<Resource<CharactersResponse?>>(Channel.CONFLATED)
    private var charactersPage = 0
    private var charactersPages = 1
    private var fetchingCharacters = false

    fun fetchCharacters() {
        if (!fetchingCharacters && charactersPage <= charactersPages) {
            charactersPage++
            fetchingCharacters = true
            viewModelScope.launch(IO) {
                getCharacters.getCharacters(charactersPage).collect {
                    charactersRequestState.send(it)
                    if (it is Resource.Success) {
                        it.data?.also { characters -> dispatchNewCharacters(characters) }
                    }
                    fetchingCharacters = false
                }
            }
        }
    }

    fun fetchCharactersIfEmpty() {
        if (fetchedCharacters.isEmpty() && charactersPage < charactersPages) fetchCharacters()
    }

    private fun dispatchNewCharacters(response: CharactersResponse) {
        charactersPages = response.info.pages
        val allCharacters = mutableListOf<Character>()
        allCharacters.addAll(fetchedCharacters)
        allCharacters.addAll(response.results)
        fetchedCharacters = allCharacters
        characters.postValue(fetchedCharacters)
    }

    fun resetRequestStatus() {
        var charactersRequestState = Channel<Resource<CharactersResponse?>>(Channel.CONFLATED)
    }

    var query: String? = null
    private val queryChannel = Channel<String>(Channel.CONFLATED)

    init {
        observeQuery()
    }

    fun offerQuery(text: String) = queryChannel.offer(text)

    private fun observeQuery() {
        viewModelScope.launch(IO) {
            queryChannel.consumeAsFlow().debounce(250L).mapLatest {
                clearFiltered()
                query = it
                it
            }
                .collect { fetchFilteredCharacters() }
        }
    }

    val filteredCharacters = MutableLiveData<List<Character>>()
    private var fetchedFilteredCharacters = listOf<Character>()
    var filteredCharactersRequestState = Channel<Resource<CharactersResponse?>>(Channel.CONFLATED)
    private var filteredCharactersPage = 0
    private var filteredCharactersPages = 1
    private var filteredFetchingCharacters = false

    fun fetchFilteredCharacters() {
        if (!filteredFetchingCharacters && filteredCharactersPage <= filteredCharactersPages) {
            filteredCharactersPage++
            filteredFetchingCharacters = true
            viewModelScope.launch(IO) {
                filterCharacters.filterCharacters(query!!).collect {
                    filteredCharactersRequestState.offer(it)
                    if (it is Resource.Success) {
                        it.data?.also { characters -> dispatchFilteredCharacters(characters) }
                    }
                    if (it is Resource.Error) filteredFetchingCharacters = false
                }
            }
        }
    }

    private fun dispatchFilteredCharacters(response: CharactersResponse) {
        filteredFetchingCharacters = false
        filteredCharactersPages = response.info.pages
        val allCharacters = mutableListOf<Character>()
        allCharacters.addAll(fetchedFilteredCharacters)
        allCharacters.addAll(response.results)
        fetchedFilteredCharacters = allCharacters
        filteredCharacters.postValue(fetchedFilteredCharacters)
    }

    fun clearFiltered() {
        query = null
        filteredCharactersPage = 0
        filteredCharactersPages = 1
        fetchedFilteredCharacters = listOf()
        filteredFetchingCharacters = false
        characters.postValue(fetchedCharacters)
    }

    fun resetFilterRequestStatus() {
        filteredCharactersRequestState = Channel<Resource<CharactersResponse?>>(Channel.CONFLATED)
    }
}
package com.example.rick_and_morty.ui.locations

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.rick_and_morty.data.locations.Location
import com.example.rick_and_morty.data.locations.LocationsResponse
import com.example.rick_and_morty.data.usecases.locations.FilterLocations
import com.example.rick_and_morty.data.usecases.locations.GetLocations
import com.example.rick_and_morty.data.utils.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class LocationsPaginator(
    private val viewModelScope: CoroutineScope,
    private val getLocations: GetLocations,
    private val filterLocations: FilterLocations
) {
    val locations = MutableLiveData<List<Location>>()
    private var fetchedLocations = listOf<Location>()
    var locationRequestState = Channel<Resource<LocationsResponse?>>(Channel.CONFLATED)
    private var locationsPage = 0
    private var locationPages = 1
    private var fetchingLocations = false

    fun fetchLocations() {
        if (!fetchingLocations && locationsPage <= locationPages) {
            locationsPage++
            fetchingLocations = true
            viewModelScope.launch(Dispatchers.IO) {
                getLocations.getLocations(locationsPage).collect {
                    locationRequestState.send(it)
                    if (it is Resource.Success) {
                        it.data?.also { locations -> dispatchNewLocations(locations) }
                    }
                    fetchingLocations = false
                }
            }
        }
    }

    fun fetchLocationsIfEmpty() {
        if (fetchedLocations.isEmpty() && locationsPage < locationPages) fetchLocations()
    }

    private fun dispatchNewLocations(response: LocationsResponse) {
        locationPages = response.info.pages
        val allLocations = mutableListOf<Location>()
        allLocations.addAll(fetchedLocations)
        allLocations.addAll(response.results)
        allLocations.sortBy { it.name }
        fetchedLocations = allLocations
        locations.postValue(fetchedLocations)
    }

    fun resetRequestStatus() {
        locationRequestState = Channel<Resource<LocationsResponse?>>(Channel.CONFLATED)
    }

    var query: String? = null

    private val queryChannel = Channel<String>(Channel.CONFLATED)

    init {
        observeQuery()
    }

    fun offerQuery(text: String) = queryChannel.offer(text)

    private fun observeQuery() {
        viewModelScope.launch(IO) {
            queryChannel.consumeAsFlow().debounce(250L).map {
                clearFiltered()
                query = it
                it
            }.collect {
                filterLocations()
            }
        }
    }

    val filteredLocations = MutableLiveData<List<Location>>()
    private var fetchedFilteredLocations = listOf<Location>()
    var filterLocationRequestState = Channel<Resource<LocationsResponse?>>(Channel.CONFLATED)
    private var filteredLocationsPage = 0
    private var filteredLocationPages = 1
    private var filteredFetchingLocations = false

    fun filterLocations() {
        if (!filteredFetchingLocations && filteredLocationsPage <= filteredLocationPages) {
            filteredLocationsPage++
            filteredFetchingLocations = true
            viewModelScope.launch(IO) {
                filterLocations.filterLocations(query!!).collect {
                    filterLocationRequestState.send(it)
                    if (it is Resource.Success) {
                        it.data?.also { locations -> dispatchFilteredLocations(locations) }
                    }
                    filteredFetchingLocations = false
                }
            }
        }
    }

    private fun dispatchFilteredLocations(response: LocationsResponse) {
        locationPages = response.info.pages
        val allLocations = mutableListOf<Location>()
        allLocations.addAll(fetchedFilteredLocations)
        allLocations.addAll(response.results)
        allLocations.sortBy { it.name }
        fetchedFilteredLocations = allLocations
        filteredLocations.postValue(fetchedFilteredLocations)
    }

    fun clearFiltered() {
        query = null
        filteredLocationsPage = 0
        filteredLocationPages = 1
        filteredFetchingLocations = false
        fetchedFilteredLocations = listOf()
        locations.postValue(fetchedLocations)
    }

    fun resetFilterRequestStatus() {
        filterLocationRequestState = Channel<Resource<LocationsResponse?>>(Channel.CONFLATED)
    }
}
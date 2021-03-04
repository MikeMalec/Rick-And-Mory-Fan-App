package com.example.rick_and_morty.data.locations

import com.example.rick_and_morty.data.characters.Location

data class LocationCharactersResponseItem(
    val created: String,
    val episode: List<String>,
    val gender: String,
    val id: Int,
    val image: String,
    val location: Location,
    val name: String,
    val origin: Origin,
    val species: String,
    val status: String,
    val type: String,
    val url: String
)
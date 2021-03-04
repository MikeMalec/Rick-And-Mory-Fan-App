package com.example.rick_and_morty.data.characters

data class CharacterEpisodesResponseItem(
    val air_date: String,
    val characters: List<String>,
    val created: String,
    val episode: String,
    val id: Int,
    val name: String,
    val url: String
)
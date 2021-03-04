package com.example.rick_and_morty.data.characters

data class CharactersResponse(
    val info: Info,
    val results: List<Result>
)
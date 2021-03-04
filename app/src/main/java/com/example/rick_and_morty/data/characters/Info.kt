package com.example.rick_and_morty.data.characters

data class Info(
    val count: Int,
    val next: String? = null,
    val pages: Int,
    val prev: String? = null
)
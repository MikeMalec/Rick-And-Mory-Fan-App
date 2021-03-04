package com.example.rick_and_morty.data.episodes

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Result(
    val air_date: String,
    val characters: List<String>,
    val created: String,
    val episode: String,
    val id: Int,
    val name: String,
    val url: String,
    var seasonNumber: Int? = null,
    var episodeNumber: Int? = null
) : Parcelable

typealias Episode = Result
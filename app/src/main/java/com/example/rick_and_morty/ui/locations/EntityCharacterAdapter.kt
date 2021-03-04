package com.example.rick_and_morty.ui.locations

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.rick_and_morty.data.locations.LocationCharactersResponseItem
import com.example.rick_and_morty.databinding.LocationCharacterItemBinding
import javax.inject.Inject

class EntityCharacterAdapter @Inject constructor() :
    RecyclerView.Adapter<EntityCharacterAdapter.LocationCharacterViewHolder>() {
    inner class LocationCharacterViewHolder(val binding: LocationCharacterItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    var characters: List<LocationCharactersResponseItem> = emptyList()

    lateinit var onItemClick: (character: LocationCharactersResponseItem) -> Unit

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationCharacterViewHolder {
        return LocationCharacterViewHolder(
            LocationCharacterItemBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: LocationCharacterViewHolder, position: Int) {
        val character = characters[position]
        holder.binding.apply {
            Glide.with(ivLocationCharacterImage).load(character.image)
                .into(ivLocationCharacterImage)
            tvLocationCharacterName.text = character.name
            clLocationCharacterItem.setOnClickListener { onItemClick(character) }
        }
    }

    override fun getItemCount(): Int {
        return characters.size
    }
}
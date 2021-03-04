package com.example.rick_and_morty.ui.characters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.rick_and_morty.data.characters.Result
import com.example.rick_and_morty.databinding.CharacterItemBinding
import javax.inject.Inject

class CharactersAdapter @Inject constructor() :
    RecyclerView.Adapter<CharactersAdapter.CharacterViewHolder>() {
    inner class CharacterViewHolder(val characterItemBinding: CharacterItemBinding) :
        RecyclerView.ViewHolder(characterItemBinding.root)

    lateinit var itemCallback: (result: Result) -> Unit

    private val diffCallback = object : DiffUtil.ItemCallback<Result>() {
        override fun areItemsTheSame(oldItem: Result, newItem: Result): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Result, newItem: Result): Boolean {
            return oldItem == newItem
        }
    }

    private val differ = AsyncListDiffer(this, diffCallback)
    var results: List<Result>
        get() = differ.currentList
        set(value) = differ.submitList(value)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CharacterViewHolder {
        return CharacterViewHolder(
            CharacterItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: CharacterViewHolder, position: Int) {
        val character = results[position]
        holder.characterItemBinding.apply {
            Glide.with(ivCharacterImage).load(character.image).into(ivCharacterImage)
            clCharacterItem.setOnClickListener {
                itemCallback(character)
            }
            tvName.text = character.name
            tvStatus.text = character.status
            tvSpecie.text = character.species

            if (character.type.isEmpty()) {
                tvType.text = "Unknown"
            } else {
                tvType.text = character.type
            }
            tvGender.text = character.gender
            tvOrigin.text = character.origin.name
            tvLocation.text = character.location.name
        }
    }

    override fun getItemCount(): Int {
        return results.size
    }
}
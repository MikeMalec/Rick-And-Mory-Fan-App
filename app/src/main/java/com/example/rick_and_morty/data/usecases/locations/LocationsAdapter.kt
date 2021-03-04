package com.example.rick_and_morty.data.usecases.locations

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.rick_and_morty.data.locations.Location
import com.example.rick_and_morty.data.locations.Result
import com.example.rick_and_morty.databinding.LocationItemBinding
import javax.inject.Inject

class LocationsAdapter @Inject constructor() :
    RecyclerView.Adapter<LocationsAdapter.LocationsViewHolder>() {
    inner class LocationsViewHolder(val binding: LocationItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    lateinit var itemClickCallback: (item: Location) -> Unit

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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationsViewHolder {
        return LocationsViewHolder(
            LocationItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: LocationsViewHolder, position: Int) {
        val location = results[position]
        holder.binding.apply {
            clLocationItem.setOnClickListener { itemClickCallback(location) }
            tvLocationDimension.text = location.dimension
            tvLocationName.text = location.name
            tvLocationResidents.text = location.residents.size.toString()
            tvLocationType.text = location.type
        }
    }

    override fun getItemCount(): Int {
        return results.size
    }
}
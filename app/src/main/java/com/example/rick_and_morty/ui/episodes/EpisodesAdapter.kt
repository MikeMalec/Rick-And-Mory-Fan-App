package com.example.rick_and_morty.ui.episodes

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.LifecycleService
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.rick_and_morty.R
import com.example.rick_and_morty.data.episodes.Episode
import com.example.rick_and_morty.data.episodes.Season
import com.example.rick_and_morty.databinding.EpisodeItemLayoutBinding
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class EpisodesAdapter @Inject constructor() :
    RecyclerView.Adapter<EpisodesAdapter.EpisodeViewHolder>() {
    inner class EpisodeViewHolder(val binding: EpisodeItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root)

    lateinit var itemCallback: (episode: Episode) -> Unit

    lateinit var scope: LifecycleCoroutineScope

    private val diffCallback = object : DiffUtil.ItemCallback<Season>() {
        override fun areItemsTheSame(oldItem: Season, newItem: Season): Boolean {
            return oldItem.season == newItem.season
        }

        override fun areContentsTheSame(oldItem: Season, newItem: Season): Boolean {
            return oldItem == newItem
        }

    }

    private val differ = AsyncListDiffer(this, diffCallback)
    var seasons: List<Season>
        get() = differ.currentList
        set(value) = differ.submitList(value)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EpisodeViewHolder {
        return EpisodeViewHolder(
            EpisodeItemLayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: EpisodeViewHolder, position: Int) {
        val season = seasons[position]
        holder.binding.apply {
            tvSeason.text = "Season ${season.season}"
            season.episodes.forEach { episode ->
                val item = LayoutInflater.from(this@apply.root.context)
                    .inflate(R.layout.episode_item, null, false)
                val tvEpisodeName = item.findViewById<TextView>(R.id.tvEpisodeName)

                val tvDate = item.findViewById<TextView>(R.id.tvEpisodeDate)

                val tvEpisode = item.findViewById<TextView>(R.id.tvEpisodeEpisode)
                item.setOnClickListener {
                    itemCallback(episode)
                }
                tvDate.text = episode.air_date
                tvEpisodeName.text = episode.name
                tvEpisode.text = episode.episode
                llEpisodes.addView(item)
            }
        }
    }

    override fun getItemCount(): Int {
        return seasons.size
    }
}
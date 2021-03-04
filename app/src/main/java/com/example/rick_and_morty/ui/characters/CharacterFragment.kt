package com.example.rick_and_morty.ui.characters

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.rick_and_morty.R
import com.example.rick_and_morty.data.characters.Character
import com.example.rick_and_morty.data.characters.CharacterEpisodesResponseItem
import com.example.rick_and_morty.data.episodes.Episode
import com.example.rick_and_morty.data.locations.Location
import com.example.rick_and_morty.data.utils.Resource
import com.example.rick_and_morty.databinding.CharacterFragmentBinding
import com.example.rick_and_morty.ui.common.FragmentWithMainActivity
import com.example.rick_and_morty.utils.views.gone
import com.example.rick_and_morty.utils.views.shortSnackbar
import com.example.rick_and_morty.utils.views.show
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.consumeAsFlow

@AndroidEntryPoint
class CharacterFragment : FragmentWithMainActivity(R.layout.character_fragment) {

    private lateinit var binding: CharacterFragmentBinding

    private val characterViewModel: CharacterViewModel by viewModels()

    private val args: CharacterFragmentArgs by navArgs()

    private val character: Character
        get() = args.character

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = CharacterFragmentBinding.bind(view)
        setViews()
        fetchCharInfo()
        observeEpisodesRequestState()
        observeEpisodes()
    }

    private fun fetchCharInfo() {
        lifecycleScope.launchWhenStarted {
            delay(1000)
            characterViewModel.getEpisodes(character)
            characterViewModel.getLocation(character.location.url)
        }
    }

    private fun setViews() {
        binding.apply {
            Glide.with(ivCharacterImage).load(character.image).into(ivCharacterImage)
            characterToolbar.title = character.name
            characterToolbar.setTitleTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.white
                )
            )
            characterToolbar.setTitleTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.white
                )
            )
            characterToolbar.setSubtitleTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.white
                )
            )
            characterToolbar.setSubtitleTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.white
                )
            )
            characterToolbar.setTitleTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.white
                )
            )
            tvStatus.text = character.status
            tvSpecies.text = character.species
            if (character.type.isEmpty()) {
                tvType.text = "Unknown"
            } else {
                tvType.text = character.type
            }
            tvGender.text = character.gender
            tvOrigin.text = character.origin.name
            tvLocation.text = character.location.name
            tvLocation.setOnClickListener { showLocation() }
        }
    }

    private fun observeEpisodesRequestState() {
        lifecycleScope.launchWhenStarted {
            characterViewModel.episodesRequestState.consumeAsFlow().collect {
                when (it) {
                    is Resource.Loading -> showLoading()
                    is Resource.Error -> {
                        hideLoading()
                        it.error?.also { err -> mainActivity.shortSnackbar(err) }
                    }
                    is Resource.Success -> hideLoading()
                }
            }
        }
    }

    private fun showLoading() {
        binding.pbLoading.show()
    }

    private fun hideLoading() {
        binding.pbLoading.gone()
    }

    private fun observeEpisodes() {
        lifecycleScope.launchWhenStarted {
            characterViewModel.episodes.observe(
                viewLifecycleOwner,
                Observer {
                    addEpisodesToLayout(it)
                }
            )
        }
    }

    private fun addEpisodesToLayout(episodes: List<CharacterEpisodesResponseItem>) {
        episodes.forEach { character ->
            val item = layoutInflater.inflate(R.layout.character_episode, null, false)
            val episode = item.findViewById<TextView>(R.id.tvEpisode)
            episode.text = character.episode
            val date = item.findViewById<TextView>(R.id.tvDate)
            date.text = character.air_date
            val name = item.findViewById<TextView>(R.id.tvEpisodeName)
            name.text = character.name
            binding.llEpisodes.addView(item)
            item.setOnClickListener { showEpisode(character) }
        }
    }

    private fun showEpisode(characterEpisodesResponseItem: CharacterEpisodesResponseItem) {
        val episode = Episode(
            air_date = characterEpisodesResponseItem.air_date,
            characters = characterEpisodesResponseItem.characters,
            created = characterEpisodesResponseItem.created,
            episode = characterEpisodesResponseItem.episode,
            id = characterEpisodesResponseItem.id,
            name = characterEpisodesResponseItem.name,
            url = characterEpisodesResponseItem.url,
            seasonNumber = null,
            episodeNumber = null
        )
        CharacterFragmentDirections.actionCharacterFragmentToEpisodeFragment(episode).run {
            findNavController().navigate(this)
        }
    }

    private fun showLocation() {
        characterViewModel.location?.also { location ->
            CharacterFragmentDirections.actionCharacterFragmentToLocationFragment(location).run {
                findNavController().navigate(this)
            }
        }
    }
}
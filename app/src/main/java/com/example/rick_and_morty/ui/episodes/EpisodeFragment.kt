package com.example.rick_and_morty.ui.episodes

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rick_and_morty.R
import com.example.rick_and_morty.data.characters.Character
import com.example.rick_and_morty.data.characters.Origin
import com.example.rick_and_morty.data.characters.Result
import com.example.rick_and_morty.data.episodes.Episode
import com.example.rick_and_morty.data.locations.LocationCharactersResponseItem
import com.example.rick_and_morty.data.utils.Resource
import com.example.rick_and_morty.data.utils.Resource.Error
import com.example.rick_and_morty.data.utils.Resource.Loading
import com.example.rick_and_morty.data.utils.Resource.Success
import com.example.rick_and_morty.databinding.EpisodeFragmentBinding
import com.example.rick_and_morty.ui.common.FragmentWithMainActivity
import com.example.rick_and_morty.ui.locations.EntityCharacterAdapter
import com.example.rick_and_morty.ui.locations.CharactersViewModel
import com.example.rick_and_morty.utils.views.gone
import com.example.rick_and_morty.utils.views.shortSnackbar
import com.example.rick_and_morty.utils.views.show
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.consumeAsFlow
import javax.inject.Inject

@AndroidEntryPoint
class EpisodeFragment : FragmentWithMainActivity(R.layout.episode_fragment) {
    private lateinit var binding: EpisodeFragmentBinding

    private val characterViewModel: CharactersViewModel by viewModels()

    private val args: EpisodeFragmentArgs by navArgs()

    private val episode: Episode
        get() = args.episode

    @Inject
    lateinit var entityCharacterAdapter: EntityCharacterAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = EpisodeFragmentBinding.bind(view)
        characterViewModel.getEntityCharacters(episode.characters)
        setViews()
        setRv()
        observeData()
    }

    private fun observeData() {
        lifecycleScope.launchWhenStarted {
            delay(400)
            observeCharactersRequestState()
            observeCharacters()
        }
    }

    private fun setViews() {
        binding.apply {
            tvEpisodeName.text = episode.name
            tvEpisodeAirDate.text = episode.air_date
            tvEpisodeEpisode.text = episode.episode
        }
    }

    private fun setRv() {
        entityCharacterAdapter.onItemClick = ::showCharacter
        binding.rvEpisodeCharacters.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = entityCharacterAdapter
        }
    }

    private fun observeCharactersRequestState() {
        lifecycleScope.launchWhenStarted {
            characterViewModel.charactersRequestState.consumeAsFlow().collect {
                when (it) {
                    is Loading -> showLoading()
                    is Success -> hideLoading()
                    is Error -> {
                        hideLoading()
                        it.error?.also { err ->
                            mainActivity.shortSnackbar(err)
                        }
                    }
                }
            }
        }
    }

    private fun observeCharacters() {
        lifecycleScope.launchWhenStarted {
            characterViewModel.characters.observe(viewLifecycleOwner, Observer {
                entityCharacterAdapter.characters = it
                entityCharacterAdapter.notifyDataSetChanged()
            })
        }
    }

    private fun showLoading() {
        binding.pbLoading.show()
    }

    private fun hideLoading() {
        binding.pbLoading.gone()
    }

    private fun showCharacter(item: LocationCharactersResponseItem) {
        val character = Result(
            created = item.created,
            episode = item.episode,
            gender = item.gender,
            id = item.id,
            image = item.image,
            location = item.location,
            name = item.name,
            origin = Origin(name = item.origin.name, url = item.origin.url),
            species = item.species,
            status = item.status,
            type = item.type,
            url = item.url
        )
        EpisodeFragmentDirections.actionEpisodeFragmentToCharacterFragment(character).run {
            findNavController().navigate(this)
        }
    }
}
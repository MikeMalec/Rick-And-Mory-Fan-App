package com.example.rick_and_morty.ui.locations

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rick_and_morty.R
import com.example.rick_and_morty.data.characters.Origin
import com.example.rick_and_morty.data.characters.Result
import com.example.rick_and_morty.data.locations.Location
import com.example.rick_and_morty.data.locations.LocationCharactersResponseItem
import com.example.rick_and_morty.data.utils.Resource.Error
import com.example.rick_and_morty.data.utils.Resource.Loading
import com.example.rick_and_morty.data.utils.Resource.Success
import com.example.rick_and_morty.databinding.LocationFragmentBinding
import com.example.rick_and_morty.ui.common.FragmentWithMainActivity
import com.example.rick_and_morty.utils.views.gone
import com.example.rick_and_morty.utils.views.shortSnackbar
import com.example.rick_and_morty.utils.views.show
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.consumeAsFlow
import javax.inject.Inject

@AndroidEntryPoint
class LocationFragment : FragmentWithMainActivity(R.layout.location_fragment) {
    private val charactersViewModel: CharactersViewModel by viewModels()

    private val args: LocationFragmentArgs by navArgs()

    private val location: Location
        get() = args.location

    private lateinit var binding: LocationFragmentBinding

    @Inject
    lateinit var entityCharacterAdapter: EntityCharacterAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = LocationFragmentBinding.bind(view)
        charactersViewModel.getEntityCharacters(location.residents)
        setViews()
        setRv()
        observeData()
    }

    private fun observeData() {
        lifecycleScope.launchWhenStarted {
            delay(400)
            observeCharactersResponseState()
            observeCharacters()
        }
    }

    private fun setViews() {
        binding.apply {
            tvLocationName.text = location.name
            tvLocationType.text = location.type
            tvLocationDimension.text = location.dimension
        }
    }

    private fun setRv() {
        entityCharacterAdapter.onItemClick = ::showCharacter
        binding.rvLocationCharacters.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = entityCharacterAdapter
        }
    }

    private fun observeCharactersResponseState() {
        lifecycleScope.launchWhenStarted {
            charactersViewModel.charactersRequestState.consumeAsFlow().collect {
                when (it) {
                    is Loading -> showLoading()
                    is Error -> {
                        hideLoading()
                        it.error?.also { err -> mainActivity.shortSnackbar(err) }
                    }
                    is Success -> hideLoading()
                }
            }
        }
    }

    private fun observeCharacters() {
        lifecycleScope.launchWhenStarted {
            charactersViewModel.characters.observe(viewLifecycleOwner, Observer {
                entityCharacterAdapter.characters = it
                entityCharacterAdapter.notifyDataSetChanged()
            })
        }
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
        LocationFragmentDirections.actionLocationFragmentToCharacterFragment(character).run {
            findNavController().navigate(this)
        }
    }

    private fun showLoading() {
        binding.pbLoading.show()
    }

    private fun hideLoading() {
        binding.pbLoading.gone()
    }
}
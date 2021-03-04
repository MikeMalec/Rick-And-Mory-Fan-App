package com.example.rick_and_morty.ui.episodes

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rick_and_morty.R
import com.example.rick_and_morty.data.episodes.Episode
import com.example.rick_and_morty.data.utils.Resource
import com.example.rick_and_morty.data.utils.Resource.*
import com.example.rick_and_morty.databinding.EpisodesFragmentBinding
import com.example.rick_and_morty.ui.common.FragmentWithMainActivity
import com.example.rick_and_morty.utils.views.gone
import com.example.rick_and_morty.utils.views.shortSnackbar
import com.example.rick_and_morty.utils.views.show
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.consumeAsFlow
import javax.inject.Inject

@AndroidEntryPoint
class EpisodesFragment : FragmentWithMainActivity(R.layout.episodes_fragment) {
    private lateinit var binding: EpisodesFragmentBinding

    @Inject
    lateinit var episodesAdapter: EpisodesAdapter

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = EpisodesFragmentBinding.bind(view)
        viewModel.episodesFetcher.getEpisodes()
        setRv()
        observeEpisodeRequestState()
        observeEpisodes()
    }

    override fun onResume() {
        super.onResume()
        binding.rvEpisodes.apply {
            layoutManager?.onRestoreInstanceState(viewModel.episodesScrollState)
        }
    }

    private fun setRv() {
        episodesAdapter.itemCallback = ::showEpisode
        episodesAdapter.scope = lifecycleScope
        binding.rvEpisodes.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = episodesAdapter
        }
    }

    private fun observeEpisodeRequestState() {
        lifecycleScope.launchWhenStarted {
            viewModel.episodesFetcher.episodeRequestState.consumeAsFlow().collect {
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

    private fun observeEpisodes() {
        lifecycleScope.launchWhenStarted {
            viewModel.seasons.observe(viewLifecycleOwner, Observer {
                episodesAdapter.seasons = it
            })
        }
    }

    private fun showLoading() {
        binding.pbLoading.show()
    }

    private fun hideLoading() {
        binding.pbLoading.gone()
    }

    private fun showEpisode(episode: Episode) {
        EpisodesFragmentDirections.actionEpisodesFragmentToEpisodeFragment(episode).run {
            findNavController().navigate(this)
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.episodesScrollState = binding.rvEpisodes.layoutManager?.onSaveInstanceState()
    }
}
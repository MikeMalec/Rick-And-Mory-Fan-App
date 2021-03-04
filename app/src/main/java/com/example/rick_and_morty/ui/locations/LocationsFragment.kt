package com.example.rick_and_morty.ui.locations

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rick_and_morty.R
import com.example.rick_and_morty.data.locations.Location
import com.example.rick_and_morty.data.locations.LocationsResponse
import com.example.rick_and_morty.data.usecases.locations.LocationsAdapter
import com.example.rick_and_morty.data.utils.Resource
import com.example.rick_and_morty.databinding.LocationsFragmentBinding
import com.example.rick_and_morty.ui.common.FragmentWithMainActivity
import com.example.rick_and_morty.utils.views.gone
import com.example.rick_and_morty.utils.views.shortSnackbar
import com.example.rick_and_morty.utils.views.show
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.consumeAsFlow
import javax.inject.Inject

@AndroidEntryPoint
class LocationsFragment : FragmentWithMainActivity(R.layout.locations_fragment) {
    private lateinit var binding: LocationsFragmentBinding

    @Inject
    lateinit var locationsAdapter: LocationsAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = LocationsFragmentBinding.bind(view)
        viewModel.locationsPaginator.fetchLocationsIfEmpty()
        setRv()
        setSq()
        observeLocationsRequestState()
        observeFilteredLocationsRequestState()
        observeLocations()
        observeFilteredLocations()
    }

    override fun onResume() {
        super.onResume()
        binding.rvLocations.apply {
            layoutManager?.onRestoreInstanceState(viewModel.locationsScrollState)
        }
    }

    private fun setSq() {
        binding.locationsSearchView.apply {
            if (viewModel.filteringLocations()) {
                setQuery(viewModel.locationsPaginator.query, false)
            }
            setOnCloseListener {
                viewModel.locationsPaginator.clearFiltered()
                false
            }
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    when (newText) {
                        null, "" -> viewModel.locationsPaginator.clearFiltered()
                        else -> viewModel.locationsPaginator.offerQuery(newText)
                    }
                    return true
                }

            })
        }
    }

    private fun setRv() {
        locationsAdapter.itemClickCallback = ::showLocation
        binding.rvLocations.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = locationsAdapter
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    val ll = recyclerView.layoutManager as LinearLayoutManager
                    val lastPosition = ll.findLastVisibleItemPosition()
                    if (lastPosition == locationsAdapter.itemCount || lastPosition == locationsAdapter.itemCount - 1) {
                        if (viewModel.filteringLocations()) {
                            viewModel.locationsPaginator.filterLocations()
                        } else {
                            viewModel.locationsPaginator.fetchLocations()
                        }
                    }
                }
            })
        }
    }

    private fun observeLocationsRequestState() {
        viewModel.locationsPaginator.resetRequestStatus()
        lifecycleScope.launchWhenStarted {
            viewModel.locationsRequestState.consumeAsFlow().collect {
                dispatchRequestState(it)
            }
        }
    }

    private fun observeFilteredLocationsRequestState() {
        viewModel.locationsPaginator.resetFilterRequestStatus()
        lifecycleScope.launchWhenStarted {
            viewModel.filteredLocationsRequestState.consumeAsFlow().collect {
                dispatchRequestState(it)
            }
        }
    }

    private fun dispatchRequestState(state: Resource<LocationsResponse?>) {
        when (state) {
            is Resource.Loading -> showLoading()
            is Resource.Error -> {
                hideLoading()
                state.error?.also { err -> mainActivity.shortSnackbar(err) }
            }
            is Resource.Success -> hideLoading()
        }
    }

    private fun observeLocations() {
        lifecycleScope.launchWhenStarted {
            viewModel.locations.observe(viewLifecycleOwner, Observer {
                if (!viewModel.filteringLocations()) locationsAdapter.results = it
            })
        }
    }

    private fun observeFilteredLocations() {
        lifecycleScope.launchWhenStarted {
            viewModel.filteredLocations.observe(viewLifecycleOwner, Observer {
                if (viewModel.filteringLocations()) locationsAdapter.results = it
            })
        }
    }

    private fun showLoading() {
        if (viewModel.filteringLocations()) {
            showTopLoading()
        } else {
            when (locationsAdapter.itemCount) {
                0 -> showTopLoading()
                else -> showBottomLoading()
            }
        }
    }

    private fun showTopLoading() {
        binding.pbLoadingTop.show()
    }

    private fun showBottomLoading() {
        binding.pbBottomLoading.show()
    }

    private fun hideLoading() {
        hideTopLoading()
        hideBottomLoading()
    }

    private fun hideTopLoading() {
        binding.pbLoadingTop.gone()
    }

    private fun hideBottomLoading() {
        binding.pbBottomLoading.gone()
    }

    private fun showLocation(location: Location) {
        LocationsFragmentDirections.actionLocationsFragmentToLocationFragment(location).run {
            findNavController().navigate(this)
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.locationsScrollState = binding.rvLocations.layoutManager?.onSaveInstanceState()
    }
}
package com.example.rick_and_morty.ui.characters

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rick_and_morty.R
import com.example.rick_and_morty.data.characters.CharactersResponse
import com.example.rick_and_morty.data.characters.Result
import com.example.rick_and_morty.data.utils.Resource
import com.example.rick_and_morty.databinding.CharactersFragmentBinding
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
class CharactersFragment : FragmentWithMainActivity(R.layout.characters_fragment) {
    private lateinit var binding: CharactersFragmentBinding

    @Inject
    lateinit var charactersAdapter: CharactersAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = CharactersFragmentBinding.bind(view)
        viewModel = mainActivity.mainViewModel
        viewModel.charactersPaginator.fetchCharactersIfEmpty()
        setSq()
        setRv()
        observeData()
    }

    private fun observeData() {
        observeCharactersRequestStatus()
        observeFilterCharactersRequestStatus()
        observerCharacters()
        observeFilteredCharacters()
    }

    override fun onResume() {
        super.onResume()
        binding.rvCharacters.apply {
            layoutManager?.onRestoreInstanceState(viewModel.charactersScrollState)
        }
    }

    private fun setSq() {
        binding.charactersSearchView.apply {
            if (viewModel.filteringCharacters()) {
                setQuery(viewModel.charactersPaginator.query, false)
            }
            setOnCloseListener {
                viewModel.charactersPaginator.clearFiltered()
                false
            }
            setOnQueryTextListener(object :
                SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    when (newText) {
                        null, "" -> viewModel.charactersPaginator.clearFiltered()
                        else -> viewModel.charactersPaginator.offerQuery(newText)
                    }
                    return true
                }
            })
        }
    }

    private fun setRv() {
        charactersAdapter.itemCallback = ::showCharacter
        binding.rvCharacters.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = charactersAdapter
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    val ll = recyclerView.layoutManager as LinearLayoutManager
                    val lastPosition = ll.findLastVisibleItemPosition()
                    if (lastPosition == charactersAdapter.itemCount - 1 || lastPosition == charactersAdapter.itemCount - 2) {
                        if (viewModel.filteringCharacters()) {
                            viewModel.charactersPaginator.fetchFilteredCharacters()
                        } else {
                            viewModel.charactersPaginator.fetchCharacters()
                        }
                    }
                }
            })
        }
    }

    private fun observeCharactersRequestStatus() {
        viewModel.charactersPaginator.resetRequestStatus()
        lifecycleScope.launchWhenStarted {
            viewModel.charactersRequestState.consumeAsFlow().collect {
                dispatchStatus(it)
            }
        }
    }

    private fun observeFilterCharactersRequestStatus() {
        viewModel.charactersPaginator.resetFilterRequestStatus()
        lifecycleScope.launchWhenStarted {
            viewModel.filteredCharactersRequestState.consumeAsFlow().collect {
                dispatchStatus(it)
            }
        }
    }

    private fun dispatchStatus(status: Resource<CharactersResponse?>) {
        when (status) {
            is Resource.Loading -> showLoading()
            is Resource.Error -> {
                hideLoading()
                status.error?.also { err -> mainActivity.shortSnackbar(err) }
            }
            is Resource.Success -> hideLoading()
        }
    }

    private fun observerCharacters() {
        lifecycleScope.launchWhenStarted {
            viewModel.characters.observe(viewLifecycleOwner, Observer {
                if (!viewModel.filteringCharacters()) charactersAdapter.results = it
            })
        }
    }

    private fun observeFilteredCharacters() {
        lifecycleScope.launchWhenStarted {
            viewModel.filteredCharacters.observe(viewLifecycleOwner, Observer {
                if (viewModel.filteringCharacters()) {
                    charactersAdapter.results = it
                }
            })
        }
    }

    private fun showLoading() {
        if (viewModel.filteringCharacters()) {
            showTopLoading()
        } else {
            when (charactersAdapter.itemCount) {
                0 -> showTopLoading()
                else -> showBottomLoading()
            }
        }
    }

    private fun hideLoading() {
        hideBottomLoading()
        hideTopLoading()
    }

    private fun showBottomLoading() {
        binding.pbBottomLoading.show()
    }

    private fun hideBottomLoading() {
        binding.pbBottomLoading.gone()
    }

    private fun showTopLoading() {
        binding.pbTopLoading.show()
    }

    private fun hideTopLoading() {
        binding.pbTopLoading.gone()
    }

    private fun showCharacter(result: Result) {
        CharactersFragmentDirections.actionCharactersFragmentToCharacterFragment(result).run {
            findNavController().navigate(this)
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.charactersScrollState = binding.rvCharacters.layoutManager?.onSaveInstanceState()
    }
}
package com.medise.marvelapp.ui.search

import android.os.Bundle
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.medise.marvelapp.R
import com.medise.marvelapp.databinding.FragmentSearchCharacterBinding
import com.medise.marvelapp.ui.adapters.CharacterAdapters
import com.medise.marvelapp.ui.base.BaseFragment
import com.medise.marvelapp.ui.state.Resource
import com.medise.marvelapp.util.Constants
import com.medise.marvelapp.util.hide
import com.medise.marvelapp.util.show
import com.medise.marvelapp.util.toast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class SearchCharacterFragment :
    BaseFragment<FragmentSearchCharacterBinding, SearchCharacterViewModel>() {

    override val viewModel: SearchCharacterViewModel by viewModels()
    private val characterAdapter by lazy { CharacterAdapters() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        clickAdapter()
        val query = savedInstanceState?.getString(Constants.LAST_SEARCH_QUERY)?:Constants.DEFAULT_QUERY
        searchInit(query)
        collectObserver()
    }

    private fun collectObserver() = lifecycleScope.launch {
        viewModel.searchCharacter.collect { result ->
            when(result){
                is Resource.Success ->{
                    result.data?.let {
                        binding.progressbarSearch.hide()
                        characterAdapter.characters = it.data.results
                    }
                }
                is Resource.Loading ->{
                    binding.progressbarSearch.show()
                }
                is Resource.Error ->{
                    binding.progressbarSearch.hide()
                    result.message?.let {
                        toast(it)
                        Timber.tag("SearchCharacterFragment").e("Error -> $it")
                    }
                }
                else -> Unit
            }
        }
    }

    private fun searchInit(search: String) = with(binding) {
        edSearchCharacter.setText(search)
        edSearchCharacter.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_GO) {
                updateCharacterList()
                true
            } else {
                false
            }
        }
        edSearchCharacter.setOnKeyListener { _, keyCode, keyEvent ->
            if (keyEvent.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                updateCharacterList()
                true
            } else {
                false
            }
        }
    }

    private fun updateCharacterList() = with(binding) {
        edSearchCharacter.editableText.trim().let {
            if (it.isNotEmpty()) {
                searchQuery(it.toString())
            }
        }
    }

    private fun searchQuery(search: String) {
        viewModel.fetch(search)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(
            Constants.LAST_SEARCH_QUERY,
            binding.edSearchCharacter.editableText.trim().toString()
        )
    }

    private fun clickAdapter() {
        characterAdapter.setOnClickListener {
            val action = SearchCharacterFragmentDirections
                .actionSearchCharacterFragmentToDetailCharacterFragment(it)
            findNavController().navigate(action)
        }
    }

    private fun setupRecyclerView() = with(binding) {
        rvSearchCharacter.apply {
            adapter = characterAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSearchCharacterBinding =
        FragmentSearchCharacterBinding.inflate(inflater, container, false)


}
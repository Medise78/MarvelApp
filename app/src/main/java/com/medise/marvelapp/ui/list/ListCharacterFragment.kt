package com.medise.marvelapp.ui.list

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.medise.marvelapp.R
import com.medise.marvelapp.databinding.FragmentListCharacterBinding
import com.medise.marvelapp.ui.adapters.CharacterAdapters
import com.medise.marvelapp.ui.base.BaseFragment
import com.medise.marvelapp.ui.state.Resource
import com.medise.marvelapp.util.hide
import com.medise.marvelapp.util.show
import com.medise.marvelapp.util.toast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class ListCharacterFragment : BaseFragment<FragmentListCharacterBinding, ListCharacterViewModel>() {

    private val characterAdapter by lazy { CharacterAdapters() }
    override val viewModel: ListCharacterViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpRecyclerView()
        onItemAdapterClick()
        collectObserver()
    }

    private fun collectObserver() = lifecycleScope.launch {
        viewModel.list.collect { result ->
            when (result) {
                is Resource.Success -> {
                    result.data?.let {
                        binding.progressCircular.hide()
                        characterAdapter.characters = it.data.results
                    }
                }
                is Resource.Error -> {
                    binding.progressCircular.hide()
                    result.message?.let {
                        toast(it)
                        Timber.tag("ListCharacterFragment").e("Error -> $it")
                    }
                }
                is Resource.Loading -> {
                    binding.progressCircular.show()
                }
                else -> Unit
            }
        }
    }

    private fun onItemAdapterClick() {
        characterAdapter.setOnClickListener {
            val action = ListCharacterFragmentDirections
                .actionListCharacterFragmentToDetailCharacterFragment(it)
            findNavController().navigate(action)
        }
    }

    private fun setUpRecyclerView() = with(binding) {
        rvCharacters.apply {
            adapter = characterAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentListCharacterBinding =
        FragmentListCharacterBinding.inflate(inflater, container, false)
}
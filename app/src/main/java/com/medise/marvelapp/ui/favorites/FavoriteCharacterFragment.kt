package com.medise.marvelapp.ui.favorites

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.medise.marvelapp.R
import com.medise.marvelapp.databinding.FragmentFavoriteCharacterBinding
import com.medise.marvelapp.ui.adapters.CharacterAdapters
import com.medise.marvelapp.ui.base.BaseFragment
import com.medise.marvelapp.ui.state.Resource
import com.medise.marvelapp.util.hide
import com.medise.marvelapp.util.show
import com.medise.marvelapp.util.toast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FavoriteCharacterFragment :
    BaseFragment<FragmentFavoriteCharacterBinding, FavoriteCharacterViewModel>() {

    override val viewModel: FavoriteCharacterViewModel by viewModels()
    private val characterAdapter by lazy { CharacterAdapters() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        onAdapterClick()
        observer()
    }

    private fun observer() = lifecycleScope.launch {
        viewModel.favorites.collect { result ->
            when (result) {
                is Resource.Success -> {
                    result.data?.let {
                        binding.tvEmptyList.hide()
                        characterAdapter.characters = it.toList()
                    }
                }
                is Resource.Empty -> {
                    binding.tvEmptyList.show()
                }
                else -> Unit
            }
        }
    }

    private fun onAdapterClick() {
        characterAdapter.setOnClickListener {
            val action = FavoriteCharacterFragmentDirections
                .actionFavoriteCharacterFragmentToDetailCharacterFragment(it)
            findNavController().navigate(action)
        }
    }

    private fun setupRecyclerView() = with(binding) {
        rvFavoriteCharacter.apply {
            adapter = characterAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
        ItemTouchHelper(itemTouchHelper()).attachToRecyclerView(rvFavoriteCharacter)
    }

    private fun itemTouchHelper(): ItemTouchHelper.SimpleCallback {
        return object : ItemTouchHelper.SimpleCallback(
            0, ItemTouchHelper.RIGHT or ItemTouchHelper
                .LEFT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val character = characterAdapter.getPosition(viewHolder.adapterPosition)
                viewModel.delete(character).also {
                    toast("${character.name} Deleted")
                }
            }

        }
    }

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentFavoriteCharacterBinding =
        FragmentFavoriteCharacterBinding.inflate(inflater, container, false)
}
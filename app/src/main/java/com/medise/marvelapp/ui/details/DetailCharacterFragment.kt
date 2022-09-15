package com.medise.marvelapp.ui.details

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.medise.marvelapp.R
import com.medise.marvelapp.data.model.character.CharacterModel
import com.medise.marvelapp.databinding.FragmentDetailCharacterBinding
import com.medise.marvelapp.ui.adapters.ComicAdapters
import com.medise.marvelapp.ui.base.BaseFragment
import com.medise.marvelapp.ui.state.Resource
import com.medise.marvelapp.util.hide
import com.medise.marvelapp.util.limitDescription
import com.medise.marvelapp.util.show
import com.medise.marvelapp.util.toast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DetailCharacterFragment :
    BaseFragment<FragmentDetailCharacterBinding, DetailsCharacterViewModel>() {

    override val viewModel: DetailsCharacterViewModel by viewModels()
    private val comicAdapters by lazy { ComicAdapters() }
    private val args: DetailCharacterFragmentArgs by navArgs()
    private lateinit var characterModel: CharacterModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        characterModel = args.character
        viewModel.fetch(characterModel.id)
        setupRecyclerView()
        onLoadCharacter(characterModel)
        collectObserver()
        descriptionCharacter()
    }

    private fun descriptionCharacter(){
        binding.tvDescriptionCharacterDetails.setOnClickListener{
            showDialog(characterModel)
        }
    }

    private fun showDialog(characterModel: CharacterModel) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(characterModel.name)
            .setMessage(characterModel.description)
            .setNegativeButton(getString(R.string.close_dialog)) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun collectObserver() = lifecycleScope.launch {
        viewModel.details.collect { result ->
            when (result) {
                is Resource.Success -> {
                    binding.progressBarDetail.hide()
                    result.data?.let {
                        if (it.data.result.count() > 0)
                            comicAdapters.comics = it.data.result
                        else toast(getString(R.string.empty_list_comics))
                    }
                }
                is Resource.Error -> {
                    result.message?.let {
                        binding.progressBarDetail.hide()
                        toast(it)
                    }
                }
                is Resource.Loading -> {
                    binding.progressBarDetail.show()
                }
                else -> Unit
            }
        }
    }

    private fun onLoadCharacter(characterModel: CharacterModel) = with(binding) {
        tvNameCharacterDetails.text = characterModel.name
        if (characterModel.description.isEmpty()) {
            tvDescriptionCharacterDetails.text = context?.getText(R.string.text_description_empty)
        } else {
            tvDescriptionCharacterDetails.text = characterModel.description.limitDescription(100)
        }
        Glide.with(requireContext())
            .load(characterModel.thumbnailModel.path + "." + args.character.thumbnailModel.extension)
            .into(imgCharacterDetails)
    }

    private fun setupRecyclerView() = with(binding) {
        rvComics.apply {
            adapter = comicAdapters
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_details, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.favorite -> {
                viewModel.insert(characterModel)
                toast("${characterModel.name} Added")
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentDetailCharacterBinding =
        FragmentDetailCharacterBinding.inflate(inflater, container, false)
}
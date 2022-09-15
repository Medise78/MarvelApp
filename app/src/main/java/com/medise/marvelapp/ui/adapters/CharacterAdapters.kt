package com.medise.marvelapp.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.medise.marvelapp.R
import com.medise.marvelapp.data.model.character.CharacterModel
import com.medise.marvelapp.databinding.ItemCharacterBinding
import com.medise.marvelapp.util.limitDescription
import com.medise.marvelapp.util.loadImage

class CharacterAdapters : RecyclerView.Adapter<CharacterAdapters.CharacterViewHolder>() {

    inner class CharacterViewHolder(val binding: ItemCharacterBinding) :
        RecyclerView.ViewHolder(binding.root)

    private val differCallback = object : DiffUtil.ItemCallback<CharacterModel>() {
        override fun areItemsTheSame(oldItem: CharacterModel, newItem: CharacterModel): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

        override fun areContentsTheSame(oldItem: CharacterModel, newItem: CharacterModel): Boolean {
            return oldItem.id == newItem.id && oldItem.name == newItem.name &&
                    oldItem.description == newItem.description && oldItem.thumbnailModel.path ==
                    newItem.thumbnailModel.path && oldItem.thumbnailModel.extension ==
                    newItem.thumbnailModel.extension
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    var characters: List<CharacterModel>
        get() = differ.currentList
        set(value) = differ.submitList(value)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CharacterViewHolder {
        return CharacterViewHolder(
            ItemCharacterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: CharacterViewHolder, position: Int) {
        val characters = characters[position]
        holder.binding.apply {
            tvNameCharacter.text = characters.name
            if (characters.description == "") {
                tvDescriptionCharacter.text =
                    holder.itemView.context.getText(R.string.text_description_empty)
            } else {
                tvDescriptionCharacter.text = characters.description.limitDescription(100)
            }
            loadImage(
                imgCharacter,
                characters.thumbnailModel.path,
                characters.thumbnailModel.extension
            )
        }

        holder.itemView.setOnClickListener {
            onItemClickListener?.let {
                it(characters)
            }
        }
    }

    override fun getItemCount(): Int {
        return characters.size
    }

    private var onItemClickListener: ((CharacterModel) -> Unit)? = null

    fun setOnClickListener(listener: (CharacterModel) -> Unit) {
        onItemClickListener = listener
    }

    fun getPosition(position: Int): CharacterModel {
        return characters[position]
    }
}
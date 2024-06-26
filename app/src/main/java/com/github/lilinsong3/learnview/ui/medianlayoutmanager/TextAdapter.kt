package com.github.lilinsong3.learnview.ui.medianlayoutmanager

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.github.lilinsong3.learnview.common.DefaultDiffItemCallback
import com.github.lilinsong3.learnview.databinding.ItemTextBinding

class TextAdapter :
    ListAdapter<TextItemUiState, TextAdapter.TextViewHolder>(DefaultDiffItemCallback()) {

    class TextViewHolder(val binding: ItemTextBinding) : ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TextViewHolder =
        TextViewHolder(
            ItemTextBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )

    override fun onBindViewHolder(holder: TextViewHolder, position: Int) {
        val itemData = getItem(position)
        holder.binding.root.text = itemData.content
        holder.binding.root.setTextAppearance(itemData.textAppearanceRes)
    }
}
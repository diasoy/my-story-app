package com.example.mystoryapp.data.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mystoryapp.data.model.StoryDetail
import com.example.mystoryapp.databinding.StoryListBinding

class StoryAdapter(private val onItemClick: (StoryDetail) -> Unit) : PagingDataAdapter<StoryDetail, StoryAdapter.StoryViewHolder>(DIFF_CALLBACK) {

    inner class StoryViewHolder(private val binding: StoryListBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(story: StoryDetail) {
            binding.apply {
                tvName.text = story.name
                tvDate.text = story.createdAt
                Glide.with(root.context)
                    .load(story.photoUrl)
                    .into(ivPhoto)

                root.setOnClickListener {
                    onItemClick(story)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        val binding = StoryListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        val story = getItem(position)
        if (story != null) {
            holder.bind(story)
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<StoryDetail>() {
            override fun areItemsTheSame(oldItem: StoryDetail, newItem: StoryDetail): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: StoryDetail, newItem: StoryDetail): Boolean {
                return oldItem == newItem
            }
        }
    }
}
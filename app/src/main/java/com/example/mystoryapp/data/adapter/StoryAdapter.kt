package com.example.mystoryapp.data.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mystoryapp.data.model.StoryDetail
import com.example.mystoryapp.databinding.StoryListBinding

class StoryAdapter : RecyclerView.Adapter<StoryAdapter.StoryViewHolder>() {

    private val storyList = mutableListOf<StoryDetail>()

    class StoryViewHolder(private val binding: StoryListBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(story: StoryDetail) {
            binding.tvName.text = story.name
            binding.tvDate.text = story.createdAt
            Glide.with(binding.root.context)
                .load(story.photoUrl)
                .into(binding.ivPhoto)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        val binding = StoryListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        holder.bind(storyList[position])
    }

    override fun getItemCount(): Int = storyList.size

    fun setData(newStoryList: List<StoryDetail>) {
        storyList.clear()
        storyList.addAll(newStoryList)
        notifyDataSetChanged()
    }
}

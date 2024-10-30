package com.example.mystoryapp.data.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mystoryapp.data.model.StoryDetail
import com.example.mystoryapp.databinding.StoryListBinding

class StoryAdapter(
    private val onItemClick: (StoryDetail) -> Unit
) : RecyclerView.Adapter<StoryAdapter.StoryViewHolder>() {

    private val storyList = mutableListOf<StoryDetail>()

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
        holder.bind(storyList[position])
    }

    override fun getItemCount(): Int = storyList.size

    fun setData(newStoryList: List<StoryDetail>) {
        val diffCallback = StoryDiffCallback(storyList, newStoryList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        storyList.clear()
        storyList.addAll(newStoryList)
        diffResult.dispatchUpdatesTo(this)
    }
}

class StoryDiffCallback(
    private val oldList: List<StoryDetail>,
    private val newList: List<StoryDetail>
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldList.size
    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].id == newList[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}

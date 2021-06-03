package com.amin.marvelcharcaters.adapter

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import com.amin.marvelcharcaters.R
import com.amin.marvelcharcaters.databinding.PostersItemBinding
import com.amin.marvelcharcaters.model.BaseResult
import com.amin.marvelcharcaters.model.PosterItem
import com.amin.marvelcharcaters.utils.getImage
import com.bumptech.glide.Glide

class PostersRecyclerAdapter() :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<BaseResult>() {

        override fun areItemsTheSame(oldItem: BaseResult, newItem: BaseResult): Boolean {
          return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: BaseResult, newItem: BaseResult): Boolean {
          return oldItem == newItem
        }

    }
    private val differ = AsyncListDiffer(this, DIFF_CALLBACK)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return PostersViewHolder(
            PostersItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is PostersViewHolder -> {
                holder.bind(differ.currentList.get(position))
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun submitList(list: List<BaseResult>) {
        differ.submitList(list)
    }

    class PostersViewHolder
    constructor(
        private val binding: PostersItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: BaseResult) = with(binding.root) {

            binding.PosterTitle.text = item.name

            var image = item.thumbnail?.extension?.let { item.getImage(item.thumbnail?.path, it) }

            Glide.with(binding.root.context)
                .load(image)
                .placeholder(R.drawable.image_placeholder)
                .into(binding.posterImage)


        }
    }


}
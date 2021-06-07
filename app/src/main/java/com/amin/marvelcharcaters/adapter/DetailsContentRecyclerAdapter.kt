package com.amin.marvelcharcaters.adapter


import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.amin.marvelcharcaters.databinding.DetailsContentItemBinding
import com.amin.marvelcharcaters.model.details.NestedItem
import com.amin.marvelcharcaters.utils.extensions.addAnimation

class DetailsContentRecyclerAdapter() :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<NestedItem>() {

        override fun areItemsTheSame(oldItem: NestedItem, newItem: NestedItem): Boolean {
            return oldItem.requestedTitle == newItem.requestedTitle
        }

        override fun areContentsTheSame(oldItem: NestedItem, newItem: NestedItem): Boolean {
            return oldItem == newItem
        }

    }
    private val differ = AsyncListDiffer(this, DIFF_CALLBACK)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return DetailsContentViewHolder(
            DetailsContentItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is DetailsContentViewHolder -> {
                holder.bind(differ.currentList.get(position))
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun submitList(list: List<NestedItem>) {
        differ.submitList(list)
    }

    class DetailsContentViewHolder
    constructor(
        private val binding: DetailsContentItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: NestedItem) = with(binding.root) {
            binding.contentTitle.text = item.requestedTitle
            val mAdapter = PostersRecyclerAdapter()
            binding.contentRV.apply {
                layoutManager = LinearLayoutManager(binding.root.context, LinearLayoutManager.HORIZONTAL, false)
                adapter = mAdapter
            }

            item.list?.let { mAdapter.submitList(it) }

        }
    }

    var lastPosition = -1
    override fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {
        super.onViewAttachedToWindow(holder)
        holder.addAnimation(OvershootInterpolator(),lastPosition,300)
    }



}

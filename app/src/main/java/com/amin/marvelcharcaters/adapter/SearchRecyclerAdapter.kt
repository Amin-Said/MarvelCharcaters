package com.amin.taskdemo


import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.amin.marvelcharcaters.R
import com.amin.marvelcharcaters.databinding.SearchItemBinding
import com.amin.marvelcharcaters.model.CharacterResult
import com.amin.marvelcharcaters.utils.Helper
import com.amin.marvelcharcaters.utils.getImage
import com.bumptech.glide.Glide
import java.util.*


class SearchRecyclerAdapter(private val interaction: Interaction? = null) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(),Filterable {

    var searchFilterList = listOf<CharacterResult>()
    var textSearch:String = ""



    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<CharacterResult>() {

        override fun areItemsTheSame(oldItem: CharacterResult, newItem: CharacterResult): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: CharacterResult, newItem: CharacterResult): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

    }
    private val differ = AsyncListDiffer(this, DIFF_CALLBACK)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return SearchViewHolder(
            SearchItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            interaction,textSearch
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is SearchViewHolder -> {
                holder.bind(differ.currentList.get(position))
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun submitList(list: List<CharacterResult>) {
        println("DEBUG in submitList")
        searchFilterList = list
        differ.submitList(list)
    }

    class SearchViewHolder
    constructor(
        private val binding: SearchItemBinding,
        private val interaction: Interaction?,
        private val textSearch:String
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: CharacterResult) = with(binding.root) {
            binding.root.setOnClickListener {
                interaction?.onSearchItemSelected(adapterPosition, item)
            }

            binding.searchTitle.text = Helper.buildHighlightString(item.name,textSearch)

            var image = item.getImage(item.thumbnail.path,item.thumbnail.extension)

            Glide.with(binding.root.context)
                .load(image)
                .placeholder(R.drawable.image_placeholder)
                .into(binding.searchImage)
        }

    }


    interface Interaction {
        fun onSearchItemSelected(position: Int, item: CharacterResult)
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
            //differ.currentList
                val charSearch = constraint.toString()
                textSearch = charSearch
                if (charSearch.isEmpty()) {
                    searchFilterList = differ.currentList
                } else {
                    val resultList = mutableListOf<CharacterResult>()
                    for (row in differ.currentList) {
                        if (row.name.toLowerCase(Locale.ROOT).contains(charSearch.toLowerCase(Locale.ROOT))) {
                            resultList.add(row)
                        }
                    }
                    searchFilterList = resultList
                }
                val filterResults = FilterResults()
                filterResults.values = searchFilterList
                return filterResults

            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                searchFilterList = results?.values as List<CharacterResult>
                submitList(searchFilterList)
                notifyDataSetChanged()
            }

        }
    }

}

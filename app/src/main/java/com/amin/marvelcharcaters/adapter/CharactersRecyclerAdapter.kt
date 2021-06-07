package  com.amin.marvelcharcaters.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.amin.marvelcharcaters.R
import com.amin.marvelcharcaters.databinding.CharacterItemBinding
import com.amin.marvelcharcaters.model.CharacterResult
import com.amin.marvelcharcaters.utils.extensions.getImage
import com.bumptech.glide.Glide
import java.util.ArrayList

class CharactersRecyclerAdapter(private val interaction: Interaction? = null) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var itemsList =
        ArrayList<CharacterResult>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return SettingViewHolder(
            CharacterItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            interaction
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is SettingViewHolder -> {
                holder.bind(itemsList.get(position))
            }
        }
    }

    override fun getItemCount(): Int {
        return itemsList.size
    }

    class SettingViewHolder
    constructor(
        private val binding: CharacterItemBinding,
        private val interaction: Interaction?
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: CharacterResult) = with(binding.root) {
            binding.root.setOnClickListener {
                interaction?.onCharacterItemSelected(adapterPosition, item)
            }

            var image = item.getImage(item.thumbnail.path,item.thumbnail.extension)

            binding.characterTitle.text = item.name
            Glide.with(binding.root.context)
                .load(image)
                .placeholder(R.drawable.image_placeholder)
                .into(binding.characterImage)
        }
    }

    fun submitList(list: List<CharacterResult>) {
        itemsList = list as ArrayList<CharacterResult>
        notifyDataSetChanged()
    }

    fun addToCurrentList(list: List<CharacterResult>) {
        itemsList.addAll(list)
        notifyDataSetChanged()
    }

    interface Interaction {
        fun onCharacterItemSelected(position: Int, item: CharacterResult)
    }
}

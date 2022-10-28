package mx.mauriciogs.consuminggraphql

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.CharactersListQuery
import mx.mauriciogs.consuminggraphql.databinding.CharacterItemBinding

class CharacterListAdapter(
    private val characters: List<CharactersListQuery.Result>
): RecyclerView.Adapter<CharacterListAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: CharacterItemBinding): RecyclerView.ViewHolder(binding.root)

    var onItemClicked: ((CharactersListQuery.Result) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = CharacterItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val character = characters[position]
        Glide.with(holder.itemView.context)
            .load(character.image)
            .centerCrop()
            .into(holder.binding.ivMinCharacter)
        holder.binding.tvName.text = character.name
        holder.binding.tvSpecies.text = character.species

        holder.binding.root.setOnClickListener {
            onItemClicked?.invoke(character)
        }
    }

    override fun getItemCount(): Int = characters.size


}
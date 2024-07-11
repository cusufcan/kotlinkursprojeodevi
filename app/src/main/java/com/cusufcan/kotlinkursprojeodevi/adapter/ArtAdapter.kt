package com.cusufcan.kotlinkursprojeodevi.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.cusufcan.kotlinkursprojeodevi.databinding.ArtItemBinding
import com.cusufcan.kotlinkursprojeodevi.fragment.ListFragmentDirections
import com.cusufcan.kotlinkursprojeodevi.model.Art

class ArtAdapter(private var artList: List<Art>) : RecyclerView.Adapter<ArtAdapter.ArtViewHolder>() {
    class ArtViewHolder(var binding: ArtItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArtViewHolder {
        val binding = ArtItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ArtViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return artList.size
    }

    override fun onBindViewHolder(holder: ArtViewHolder, position: Int) {
        holder.binding.artItemText.text = artList[position].artName
        holder.itemView.setOnClickListener {
            val action = ListFragmentDirections.actionListFragmentToAddFragment("old", artList[position].id)
            Navigation.findNavController(it).navigate(action)
        }
    }
}
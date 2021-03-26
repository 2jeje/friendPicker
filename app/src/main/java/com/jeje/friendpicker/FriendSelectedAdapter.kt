package com.jeje.friendpicker

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jeje.friendpicker.databinding.SelectedFriendListBinding


class FriendSelectedAdapter(private val context : Context, private val viewModel: FriendPickerViewModel) : RecyclerView.Adapter<FriendSelectedAdapter.ViewHolder>() {

    inner class ViewHolder(val binding : SelectedFriendListBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(data: Friend, position: Int) {
            binding.friend = data
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = SelectedFriendListBinding.inflate( LayoutInflater.from(context), parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = viewModel.selectedFriends.value?.size ?: 0

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        viewModel.selectedFriends.value?.get(position)?.let { holder.bind(it, position) }

        val friend = viewModel.selectedFriends.value?.get(position)

        holder.itemView.setOnClickListener(View.OnClickListener {

            if (holder.adapterPosition != RecyclerView.NO_POSITION) {
                val pos = holder.adapterPosition
                viewModel.selectedFriends.value?.removeAt(pos)

                friend?.checked = false
                viewModel.friends.value = viewModel.friends.value

                notifyDataSetChanged()
            }
        })

    }
}


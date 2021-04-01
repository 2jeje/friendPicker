package com.jeje.friendpicker

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jeje.friendpicker.databinding.SelectedFriendListBinding
import com.jeje.friendpicker.model.Friend

class FriendSelectedAdapter(
    private val context: Context,
    private val view: View,
    val callback: (List<Friend>) -> Unit
) : RecyclerView.Adapter<FriendSelectedAdapter.ViewHolder>() {
    private var selectedFriends = mutableListOf<Friend>()

    class ViewHolder(private val binding: SelectedFriendListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: Friend) {
            binding.friend = data
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = SelectedFriendListBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = selectedFriends.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val friend = selectedFriends[position]
        holder.bind(friend)

        holder.itemView.setOnClickListener {

            if (holder.adapterPosition != RecyclerView.NO_POSITION) {
                val pos = holder.adapterPosition
                selectedFriends.removeAt(pos)

                friend.checked = false
                notifyItemChanged(pos + 1)

                notifyItemRemoved(pos)

                if (selectedFriends.size <= 0) {
                    view.visibility = View.GONE
                }
                callback(selectedFriends)
            }
        }
    }

    fun setSelectedFriends(selectedFriends: List<Friend>) {
        this.selectedFriends = selectedFriends.toMutableList()
        notifyDataSetChanged()
    }
}


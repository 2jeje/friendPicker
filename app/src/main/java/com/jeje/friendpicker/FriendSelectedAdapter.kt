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
    private val removeCallback: (Friend, List<Friend>) -> Unit
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

                notifyItemRemoved(pos)

                removeCallback(friend, selectedFriends)
            }
        }
    }

    fun setSelectedFriends(selectedFriends: List<Friend>) {
        this.selectedFriends = selectedFriends.toMutableList()
    }

    fun removeFriend(friend: Friend) {
        val position = selectedFriends.indexOf(friend)
        notifyItemRemoved(position)
    }

    fun addFriend(friend: Friend) {
        selectedFriends.add(0, friend)
        notifyItemInserted(0)
    }
}


package com.jeje.friendpicker

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jeje.friendpicker.databinding.SelectedFriendListBinding
import com.jeje.friendpicker.model.Friend
import kotlinx.android.synthetic.main.fragment_friend_picker.*

class FriendSelectedAdapter(
    private val context: Context,
    private val view: View,
    private val removeCallback: (Friend, List<Friend>) -> Unit
) : RecyclerView.Adapter<FriendSelectedAdapter.ViewHolder>() {
    private var friends = mutableListOf<Friend>()

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

    override fun getItemCount(): Int = friends.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val friend = friends[position]
        holder.bind(friend)

        updateVisibility()

        holder.itemView.setOnClickListener {

            if (holder.adapterPosition != RecyclerView.NO_POSITION) {
                val pos = holder.adapterPosition
                friends.removeAt(pos)

                friend.checked = false

                notifyItemRemoved(pos)

                removeCallback(friend, friends)
            }
        }
    }

    fun setSelectedFriends(selectedFriends: List<Friend>) {
        this.friends = selectedFriends.toMutableList()
    }

    fun removeFriend(friend: Friend) {
        val position = friends.indexOf(friend)
        friends.remove(friend)
        notifyItemRemoved(position)

        updateVisibility()
    }

    fun addFriend(friend: Friend) {
        if (!friends.contains(friend)) {
            friends.add(0, friend)
            notifyItemInserted(0)
        }
        updateVisibility()
    }

    private fun updateVisibility() {
        if (friends.isNullOrEmpty()) {
            view.visibility = View.GONE
        }
        else {
            view.visibility = View.VISIBLE
        }
    }
}


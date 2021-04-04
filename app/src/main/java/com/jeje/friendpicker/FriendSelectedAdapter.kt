package com.jeje.friendpicker

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.jeje.friendpicker.databinding.SelectedFriendListBinding
import com.jeje.friendpicker.model.Friend
import kotlinx.android.synthetic.main.fragment_friend_picker.*

class FriendSelectedAdapter(
    private val context: Context,
    private val removeCallback: (Friend, List<Friend>) -> Unit
) : RecyclerView.Adapter<FriendSelectedAdapter.ViewHolder>() {
    private var _friends = mutableListOf<Friend>()
    val friends: MutableList<Friend> get() = _friends

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

    override fun getItemCount(): Int = _friends.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val friend = _friends[position]
        holder.bind(friend)

        holder.itemView.setOnClickListener {

            if (holder.adapterPosition != RecyclerView.NO_POSITION) {
                val pos = holder.adapterPosition
                _friends.removeAt(pos)
                friend.checked = false
                notifyItemRemoved(pos)

                removeCallback(friend, friends)
            }
        }
    }

    fun setSelectedFriends(selectedFriends: List<Friend>) {
        this._friends = selectedFriends.toMutableList()
    }

    fun removeFriend(friend: Friend) {
        val position = _friends.indexOf(friend)
        _friends.remove(friend)
        notifyItemRemoved(position)
    }

    fun addFriend(friend: Friend) {
        if (!_friends.contains(friend)) {
            _friends.add(0, friend)
            notifyItemInserted(0)
        }
    }
}


package com.jeje.friendpicker

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jeje.friendpicker.databinding.ItemDataListBinding
import com.jeje.friendpicker.model.Friend

open class BaseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

class HeaderViewHolder(itemView: View) : BaseViewHolder(itemView)

class ItemViewHolder(val binding: ItemDataListBinding) : BaseViewHolder(binding.root) {
    fun bind(data: Friend) {
        binding.friend = data
    }
}

class FriendPickerAdapter(
    private val context: Context,
    private val addCallback: (Friend)->Unit,
    private val removeCallback: (Friend) -> Unit,
    private val listCallback: (List<Friend>) -> Unit
) : RecyclerView.Adapter<BaseViewHolder>() {
    private var friends = mutableListOf<Friend>()
    private var selectedFriends = mutableListOf<Friend>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return when (viewType) {
            TYPE_HEADER -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.friend_recycler_header, parent, false)
                HeaderViewHolder(view)
            }

            TYPE_ITEM -> {
                val view = ItemDataListBinding.inflate(LayoutInflater.from(context), parent, false)
                ItemViewHolder(view)
            }
            else -> throw Exception("Unknow viewType $viewType")
        }

    }

    override fun getItemViewType(position: Int): Int =
        when (position) {
            0 -> TYPE_HEADER
            else -> TYPE_ITEM
        }

    override fun getItemCount(): Int = friends.size + HEADER_SIZE

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        if (holder is ItemViewHolder) {
            holder.bind(friends[position - HEADER_SIZE])

            val friend = friends[position - HEADER_SIZE]
            holder.binding.checkBox.isChecked = friend.checked

            holder.itemView.setOnClickListener {

                if (holder.adapterPosition != RecyclerView.NO_POSITION) {
                    //val pos = holder.adapterPosition - HEADER_SIZE

                    friend.run {
                        if (checked) {
                            checked = false
                            holder.binding.checkBox.isChecked = false
                            selectedFriends.remove(this)
                            removeCallback(this)
                        } else {
                            checked = true
                            holder.binding.checkBox.isChecked = true
                            selectedFriends.add(0, this)
                            addCallback(this)
                        }
                    }
                }
                listCallback(selectedFriends)
            }
        }
    }

    fun setFriends(friends: MutableList<Friend>) {
        this.friends = friends
        notifyDataSetChanged()
    }

    fun removeSelectedFriend(friend: Friend, selectedFriends: List<Friend>) {
        this.selectedFriends = selectedFriends.toMutableList()
        val position = friends.indexOf(friend)
        friends[position].checked = false
        notifyItemChanged(position + 1)
    }

    fun setSelectedFriends(selectedFriends: MutableList<Friend>) {
        this.selectedFriends = selectedFriends
    }

    companion object {
        const val TYPE_HEADER = 0
        const val TYPE_ITEM = 1
        const val HEADER_SIZE = 1
    }
}
package com.jeje.friendpicker

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.RadioButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.jeje.friendpicker.databinding.ItemDataListBinding
import kotlinx.android.synthetic.main.fragment_friend_picker.*

class FriendPickerAdapter(private val context : Context, private val selectedAdapter: FriendSelectedAdapter, private val selectedView : View) : RecyclerView.Adapter<FriendPickerAdapter.ViewHolder>() {
    var friends = listOf<Friend>()

    inner class ViewHolder(val binding : ItemDataListBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(data: Friend, position: Int) {
            binding.friend = data
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = ItemDataListBinding.inflate( LayoutInflater.from(context), parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = friends.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(friends[position], position)

        holder.itemView.setOnClickListener(View.OnClickListener {
                val pos = holder.adapterPosition
                if (pos != RecyclerView.NO_POSITION) {
                    val friend = friends[pos]
                    if (friend.checked) {
                        friend.checked = false
                        holder.binding.checkBox.isChecked = false

                        for ( selectedFriend in selectedAdapter.selectedFriends) {
                            if (selectedFriend.nickName == friend.nickName && selectedFriend.profileImage == friend.profileImage) {
                                selectedAdapter.selectedFriends.remove(selectedFriend)
                                selectedAdapter.notifyDataSetChanged()
                            }
                        }

                        if (selectedAdapter.selectedFriends.size <= 0) {
                            selectedView.visibility = View.GONE
                        }
                    }
                    else {
                        friend.checked = true
                        holder.binding.checkBox.isChecked = true

                        selectedAdapter.selectedFriends.add(friend)
                        selectedAdapter.notifyItemChanged(selectedAdapter.itemCount - 1)
                        selectedView.visibility = View.VISIBLE
                    }
                }
        })

    }

}
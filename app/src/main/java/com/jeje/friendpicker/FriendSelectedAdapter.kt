package com.jeje.friendpicker

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jeje.friendpicker.databinding.ItemDataListBinding

class FriendSelectedAdapter(private val context : Context) : RecyclerView.Adapter<FriendSelectedAdapter.ViewHolder>() {
    var selectedFriends = listOf<Friend>()

    inner class ViewHolder(val binding : ItemDataListBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(data: Friend, position: Int) {
            binding.friend = data
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = ItemDataListBinding.inflate( LayoutInflater.from(context), parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = selectedFriends.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(selectedFriends[position], position)

//        holder.itemView.setOnClickListener(View.OnClickListener {
//            val pos = holder.adapterPosition
//            if (pos != RecyclerView.NO_POSITION) {
//                val friend = friends[pos]
//                if (friend.checked) {
//                    friend.checked = false
//                    holder.binding.checkBox.isChecked = false
//                }
//                else {
//                    friend.checked = true
//                    holder.binding.checkBox.isChecked = true
//                }
//            }
//        })

    }

}
package com.jeje.friendpicker

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.CheckBox
import android.widget.RadioButton
import android.widget.TextView
import androidx.annotation.RequiresPermission
import androidx.recyclerview.widget.RecyclerView
import com.jeje.friendpicker.databinding.ItemDataListBinding
import kotlinx.android.synthetic.main.fragment_friend_picker.*

open class BaseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

class HeaderViewHolder(itemView: View) : BaseViewHolder(itemView)

class ItemViewHolder(val binding : ItemDataListBinding): BaseViewHolder(binding.root) {
    fun bind(data: Friend) {
        binding.friend = data
    }
}

class FriendPickerAdapter(private val context : Context, private val selectedAdapter: FriendSelectedAdapter, private val selectedView : View) : RecyclerView.Adapter<BaseViewHolder>() {
    var friends = mutableListOf<Friend>()

    private val TYPE_HEADER = 0
    private val TYPE_ITEM = 1

    private val HEADER_SIZE = 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {

        return when (viewType) {
            TYPE_HEADER ->{
                val view = LayoutInflater.from(parent.context).inflate(R.layout.friend_recycler_header, parent, false)
                HeaderViewHolder(view)
            }

            TYPE_ITEM->{
                val view = ItemDataListBinding.inflate( LayoutInflater.from(context), parent, false)
                ItemViewHolder(view)
            }
            else -> throw Exception("Unknow viewType $viewType")
        }

    }

    override fun getItemViewType(position: Int): Int =
            when(position) {
                0 -> TYPE_HEADER
                else -> TYPE_ITEM
            }

    override fun getItemCount(): Int = friends.size + HEADER_SIZE

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        Log.i("jeje.e", "position ${position}")
        if (holder is ItemViewHolder) {

            holder.bind(friends[position - HEADER_SIZE])

            val friend = friends[position - HEADER_SIZE]
            holder.binding.checkBox.isChecked = friend.checked
            
            holder.itemView.setOnClickListener(View.OnClickListener {

                if (holder.adapterPosition != RecyclerView.NO_POSITION) {
                    val pos = holder.adapterPosition  - HEADER_SIZE

                    Log.i("jeje.e", "pos ${holder.adapterPosition }")

                    if (friend.checked) {
                        friend.checked = false
                        holder.binding.checkBox.isChecked = false

                        selectedAdapter.selectedFriends = selectedAdapter.selectedFriends.filterIndexed { index, selected ->
                            selected.profileImage != friend.profileImage && selected.nickName != friend.nickName
                        }.toMutableList()

                        selectedAdapter.notifyDataSetChanged()

                        if (selectedAdapter.selectedFriends.size <= 0) {
                            selectedView.visibility = View.GONE
                        }
                    }
                    else {
                        friend.checked = true
                        holder.binding.checkBox.isChecked = true

                        selectedAdapter.selectedFriends.add(0, friend)
                        selectedAdapter.notifyDataSetChanged()
                        selectedView.visibility = View.VISIBLE
                    }
                }
            })
        }
    }

}
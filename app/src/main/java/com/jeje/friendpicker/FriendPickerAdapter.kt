package com.jeje.friendpicker

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jeje.friendpicker.databinding.ItemDataListBinding
import com.jeje.friendpicker.model.Friend
import com.jeje.friendpicker.viewmodel.FriendPickerViewModel


interface FriendPickerAdapterListener {
    fun onClickFriend(friend: Friend?)
}


open class BaseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

class HeaderViewHolder(itemView: View) : BaseViewHolder(itemView)

class ItemViewHolder(val binding: ItemDataListBinding) : BaseViewHolder(binding.root) {
    fun bind(data: Friend) {
        binding.friend = data
    }
}

class FriendPickerAdapter(
    private val context: Context,
    private val viewModel: FriendPickerViewModel
) : RecyclerView.Adapter<BaseViewHolder>() {

    private val TYPE_HEADER = 0
    private val TYPE_ITEM = 1

    private val HEADER_SIZE = 1

    lateinit var listener: FriendPickerAdapterListener

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

    override fun getItemCount(): Int = (viewModel.friends.value?.size ?: 0) + HEADER_SIZE

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        if (holder is ItemViewHolder) {
            viewModel.friends.value?.get(position - HEADER_SIZE)?.let { holder.bind(it) }

            val friend = viewModel.friends.value?.get(position - HEADER_SIZE)
            holder.binding.checkBox.isChecked = friend?.checked ?: false

            holder.itemView.setOnClickListener {

                if (holder.adapterPosition != RecyclerView.NO_POSITION) {
                    //val pos = holder.adapterPosition - HEADER_SIZE
                    listener.onClickFriend(friend)

                    friend?.run {
                        if (checked) {
                            checked = false
                            holder.binding.checkBox.isChecked = false

                        } else {
                            checked = true
                            holder.binding.checkBox.isChecked = true
                        }
                    }
                }
            }
        }
    }

}
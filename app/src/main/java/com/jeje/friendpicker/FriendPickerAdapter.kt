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

class FriendPickerAdapter(private val context : Context, private val selectedAdapter: FriendSelectedAdapter, private val selectedView : View, private val viewModel: FriendPickerViewModel) : RecyclerView.Adapter<BaseViewHolder>() {


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

    override fun getItemCount(): Int = (viewModel.friends.value?.size ?: 0) + HEADER_SIZE

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        if (holder is ItemViewHolder) {

            viewModel.friends.value?.get(position - HEADER_SIZE)?.let { holder.bind(it) }

            val friend = viewModel.friends.value?.get(position - HEADER_SIZE)
            holder.binding.checkBox.isChecked = friend?.checked ?: false

            holder.itemView.setOnClickListener(View.OnClickListener {

                if (holder.adapterPosition != RecyclerView.NO_POSITION) {
                    val pos = holder.adapterPosition  - HEADER_SIZE

                    if (friend != null) {
                        if (friend.checked) {
                            friend.checked = false
                            holder.binding.checkBox.isChecked = false

                            viewModel.selectedFriends.value = viewModel.selectedFriends.value?.filterIndexed { index, selected ->
                                selected.profileImage != friend.profileImage && selected.nickName != friend.nickName
                            }?.toMutableList()

                            selectedAdapter.notifyDataSetChanged()

                            if (viewModel.selectedFriends.value?.size!! <= 0) {
                                selectedView.visibility = View.GONE
                            }
                        } else {
                            friend.checked = true
                            holder.binding.checkBox.isChecked = true

                            viewModel.selectedFriends.value?.add(0,friend)
                            selectedAdapter.notifyDataSetChanged()
                            selectedView.visibility = View.VISIBLE
                        }
                    }
                }
            })
        }
    }

}
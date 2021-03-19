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

class FriendPickerAdapter(private val context : Context) : RecyclerView.Adapter<FriendPickerAdapter.ViewHolder>() {
    var data = listOf<Friend>()
    var checkedPos = -1

    inner class ViewHolder(val binding : ItemDataListBinding): RecyclerView.ViewHolder(binding.root) {
        // onBindViewHolder의 역할을 대신한다.
        fun bind(data: Friend, position: Int) {
            binding.friend = data

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = ItemDataListBinding.inflate( LayoutInflater.from(context), parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.bind(data[position], position)

        if (checkedPos == position) {
            holder.binding.checkBox.setButtonDrawable(R.drawable.daynight_friends_picker_checkbox)
        }

        holder.binding.checkBox.setOnClickListener(View.OnClickListener { view ->
                Log.d("jeje", "check ${holder.binding.checkBox.isChecked} pos ${checkedPos}")
                if (position == checkedPos) {
                    holder.binding.checkBox.isChecked = false
                    checkedPos = -1
                }
                else {
                    holder.binding.checkBox.isChecked = true
                    checkedPos = position
                }
            })
    }

}
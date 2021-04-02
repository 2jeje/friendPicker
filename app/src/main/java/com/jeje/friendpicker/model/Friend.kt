package com.jeje.friendpicker.model

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.example.ImageLoader
import com.jeje.friendpicker.R


data class Friend(
    var profileImage: String? = null,
    var nickName: String? = null,
    var checked: Boolean = false
) {
    object Bind {
        @JvmStatic
        @BindingAdapter("app:imageUri")
        fun loadImage(imageView: ImageView, imageUri: String) {
            ImageLoader.get(imageView.context).load(imageUri).placeHolder(R.drawable.profile).into(imageView)
        }

        @JvmStatic
        @BindingAdapter("app:nickName")
        fun setNickName(textView: TextView, nickName: String) {
            textView.text = nickName
        }
    }
}
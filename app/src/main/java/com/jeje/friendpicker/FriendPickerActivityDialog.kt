package com.jeje.friendpicker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class FriendPickerActivityDialog : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friend_picker_dialog)

        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(
            R.id.dialoglistFrame,
            FriendPickerFragment()
        )
        transaction.commit()
    }
}
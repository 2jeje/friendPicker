package com.jeje.friendpicker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.commit

class FriendPickerActivityDialog : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friend_picker_dialog)

        supportFragmentManager.commit {
            replace(R.id.dialoglistFrame, FriendPickerFragment())
        }
    }
}
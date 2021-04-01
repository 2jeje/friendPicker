package com.jeje.friendpicker

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit

class FriendPickerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friend_picker)

        supportFragmentManager.commit {
            replace(R.id.listFrame, FriendPickerFragment())
        }
    }
}
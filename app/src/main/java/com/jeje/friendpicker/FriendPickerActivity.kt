package com.jeje.friendpicker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class FriendPickerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friend_picker)

        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(
            R.id.listFrame,
            FriendPickerFragment()
        )
        transaction.commit()
    }
}
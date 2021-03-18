package com.jeje.friendpicker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class FriendPickerActivityDialog : AppCompatActivity() {

    var dataList: ArrayList<Friend> = arrayListOf(
        Friend("https://img.hankyung.com/photo/201903/AA.19067065.1.jpg", "Ryan"),
        Friend("https://img.hankyung.com/photo/201903/AA.19067065.1.jpg", "Ryan"),
        Friend("https://img.hankyung.com/photo/201903/AA.19067065.1.jpg", "Ryan"),
        Friend("https://img.hankyung.com/photo/201903/AA.19067065.1.jpg", "Ryan"),
        Friend("https://img.hankyung.com/photo/201903/AA.19067065.1.jpg", "Ryan"),
        Friend("https://img.hankyung.com/photo/201903/AA.19067065.1.jpg", "Ryan"),
        Friend("https://img.hankyung.com/photo/201903/AA.19067065.1.jpg", "Ryan"),
        Friend("https://img.hankyung.com/photo/201903/AA.19067065.1.jpg", "Ryan"),
        Friend("https://img.hankyung.com/photo/201903/AA.19067065.1.jpg", "Ryan"),
        Friend("https://img.hankyung.com/photo/201903/AA.19067065.1.jpg", "Ryan"),
        Friend("https://img.hankyung.com/photo/201903/AA.19067065.1.jpg", "Ryan"),
        Friend("https://img.hankyung.com/photo/201903/AA.19067065.1.jpg", "Ryan"),
        Friend("https://img.hankyung.com/photo/201903/AA.19067065.1.jpg", "Ryan"),
        Friend("https://img.hankyung.com/photo/201903/AA.19067065.1.jpg", "Ryan")
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friend_picker_dialog)

        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(
            R.id.dialoglistFrame,
            FriendPickerFragment()
        )
        transaction.commit()
        intent.putExtra("DataList", dataList)
    }
}
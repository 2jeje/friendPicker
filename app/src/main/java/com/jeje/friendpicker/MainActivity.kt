  package com.jeje.friendpicker

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*

  class MainActivity : AppCompatActivity() {
      var dataList: ArrayList<TestData> = arrayListOf(
          TestData("첫 번째 데이터1", "두 번째 데이터1", "세 번째 데이터1"),
          TestData("첫 번째 데이터2", "두 번째 데이터2", "세 번째 데이터2"),
          TestData("첫 번째 데이터3", "두 번째 데이터3", "세 번째 데이터3"),
          TestData("첫 번째 데이터4", "두 번째 데이터4", "세 번째 데이터4"),
          TestData("첫 번째 데이터5", "두 번째 데이터5", "세 번째 데이터5"),
          TestData("첫 번째 데이터6", "두 번째 데이터6", "세 번째 데이터6"),
          TestData("첫 번째 데이터7", "두 번째 데이터7", "세 번째 데이터7"),
          TestData("첫 번째 데이터8", "두 번째 데이터8", "세 번째 데이터8"),
          TestData("첫 번째 데이터9", "두 번째 데이터9", "세 번째 데이터9"),
          TestData("첫 번째 데이터10", "두 번째 데이터10", "세 번째 데이터10"),
          TestData("첫 번째 데이터11", "두 번째 데이터11", "세 번째 데이터11"),
          TestData("첫 번째 데이터12", "두 번째 데이터12", "세 번째 데이터12"),
          TestData("첫 번째 데이터13", "두 번째 데이터13", "세 번째 데이터13"),
          TestData("첫 번째 데이터14", "두 번째 데이터14", "세 번째 데이터14"),
          TestData("첫 번째 데이터15", "두 번째 데이터15", "세 번째 데이터15")
      )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        activityBtn.setOnClickListener(View.OnClickListener {

            var intent = Intent(this, FriendPickerActivity::class.java)

            startActivity(intent)
        })

        fragmentBtn.setOnClickListener(View.OnClickListener {
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(
                R.id.mainlistFrame,
                FriendPickerFragment()
            )
            transaction.commit()
            intent.putExtra("DataList", dataList)
        })

    }
}




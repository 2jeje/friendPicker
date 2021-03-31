  package com.jeje.friendpicker

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import com.kakao.sdk.common.KakaoSdk
import com.kakao.sdk.user.UserApiClient
import kotlinx.android.synthetic.main.activity_main.*

  class MainActivity : AppCompatActivity() {

      override fun onCreate(savedInstanceState: Bundle?) {
          super.onCreate(savedInstanceState)
          setContentView(R.layout.activity_main)

          KakaoSdk.init(this,"9f9de684c354a72d2eb2a540a11441c2",loggingEnabled = true)

          UserApiClient.instance.loginWithKakaoAccount(this) { token, error ->
              Log.i("jeje","${token}")

          }
          activityBtn.setOnClickListener(View.OnClickListener {
              var intent = Intent(this, FriendPickerActivity::class.java)
              startActivity(intent)
          })


        dialogBtn.setOnClickListener(View.OnClickListener {
            var intent = Intent(this, FriendPickerActivityDialog::class.java)
            startActivity(intent)
        })

        fragmentBtn.setOnClickListener(View.OnClickListener {
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(
                R.id.mainlistFrame,
                FriendPickerFragment()
            )
            transaction.commit()
        })
    }
}




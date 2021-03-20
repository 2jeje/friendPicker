package com.jeje.friendpicker

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kakao.sdk.partner.talk.friendsForPartner
import com.kakao.sdk.talk.TalkApiClient
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_friend_picker.*

/**
 * A simple [Fragment] subclass.
 * Use the [FriendPickerFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class FriendPickerFragment : Fragment() {

    // RecyclerView.adapter에 지정할 Adapter
    private lateinit var pickerAdapter: FriendPickerAdapter

    var dataList: ArrayList<Friend> = arrayListOf()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_friend_picker, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pickerAdapter = FriendPickerAdapter(context!!)
        friends_view.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        friends_view.adapter = pickerAdapter

        TalkApiClient.instance.friendsForPartner { friends, error ->
            if (error != null) {
                Log.i("jeje", "${error}")

            } else {
                Log.i("jeje", "${friends}")
                if (friends != null) {
                    dataList.clear()
                    for (friend in friends.elements) {
                        dataList.add(Friend(profileImage = friend.profileThumbnailImage, nickName = friend.profileNickname))
                    }
                }
                pickerAdapter.friends = dataList
                pickerAdapter.notifyDataSetChanged()
            }
        }
    }

}

data class Friend(
    var profileImage: String? = null,
    var nickName: String? = null,
    var checked : Boolean = false
){
    object Bind {
        @JvmStatic
        @BindingAdapter("app:imageUri")
        fun loadImage(imageView: ImageView, imageUri: String) {
            if (imageUri.isNullOrEmpty() == false) {
                Picasso.get().load(imageUri).into(imageView)
            }
        }
    }
}
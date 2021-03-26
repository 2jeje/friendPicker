package com.jeje.friendpicker

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.widget.doOnTextChanged
import androidx.databinding.BindingAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kakao.sdk.auth.AuthApiClient
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

    private lateinit var pickerAdapter: FriendPickerAdapter
    private lateinit var selectedAdapter: FriendSelectedAdapter

    private val viewModel : FriendPickerViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        viewModel.friends.observe(viewLifecycleOwner, Observer {
            pickerAdapter.notifyDataSetChanged()
        })

        viewModel.selectedFriends.observe(viewLifecycleOwner, Observer {
            selectedAdapter.notifyDataSetChanged()
        })


        return inflater.inflate(R.layout.fragment_friend_picker, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        search_bar.doOnTextChanged { text, start, before, count ->
            viewModel.searchText = text.toString()
            if (text.isNullOrEmpty()) {
                viewModel.friends.value = viewModel.originFriends.toMutableList()
            }else{
                viewModel.friends.value = viewModel.originFriends.filterIndexed{ index, friend ->
                    KoreanSoundSearchUtils.isMatchString(friend.nickName.toString() ,text.toString() ) != null
                }.toMutableList()
            }
        }

        selectedAdapter = FriendSelectedAdapter(requireContext(), viewModel)
        selected_friends_view.layoutManager = LinearLayoutManager(activity, RecyclerView.HORIZONTAL, false)
        selected_friends_view.adapter = selectedAdapter

        pickerAdapter = FriendPickerAdapter(requireContext(), selectedAdapter, selected_friends_view, viewModel)
        friends_view.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        friends_view.adapter = pickerAdapter

        viewModel.fetch()

        if (viewModel.selectedFriends.value.isNullOrEmpty()) {
            selected_friends_view.visibility = View.GONE
        }
        else {
            selected_friends_view.visibility = View.VISIBLE
        }

        search_bar.setText(viewModel.searchText)
    }
}

class FriendPickerViewModel() : ViewModel() {
    val friends : MutableLiveData<MutableList<Friend>> = MutableLiveData()
    var selectedFriends : MutableLiveData<MutableList<Friend>> = MutableLiveData(mutableListOf())

    val originFriends : MutableList<Friend> = mutableListOf()

    var searchText : String = ""

    fun fetch() {
        if (friends.value != null) {
            return
        }

        TalkApiClient.instance.friendsForPartner(limit = 100) { it, error ->
            if (error != null) {
                Log.i("jeje", "${error}")
            } else {
                if (it != null) {
                    friends.value = mutableListOf()
                    for (friend in it.elements) {
                        val friend = Friend(profileImage = friend.profileThumbnailImage, nickName = friend.profileNickname)
                        friends.value?.add(friend)
                        friends.value = friends.value?.toMutableList()

                        originFriends.add(friend)
                    }
                }
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
            else {
                imageView.setImageResource(R.mipmap.ic_launcher)
            }
        }

        @JvmStatic
        @BindingAdapter("app:nickName")
        fun setNickName(textView: TextView, nickName: String) {
            textView.text = nickName
        }
    }
}
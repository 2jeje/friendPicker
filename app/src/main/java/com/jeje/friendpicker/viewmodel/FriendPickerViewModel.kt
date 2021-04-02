package com.jeje.friendpicker.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.jeje.friendpicker.KoreanSoundSearchUtils
import com.jeje.friendpicker.model.Friend
import com.kakao.sdk.partner.talk.friendsForPartner
import com.kakao.sdk.partner.talk.model.FriendType
import com.kakao.sdk.partner.talk.model.PartnerFriend
import com.kakao.sdk.partner.talk.model.PartnerFriendsContext
import com.kakao.sdk.talk.TalkApiClient
import com.kakao.sdk.talk.model.Friends
import com.kakao.sdk.talk.model.Order

class FriendPickerViewModel : ViewModel() {
    private var _friends = MutableLiveData<MutableList<Friend>>()
    val friends: LiveData<MutableList<Friend>> get() = _friends

    private var _originFriends = MutableLiveData<MutableList<Friend>>(mutableListOf())
    val originFriends: LiveData<MutableList<Friend>> get() = _originFriends

    private var _selectedFriends = MutableLiveData<MutableList<Friend>>(mutableListOf())
    val selectedFriend: LiveData<MutableList<Friend>> get() = _selectedFriends

    var searchText: String = ""

    private var recursiveAppFriendsCompletion: ((Friends<PartnerFriend>?, Error?) -> Unit)? = null

    fun fetch(callback: (startPos: Int?, numberOfItem: Int?, error: Throwable?) -> Unit) {
        if (friends.value != null) {
            return
        }

        var nextFriendsContext = PartnerFriendsContext(
            offset = 0,
            limit = FETCH_COUNT,
            order = Order.ASC,
            friendType = FriendType.KAKAO_TALK
        )
        _friends.value = mutableListOf()

        recursiveAppFriendsCompletion = recursiveAppFriendsCompletion@{ friends, error ->
            if (error == null) {
                if (friends != null) {

                    try {
                        friends.afterUrl?.let {
                            nextFriendsContext = PartnerFriendsContext(url = it)
                        } ?: return@recursiveAppFriendsCompletion

                    } catch (e: IllegalArgumentException) {
                        return@recursiveAppFriendsCompletion
                    }
                }

                TalkApiClient.instance.friendsForPartner(context = nextFriendsContext) { receivedFriends, error ->
                    if (error != null) {
                        Log.e("jeje", "카카오톡 친구 목록 받기 실패", error)
                        callback(null, null, error)
                    } else if (receivedFriends != null) {
                        Log.i(
                            "jeje",
                            "카카오톡 친구 목록 받기 성공 \n${receivedFriends.elements.joinToString("\n")}"
                        )

                        for (friend in receivedFriends.elements) {
                            val friend = Friend(
                                profileImage = friend.profileThumbnailImage,
                                nickName = friend.profileNickname
                            )
                            this.friends.value?.add(friend)
                            _originFriends.value?.add(friend)
                        }
                        _originFriends.value = _originFriends.value?.toMutableList()
                        val afterContext =
                            receivedFriends.afterUrl?.let { PartnerFriendsContext(url = it) }

                        if (afterContext != null) {
                            callback(afterContext.offset, afterContext.limit, null)
                        }
                        recursiveAppFriendsCompletion?.let { it(receivedFriends, null) }
                    }
                }
            }
        }

        recursiveAppFriendsCompletion?.let { it(null, null) }
    }

    fun searchName(text: CharSequence?) {
        if (text.toString() == searchText) {
            return
        }

        searchText = text.toString()

        if (text.isNullOrEmpty()) {
            _friends.value = originFriends.value
        } else {
            _friends.value = originFriends.value?.filterIndexed { index, friend ->
                KoreanSoundSearchUtils.isMatchString(
                    friend.nickName.toString(),
                    text.toString()
                ) != null
            }?.toMutableList()
        }
    }

    fun setSelectedFriends(selectedFriends: List<Friend>){
        _selectedFriends.value = selectedFriends.toMutableList()
    }

    companion object {
        const val FETCH_COUNT = 100
    }
}

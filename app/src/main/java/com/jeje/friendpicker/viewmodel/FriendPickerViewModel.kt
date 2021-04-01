package com.jeje.friendpicker.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.jeje.friendpicker.model.Friend
import com.kakao.sdk.partner.talk.friendsForPartner
import com.kakao.sdk.partner.talk.model.FriendType
import com.kakao.sdk.partner.talk.model.PartnerFriend
import com.kakao.sdk.partner.talk.model.PartnerFriendsContext
import com.kakao.sdk.talk.TalkApiClient
import com.kakao.sdk.talk.model.Friends
import com.kakao.sdk.talk.model.Order
import java.lang.IllegalArgumentException

class FriendPickerViewModel() : ViewModel() {
    val friends: MutableLiveData<MutableList<Friend>> = MutableLiveData()
    var selectedFriends: MutableLiveData<MutableList<Friend>> = MutableLiveData(mutableListOf())

    val originFriends: MutableList<Friend> = mutableListOf()

    var searchText: String = ""

    private var recursiveAppFriendsCompletion: ((Friends<PartnerFriend>?, Error?) -> Unit)? = null

    fun fetch(callback: (startPos: Int?, numberOfItem: Int?, error: Throwable?) -> Unit) {
        if (friends.value != null) {
            return
        }

        var nextFriendsContext = PartnerFriendsContext(
            offset = 0,
            limit = Companion.FETCH_COUNT,
            order = Order.ASC,
            friendType = FriendType.KAKAO_TALK
        )
        this.friends.value = mutableListOf()

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

                        if (receivedFriends != null) {
                            for (friend in receivedFriends.elements) {
                                val friend = Friend(
                                    profileImage = friend.profileThumbnailImage,
                                    nickName = friend.profileNickname
                                )
                                this.friends.value?.add(friend)
                                originFriends.add(friend)
                            }
                            val afterContext =
                                receivedFriends.afterUrl?.let { PartnerFriendsContext(url = it) }

                            if (afterContext != null) {
                                callback(afterContext.offset, afterContext.limit, null)
                            }
                        }
                        recursiveAppFriendsCompletion?.let { it(receivedFriends, null) }
                    }
                }
            }
        }

        recursiveAppFriendsCompletion?.let { it(null, null) }
    }

    companion object {
        const val FETCH_COUNT = 100
    }
}

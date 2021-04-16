package com.jeje.friendpicker.util

import java.util.*
import java.util.concurrent.atomic.AtomicLong

object FriendManager {

    // TODO : comparator - private으로 수정하고, 직접 comparator를 활용하는 대신 sort 호출하도록 수정
    val comparator: FriendComparator = FriendComparator()

    private val oldComparator: FriendOldComparator = FriendOldComparator()


    fun compare(s1: String?, s2: String?): Int {
        return comparator.compare(s1, s2)
    }

    fun compareForOld(s1: String?, s2: String?): Int {
        return oldComparator.compare(s1, s2)
    }

}
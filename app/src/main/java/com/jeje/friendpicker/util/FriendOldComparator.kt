package com.jeje.friendpicker.util

import java.util.Comparator

class FriendOldComparator : Comparator<NameComparable> {

    override fun compare(o1: NameComparable?, o2: NameComparable?): Int {
        return compare(o1?.phoneticNameForSorting, o2?.phoneticNameForSorting)
    }

    fun compare(s1: String?, s2: String?): Int {
        if (s1.isNullOrBlank() && s2.isNullOrBlank()) {
            return 0
        }
        if (s1.isNullOrBlank()) {
            return 1
        }
        if (s2.isNullOrBlank()) {
            return -1
        }

        val leftC = PhonemeUtils.getFirstPhoneme(s1)
        val rightC = PhonemeUtils.getFirstPhoneme(s2)

        var result = PhonemeUtils.startsWith(leftC) - PhonemeUtils.startsWith(rightC)
        if (result == 0) {
            result = leftC - rightC
            if (result == 0) {
                return s1.compareTo(s2)
            }
        }
        return result
    }

}
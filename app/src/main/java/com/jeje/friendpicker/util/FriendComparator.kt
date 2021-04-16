package com.jeje.friendpicker.util




import android.content.res.Resources
import androidx.core.os.ConfigurationCompat

import java.text.Collator
import java.util.*

class FriendComparator : Comparator<NameComparable> {

    private var sortOrder: SortOrderable = getSortOrder()
    private var collator: Collator = sortOrder.collator()

    private fun getSortOrder(): SortOrderable {
        val localeList = ConfigurationCompat.getLocales(Resources.getSystem().configuration)
        return if (localeList.isEmpty) {
            AlphabeticalSortOrder()
        } else {
            when (val lang = localeList[0].language) {
                Locale.KOREAN.language -> KoreanSortOrder()
                Locale.JAPANESE.language -> JapaneseSortOrder()
                RUSSIAN -> RussianSortOrder()
                THAI -> ThaiSortOrder()
                ARABIC -> ArabicSortOrder()
                Locale.CHINESE.language -> ChineseSortOrder()
                else -> AlphabeticalSortOrder(Locale(lang))
            }
        }
    }

    fun resetSortOrder() {
        sortOrder = getSortOrder()
        collator = sortOrder.collator()
    }

    override fun compare(s1: NameComparable?, s2: NameComparable?): Int {
        return compare(s1?.phoneticNameForSorting?.trimZeroWidthChars(),
            s2?.phoneticNameForSorting?.trimZeroWidthChars())
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

        val leftOriginOrder: Int = if (Character.isSurrogate(s1.first())) {
            PhonemeUtils.startsWith(Character.codePointAt(s1, 0))
        } else {
            PhonemeUtils.startsWith(PhonemeUtils.getFirstPhoneme(s1))
        }

        val rightOriginOrder: Int = if (Character.isSurrogate(s2.first())) {
            PhonemeUtils.startsWith(Character.codePointAt(s2, 0))
        } else {
            PhonemeUtils.startsWith(PhonemeUtils.getFirstPhoneme(s2))
        }

        val result = sortOrder.order(leftOriginOrder) - sortOrder.order(rightOriginOrder)

        return if (result == 0) {
            collator.compare(s1, s2)
        } else {
            result
        }
    }

    fun locale(): Locale = sortOrder.locale

    // 영 > 한 > 그 외 언어 > 숫자 > 기타
    class AlphabeticalSortOrder(override val locale: Locale = Locale.ENGLISH) : SortOrderable {
        override fun first(): Int = PhonemeUtils.ALPHABET

        override fun second(): Int = PhonemeUtils.HANGUL
    }

    // 한 > 영 > 그 외 언어 > 숫자 > 기타
    class KoreanSortOrder : SortOrderable {
        override val locale: Locale = Locale.KOREAN

        override fun first(): Int = PhonemeUtils.HANGUL

        override fun second(): Int = PhonemeUtils.ALPHABET
    }

    // 일 > 한 > 그 외 언어 > 숫자 > 기타
    class JapaneseSortOrder : SortOrderable {
        override val locale: Locale = Locale.JAPANESE

        override fun first(): Int = PhonemeUtils.JAPANESE

        override fun second(): Int = PhonemeUtils.HANGUL
    }

    // 러 > 한 > 그 외 언어 > 숫자 > 기타
    class RussianSortOrder : SortOrderable {
        override val locale: Locale = Locale(RUSSIAN)

        override fun first(): Int = PhonemeUtils.CYRILLIC

        override fun second(): Int = PhonemeUtils.HANGUL
    }

    // 태국어 > 한 > 그 외 언어 > 숫자 > 기타
    class ThaiSortOrder : SortOrderable {
        override val locale: Locale = Locale(THAI)

        override fun first(): Int = PhonemeUtils.THAI

        override fun second(): Int = PhonemeUtils.HANGUL
    }

    // 아랍어 > 한 > 그 외 언어 > 숫자 > 기타
    class ArabicSortOrder : SortOrderable {
        override val locale: Locale = Locale(ARABIC)

        override fun first(): Int = PhonemeUtils.ARABIC

        override fun second(): Int = PhonemeUtils.HANGUL
    }

    // 중국어 > 한 > 그 외 언어 > 숫자 > 기타
    class ChineseSortOrder : SortOrderable {
        override val locale: Locale = Locale.CHINESE

        override fun first(): Int = PhonemeUtils.CHINESE

        override fun second(): Int = PhonemeUtils.HANGUL
    }

    // 메인 언어 > 두번째 메인 언어 > (else 그 외 언어) > 숫자 > 기타
    interface SortOrderable {
        val locale: Locale

        fun order(origin: Int): Int {
            return when (origin) {
                first() -> FIRST
                second() -> SECOND
                PhonemeUtils.NUMERIC -> FOURTH
                PhonemeUtils.UNKNOWN -> UNKNOWN
                else -> THIRD
            }
        }

        fun first(): Int

        fun second(): Int

        fun collator(): Collator = Collator.getInstance(locale)
    }

    companion object {
        private const val FIRST = 1
        private const val SECOND = 2
        private const val THIRD = 3
        private const val FOURTH = 4
        private const val UNKNOWN = 5

        private const val RUSSIAN = "ru"
        private const val THAI = "th"
        private const val ARABIC = "ar"
    }

}
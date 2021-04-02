package com.jeje.friendpicker

object KoreanSoundSearchUtils {
    private const val UNICODE_KOAREAN_BEGIN = 44032  // '가'
    private const val UNICODE_KOAREAN_END = 55203   // '힣'
    private const val KOREAN_BASE_UNIT = 588        // 각자음 마다 가지는 글자수
    private val KOAREAN_INITIAL_SOUND = charArrayOf(
        'ㄱ', 'ㄲ', 'ㄴ', 'ㄷ', 'ㄸ',
        'ㄹ', 'ㅁ', 'ㅂ', 'ㅃ', 'ㅅ', 'ㅆ', 'ㅇ', 'ㅈ', 'ㅉ', 'ㅊ', 'ㅋ', 'ㅌ', 'ㅍ', 'ㅎ'
    )

    private fun isInitialSound(keyword: Char): Boolean {
        for (initialSound in KOAREAN_INITIAL_SOUND) {
            if (initialSound == keyword) {
                return true
            }
        }
        return false
    }

    private fun getInitialSound(keyword: Char): Char {
        val begin = keyword.toInt() - UNICODE_KOAREAN_BEGIN
        val index = begin / KOREAN_BASE_UNIT
        return KOAREAN_INITIAL_SOUND[index]
    }

    private fun isKorean(keyword: Char): Boolean {
        return keyword.toInt() in UNICODE_KOAREAN_BEGIN..UNICODE_KOAREAN_END
    }

    fun isMatchString(source: String, keyword: String): Int? {
        val offset = source.length - keyword.length
        if (offset < 0) {
            return null
        }

        val keywordLength = keyword.length
        var searchStartPosition: Int? = null
        for (index in 0..offset) {
            var matchIndex = 0
            while (matchIndex < keywordLength) {
                if (isInitialSound(keyword[matchIndex]) && isKorean(source[index + matchIndex])) {
                    if (getInitialSound(source[index + matchIndex]) == keyword[matchIndex]) {
                        if (searchStartPosition == null) {
                            searchStartPosition = index
                        }
                        matchIndex++
                    } else {
                        break
                    }
                } else {
                    if (source[index + matchIndex].toUpperCase() == keyword[matchIndex].toUpperCase()) {
                        if (searchStartPosition == null) {
                            searchStartPosition = index
                        }
                        matchIndex++
                    } else {
                        break
                    }
                }
            }

            if (matchIndex == keywordLength) {
                return searchStartPosition
            }
        }

        return null
    }
}
package com.jeje.friendpicker.util

import org.apache.commons.lang3.ArrayUtils
import org.apache.commons.lang3.StringUtils
import java.util.*

object PhonemeUtils {
    const val HANGUL = 1
    const val JAPANESE = 2
    const val ALPHABET = 3
    const val CYRILLIC = 4
    const val THAI = 5
    const val ARABIC = 6
    const val CHINESE = 7
    const val NUMERIC = 8
    const val UNKNOWN = 9
    const val HANGUL_BEGIN_UNICODE = 44032 // 가
        .toChar()
    const val HANGUL_END_UNICODE = 55203 // 힣
        .toChar()
    const val HANGUL_BASE_UNIT = 588 // 각자음 마다 가지는 글자수
        .toChar()
    const val JAPANESE_BEGIN_UNICODE = 12353.toChar()
    const val JAPANESE_END_UNICODE = 12534.toChar()
    const val JAPANESE_HIRAGANA_END = 12447.toChar()
    const val JAPANESE_KATAKANA_BEGIN = 12449.toChar()
    const val JAPANESE_HALF_KATAKANA_BEGIN = 0xFF61.toChar()
    const val JAPANESE_HALF_KATAKANA_END = 0xFF9F.toChar()
    private const val ALPHABETIC_LATIN_ADDITIONAL_BEGIN = 0x00C0.toChar()
    private const val ALPHABETIC_LATIN_ADDITIONAL_END = 0x024F.toChar()
    private val ALPHABETIC_LATIN_ADDITIONAL_EXCEPT =
        charArrayOf(0x00D7.toChar(), 0x00F7.toChar())
    private const val ALPHABETIC_LATIN_ADDITIONAL_EXTENDED_BEGIN = 0x1E00.toChar()
    private const val ALPHABETIC_LATIN_ADDITIONAL_EXTENDED_END = 0x1EFF.toChar()
    private const val CYRILLIC_BEGIN = 0x0400.toChar()
    private const val CYRILLIC_END = 0x052F.toChar()
    private const val THAI_BEGIN = 0x0E01.toChar()
    private const val THAI_END = 0x0E5B.toChar()
    private const val ARABIC_BEGIN = 0x0600.toChar()
    private const val ARABIC_END = 0x06FF.toChar()
    private const val ARABIC_SUPPLEMENT_BEGIN = 0x0750.toChar()
    private const val ARABIC_SUPPLEMENT_END = 0x077F.toChar()
    private const val CHINESE_BEGIN = 0x4E00.toChar()
    private const val CHINESE_END = 0x9FFF.toChar()
    private const val CHINESE_EXTENSION_A_BEGIN = 0x3400.toChar()
    private const val CHINESE_EXTENSION_A_END = 0x4DBF.toChar()
    private const val CHINESE_EXTENSION_B_CODEPOINT_BEGIN = 0x20000
    private const val CHINESE_EXTENSION_B_CODEPOINT_END = 0x2A6DF

    @JvmStatic
    val HANGUL_FIRST_PHONEME =
        charArrayOf('ㄱ', 'ㄲ', 'ㄴ', 'ㄷ', 'ㄸ', 'ㄹ', 'ㅁ', 'ㅂ', 'ㅃ', 'ㅅ', 'ㅆ',
            'ㅇ', 'ㅈ', 'ㅉ', 'ㅊ', 'ㅋ', 'ㅌ', 'ㅍ', 'ㅎ')

    @JvmStatic
    val HANGUL_MIDDLE_PHONEME =
        charArrayOf('ㅏ', 'ㅐ', 'ㅑ', 'ㅒ', 'ㅓ', 'ㅔ', 'ㅕ', 'ㅖ', 'ㅗ', 'ㅘ', 'ㅙ',
            'ㅚ', 'ㅛ', 'ㅜ', 'ㅝ', 'ㅞ', 'ㅟ', 'ㅠ', 'ㅡ', 'ㅢ', 'ㅣ')

    @JvmStatic
    val HANGUL_LAST_PHONEME =
        charArrayOf(' ', 'ㄱ', 'ㄲ', 'ㄳ', 'ㄴ', 'ㄵ', 'ㄶ', 'ㄷ', 'ㄹ', 'ㄺ', 'ㄻ', 'ㄼ',
            'ㄽ', 'ㄾ', 'ㄿ', 'ㅀ', 'ㅁ', 'ㅂ', 'ㅄ', 'ㅅ', 'ㅆ', 'ㅇ', 'ㅈ', 'ㅊ', 'ㅋ', 'ㅌ', 'ㅍ', 'ㅎ'
        )
    @JvmStatic
    val JAPANESE_FIRST_PHONEME = charArrayOf('あ', 'か', 'さ', 'た', 'な', 'は', 'ま', 'や', 'ら', 'わ')

    @JvmStatic
    val ALPHABET_FIRST_PHONEME =
        charArrayOf('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p',
            'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'
        )

    @JvmStatic
    val NUMERIC_FIRST_PHONEME = charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9')

    private fun getHangulFirstPhoneme(koreanChar: Char): Char {
        if (!isHangul(koreanChar)) {
            return koreanChar
        }
        val hanBegin = koreanChar - HANGUL_BEGIN_UNICODE
        val index = hanBegin / HANGUL_BASE_UNIT.toInt()
        return HANGUL_FIRST_PHONEME[index]
    }

    /**
     * @url - http://www.i18nguy.com/unicode/hiragana.html
     */
    private fun getJapaneseFirstPhoneme(c: Char): Char {
        val result: Int = if (isHiragana(c)) {
            c - JAPANESE_BEGIN_UNICODE
        } else {
            c - JAPANESE_KATAKANA_BEGIN
        }

        val index = when (result) {
            in 0..9 -> 0
            in 10..19 -> 1
            in 20..29 -> 2
            in 30..40 -> 3
            in 41..45 -> 4
            in 46..60 -> 5
            in 61..65 -> 6
            in 66..71 -> 7
            in 72..76 -> 8
            in 77..83 -> 9
            else -> 0
        }
        return JAPANESE_FIRST_PHONEME[index]
    }

    private fun isHangul(unicode: Char): Boolean {
        return unicode in HANGUL_BEGIN_UNICODE..HANGUL_END_UNICODE
    }

    @JvmStatic
    fun isJapanese(unicode: Char): Boolean {
        return (unicode in JAPANESE_BEGIN_UNICODE..JAPANESE_END_UNICODE
                || unicode in JAPANESE_HALF_KATAKANA_BEGIN..JAPANESE_HALF_KATAKANA_END)
    }

    @JvmStatic
    fun isJapanese(s: CharArray): Boolean {
        for (c in s) {
            if (isJapanese(c)) {
                return true
            }
        }
        return false
    }

    @JvmStatic
    fun isHalfKatakana(c: Char): Boolean {
        return c in JAPANESE_HALF_KATAKANA_BEGIN..JAPANESE_HALF_KATAKANA_END
    }

    @JvmStatic
    fun hasHalfKatakana(s: String): Boolean {
        val array = s.toCharArray()
        for (c in array) {
            if (isHalfKatakana(c)) {
                return true
            }
        }
        return false
    }

    @JvmStatic
    fun isHiragana(unicode: Char): Boolean {
        return unicode in JAPANESE_BEGIN_UNICODE..JAPANESE_HIRAGANA_END
    }

    private fun containAlphabetPhoneme(c: Char): Boolean {
        return (Arrays.binarySearch(ALPHABET_FIRST_PHONEME, c) > -1
                || c in ALPHABETIC_LATIN_ADDITIONAL_BEGIN..ALPHABETIC_LATIN_ADDITIONAL_END
                && !ArrayUtils.contains(ALPHABETIC_LATIN_ADDITIONAL_EXCEPT, c)
                || c in ALPHABETIC_LATIN_ADDITIONAL_EXTENDED_BEGIN..ALPHABETIC_LATIN_ADDITIONAL_EXTENDED_END)
    }

    private fun containNumericPhoneme(c: Char): Boolean {
        return Arrays.binarySearch(NUMERIC_FIRST_PHONEME, c) > -1
    }

    @JvmStatic
    fun containHangulFirstPhoneme(c: Char): Boolean {
        return Arrays.binarySearch(HANGUL_FIRST_PHONEME, c) > -1
    }

    @JvmStatic
    fun containJapaneseFirstPhoneme(c: Char): Boolean {
        return Arrays.binarySearch(JAPANESE_FIRST_PHONEME, c) > -1
    }

    private fun containCyrillicPhoneme(c: Char): Boolean {
        return c in CYRILLIC_BEGIN..CYRILLIC_END
    }

    private fun containThaiPhoneme(c: Char): Boolean {
        return c in THAI_BEGIN..THAI_END
    }

    private fun containArabicPhoneme(c: Char): Boolean {
        return (c in ARABIC_BEGIN..ARABIC_END || c in ARABIC_SUPPLEMENT_BEGIN..ARABIC_SUPPLEMENT_END)
    }

    private fun containChinesePhoneme(c: Char): Boolean {
        return (c in CHINESE_BEGIN..CHINESE_END || c in CHINESE_EXTENSION_A_BEGIN..CHINESE_EXTENSION_A_END)
    }

    private fun containChinesePhoneme(codePoint: Int): Boolean {
        return (codePoint in CHINESE_EXTENSION_B_CODEPOINT_BEGIN..CHINESE_EXTENSION_B_CODEPOINT_END)
    }

    fun startsWith(source: String): Int {
        return startsWith(getFirstPhoneme(source))
    }

    fun startsWith(first: Char): Int {
        return when {
            containHangulFirstPhoneme(first) -> HANGUL
            containAlphabetPhoneme(first) -> ALPHABET
            containNumericPhoneme(first) -> NUMERIC
            containJapaneseFirstPhoneme(first) -> JAPANESE
            containCyrillicPhoneme(first) -> CYRILLIC
            containThaiPhoneme(first) -> THAI
            containArabicPhoneme(first) -> ARABIC
            containChinesePhoneme(first) -> CHINESE
            else -> UNKNOWN
        }
    }

    fun startsWith(codePoint: Int): Int {
        return if (containChinesePhoneme(codePoint)) {
            CHINESE
        } else {
            UNKNOWN
        }
    }

    /**
     * first phoneme of value within search string value = 모토로이 search = ㅌ
     */
    @JvmStatic
    fun getHangulFirstPhoneme(value: String?, search: String?): String? {
        if (value == null || search == null) {
            return null
        }
        return StringBuilder().apply {
            val minLen = value.length.coerceAtMost(search.length)
            for (i in 0 until minLen) {
                val ch = value[i]
                if (isHangul(ch) && containHangulFirstPhoneme(search[i])) {
                    append(getHangulFirstPhoneme(ch))
                } else {
                    append(ch)
                }
            }
        }.toString()
    }

    private fun getHangulSinglePhoneme(c: Char): String {
        return when (c) {
            'ㅘ' -> "ㅗㅏ"
            'ㅙ' -> "ㅗㅏ"
            'ㅚ' -> "ㅗㅣ"
            'ㅝ' -> "ㅜㅓ"
            'ㅞ' -> "ㅜㅔ"
            'ㅟ' -> "ㅜㅣ"
            'ㅢ' -> "ㅡㅣ"
            'ㄲ' -> "ㄱㄱ"
            'ㄳ' -> "ㄱㅅ"
            'ㄵ' -> "ㄴㅈ"
            'ㄶ' -> "ㄴㅎ"
            'ㄸ' -> "ㄷㄷ"
            'ㄺ' -> "ㄹㄱ"
            'ㄻ' -> "ㄹㅁ"
            'ㄼ' -> "ㄹㅂ"
            'ㄽ' -> "ㄹㅅ"
            'ㄾ' -> "ㄹㅌ"
            'ㄿ' -> "ㄹㅍ"
            'ㅀ' -> "ㄹㅎ"
            'ㅃ' -> "ㅂㅂ"
            'ㅄ' -> "ㅂㅅ"
            'ㅆ' -> "ㅅㅅ"
            'ㅉ' -> "ㅈㅈ"
            else -> c.toString()
        }
    }

    @JvmStatic
    fun matchInHangulPhoneme(haystackIn: String, needleIn: String): Boolean {
        val haystack = getHangulPhoneme(haystackIn.toLowerCase(Locale.getDefault()))
        val needle = getHangulPhoneme(needleIn.toLowerCase(Locale.getDefault()))
        return haystack!!.contains(needle!!)
    }

    @JvmStatic
    fun buildHangulPhoneme(c: Char, sb: StringBuilder) {
        sb.apply {
            if (!isHangul(c)) {
                append(c)
                return
            }
            val v = c.toInt() - 0xAC00
            val last = v % 28
            val mid = (v - last) / 28 % 21
            val first = (v - last) / 28 / 21
            append(getHangulSinglePhoneme(HANGUL_FIRST_PHONEME[first]))
            append(getHangulSinglePhoneme(HANGUL_MIDDLE_PHONEME[mid]))
            if (last > 0) {
                append(getHangulSinglePhoneme(HANGUL_LAST_PHONEME[last]))
            }
        }

        return
    }

    @JvmStatic
    fun getHangulPhoneme(c: Char): String {
        val sb = StringBuilder()
        buildHangulPhoneme(c, sb)
        return sb.toString()
    }

    /**
     * 홍길똥 ==> ㅎㅗㅇㄱㅣㄹㄷㄷㅗㅇ
     */
    @JvmStatic
    fun getHangulPhoneme(value: String?): String? {
        if (value == null) {
            return null
        }
        return StringBuilder().apply {
            for (element in value) {
                if (!isHangul(element)) {
                    append(element)
                    continue
                }
                val v = element.toInt() - 0xAC00
                val last = v % 28
                val mid = (v - last) / 28 % 21
                val first = (v - last) / 28 / 21
                append(getHangulSinglePhoneme(HANGUL_FIRST_PHONEME[first]))
                append(getHangulSinglePhoneme(HANGUL_MIDDLE_PHONEME[mid]))
                if (last > 0) {
                    append(getHangulSinglePhoneme(HANGUL_LAST_PHONEME[last]))
                }
            }
        }.toString()
    }

    /**
     * value's first phoneme 모토로이 -> ㅁㅌㄹㅇ
     */
    @JvmStatic
    fun getHangulFirstPhoneme(value: String?): String? {
        if (value == null) {
            return null
        }
        return StringBuilder().apply {
            for (element in value) {
                if (isHangul(element)) {
                    append(getHangulFirstPhoneme(element))
                } else {
                    append(element)
                }
            }
        }.toString()
    }

    @JvmStatic
    fun isHangulFirstPhonemeString(search: String): Boolean {
        for (element in search) {
            if (Arrays.binarySearch(HANGUL_FIRST_PHONEME, element) < 0) {
                return false
            }
        }
        return true
    }

    // '홍ㄱ동' 과 같은 패턴도 매치할 수 있는 로직
    // 더 성능 좋게 짤 수 있도록 고민해보자
    @JvmStatic
    fun isMixedHangulFirstPhoneme(value: String, search: String): Boolean {
        val hfpValue = getHangulFirstPhoneme(value)
        val hfpSearch = getHangulFirstPhoneme(search)
        if (hfpValue == null || hfpSearch == null) return false

        if (!hfpValue.contains(hfpSearch)) {
            return false
        }
        var indexToSearch = 0
        while (indexToSearch <= value.length - search.length) {
            val idxStart = hfpValue.indexOf(hfpSearch, indexToSearch)
            if (idxStart < 0) {
                return false
            }
            indexToSearch = idxStart + 1
            var i = 0
            while (i < search.length) {
                if (hfpValue[idxStart + i] == search[i]) {
                    i++
                    continue
                }
                if (value[idxStart + i] == search[i]) {
                    i++
                    continue
                }

                // 마지막 자일 경우에는 입력 중일 수 있기 때문에 아래 패턴 적용 적용
                if (i == search.length - 1) {
                    val splitedSearch = getHangulPhoneme(search[i])
                    var splitedValue = getHangulPhoneme(value[idxStart + i])
                    if (idxStart + i + 1 < value.length) {
                        splitedValue += hfpValue[idxStart + i + 1]
                    }
                    if (splitedValue.indexOf(splitedSearch) == 0) {
                        i++
                        continue
                    }
                }
                break
            }
            if (i == search.length) {
                return true
            }
        }
        return false
    }

    @JvmStatic
    fun isHangulFirstPhoneme(value: String?, search: String?): Boolean {
        return if (value == null || search == null) {
            if (value != null) true
            else search === value
        } else getHangulFirstPhoneme(value)!!.contains(
            search
        )
    }

    @JvmStatic
    fun isHangulFirstPhonemeStartWith(value: String?, search: String?): Boolean {
        return if (value == null || search == null) {
            if (value != null) true
            else search === value
        } else getHangulFirstPhoneme(value)?.startsWith(search) == true
    }

    @JvmStatic
    fun getFirstPhoneme(value: String): Char {
        if (StringUtils.isBlank(value)) {
            return ' '
        }
        val ch = value[0]
        return when {
            isHangul(ch) -> getHangulFirstPhoneme(ch)
            isJapanese(ch) -> getJapaneseFirstPhoneme(ch)
            else -> Character.toLowerCase(ch)
        }
    }

    @JvmStatic
    fun isJapaneseHiragana(s: String): Boolean {
        if (StringUtils.isBlank(s)) {
            return false
        }
        val sCharArray = s.toCharArray()
        for (c in sCharArray) {
            if (c in JAPANESE_BEGIN_UNICODE..JAPANESE_HIRAGANA_END) {
                return true
            }
        }
        return false
    }

    @JvmStatic
    fun hiraganaToKatakana(s: String): String {
        if (StringUtils.isBlank(s)) {
            return ""
        }
        return StringBuilder().apply {
            for (i in s.indices) {
                if (isHiragana(s[i])) {
                    append(hiraganaToKatakana(s[i]))
                } else {
                    append(s[i])
                }
            }
        }.toString()
    }

    private fun hiraganaToKatakana(c: Char): Char {
        return (c.toInt() + 96).toChar()
    }

    //코드 출처: http://www7a.biglobe.ne.jp/~java-master/samples/string/ZenkakuKatakanaToHankakuKatakana.html
    @JvmStatic
    fun toZenkakuKatakanaIfNeeded(name: String): String {
        return if (name.isEmpty()) {
            name
        } else if (name.length == 1) {
            hankakuKatakanaToZenkakuKatakana(name[0]).toString() + ""
        } else {
            StringBuffer(name).apply {
                var i = 0
                while (i < length - 1) {
                    val originalChar1 = get(i)
                    val originalChar2 = get(i + 1)
                    val margedChar = mergeChar(originalChar1, originalChar2)
                    if (margedChar != originalChar1) {
                        setCharAt(i, margedChar)
                        deleteCharAt(i + 1)
                    } else {
                        val convertedChar =
                            hankakuKatakanaToZenkakuKatakana(originalChar1)
                        if (convertedChar != originalChar1) {
                            setCharAt(i, convertedChar)
                        }
                    }
                    i++
                }
                if (i < length) {
                    val originalChar1 = get(i)
                    val convertedChar = hankakuKatakanaToZenkakuKatakana(originalChar1)
                    if (convertedChar != originalChar1) {
                        setCharAt(i, convertedChar)
                    }
                }
            }.toString()
        }
    }

    private val HANKAKU_KATAKANA = charArrayOf(
        '｡', '｢', '｣', '､', '･',
        'ｦ', 'ｧ', 'ｨ', 'ｩ', 'ｪ', 'ｫ', 'ｬ', 'ｭ', 'ｮ', 'ｯ', 'ｰ', 'ｱ', 'ｲ',
        'ｳ', 'ｴ', 'ｵ', 'ｶ', 'ｷ', 'ｸ', 'ｹ', 'ｺ', 'ｻ', 'ｼ', 'ｽ', 'ｾ', 'ｿ',
        'ﾀ', 'ﾁ', 'ﾂ', 'ﾃ', 'ﾄ', 'ﾅ', 'ﾆ', 'ﾇ', 'ﾈ', 'ﾉ', 'ﾊ', 'ﾋ', 'ﾌ',
        'ﾍ', 'ﾎ', 'ﾏ', 'ﾐ', 'ﾑ', 'ﾒ', 'ﾓ', 'ﾔ', 'ﾕ', 'ﾖ', 'ﾗ', 'ﾘ', 'ﾙ',
        'ﾚ', 'ﾛ', 'ﾜ', 'ﾝ', 0xFF9E.toChar(), 0xFF9F.toChar()
    )
    private val ZENKAKU_KATAKANA = charArrayOf(
        '。', '「', '」', '、', '・',
        'ヲ', 'ァ', 'ィ', 'ゥ', 'ェ', 'ォ', 'ャ', 'ュ', 'ョ', 'ッ', 'ー', 'ア', 'イ',
        'ウ', 'エ', 'オ', 'カ', 'キ', 'ク', 'ケ', 'コ', 'サ', 'シ', 'ス', 'セ', 'ソ',
        'タ', 'チ', 'ツ', 'テ', 'ト', 'ナ', 'ニ', 'ヌ', 'ネ', 'ノ', 'ハ', 'ヒ', 'フ',
        'ヘ', 'ホ', 'マ', 'ミ', 'ム', 'メ', 'モ', 'ヤ', 'ユ', 'ヨ', 'ラ', 'リ', 'ル',
        'レ', 'ロ', 'ワ', 'ン', '゛', '゜'
    )

    private fun hankakuKatakanaToZenkakuKatakana(c: Char): Char {
        return if (c in HANKAKU_KATAKANA_FIRST_CHAR..HANKAKU_KATAKANA_LAST_CHAR) {
            ZENKAKU_KATAKANA[c - HANKAKU_KATAKANA_FIRST_CHAR]
        } else {
            c
        }
    }

    private val HANKAKU_KATAKANA_FIRST_CHAR = HANKAKU_KATAKANA[0]
    private val HANKAKU_KATAKANA_LAST_CHAR = HANKAKU_KATAKANA[HANKAKU_KATAKANA.size - 1]

    private fun mergeChar(c1: Char, c2: Char): Char {
        if (c2 == 0xFF9E.toChar()) {
            if ("ｶｷｸｹｺｻｼｽｾｿﾀﾁﾂﾃﾄﾊﾋﾌﾍﾎ".indexOf(c1) > -1) {
                when (c1) {
                    'ｶ' -> return 'ガ'
                    'ｷ' -> return 'ギ'
                    'ｸ' -> return 'グ'
                    'ｹ' -> return 'ゲ'
                    'ｺ' -> return 'ゴ'
                    'ｻ' -> return 'ザ'
                    'ｼ' -> return 'ジ'
                    'ｽ' -> return 'ズ'
                    'ｾ' -> return 'ゼ'
                    'ｿ' -> return 'ゾ'
                    'ﾀ' -> return 'ダ'
                    'ﾁ' -> return 'ヂ'
                    'ﾂ' -> return 'ヅ'
                    'ﾃ' -> return 'デ'
                    'ﾄ' -> return 'ド'
                    'ﾊ' -> return 'バ'
                    'ﾋ' -> return 'ビ'
                    'ﾌ' -> return 'ブ'
                    'ﾍ' -> return 'ベ'
                    'ﾎ' -> return 'ボ'
                }
            }
        } else if (c2 == 0xFF9F.toChar()) {
            if ("ﾊﾋﾌﾍﾎ".indexOf(c1) > -1) {
                when (c1) {
                    'ﾊ' -> return 'パ'
                    'ﾋ' -> return 'ピ'
                    'ﾌ' -> return 'プ'
                    'ﾍ' -> return 'ペ'
                    'ﾎ' -> return 'ポ'
                }
            }
        }
        return c1
    }

//    fun getOrderedFriendsList(allFriends: List<Friend>, search: String): List<GlobalSearchable> {
//        val map = HashMap<Int, MutableList<Friend>>()
//        val indexResult: MutableList<Friend> = ArrayList()
//        val extraResult: MutableList<Friend> = ArrayList()
//
//        for (friend in allFriends) {
//            val value = friend.filterKeyword
//            val index = value.indexOf(search)
//            when {
//                (index > -1) -> {
//                    var tempResult = map[index]
//                    if (tempResult == null) {
//                        tempResult = ArrayList()
//                        map[index] = tempResult
//                    }
//                    tempResult.add(friend)
//                }
//                isHangulFirstPhoneme(value, search) -> extraResult.add(friend)
//                isMixedHangulFirstPhoneme(value, search) -> extraResult.add(friend)
//                isJapanese(search.toCharArray()) -> {
//                    val katakanaSearch = hiraganaToKatakana(search)
//                    val katakanaItem = hiraganaToKatakana(value)
//                    if (StringUtils.contains(katakanaSearch, katakanaItem)) {
//                        extraResult.add(friend)
//                    }
//                }
//            }
//        }
//        if (map.isNotEmpty()) {
//            val keys = map.keys.toTypedArray()
//            Arrays.sort(keys)
//            for (key in keys) {
//                val result = map[key]
//                if (result != null) indexResult.addAll(result)
//            }
//        }
//
//        return ArrayList<Friend>().apply{
//            addAll(indexResult)
//            addAll(extraResult)
//        }
//    }

    @JvmStatic
    fun isMatch(filterKeyword: String, search: String): Boolean {
        val s1 = filterKeyword.replace("\\s".toRegex(), "").toLowerCase(Locale.getDefault())
        val s2 = search.replace("\\s".toRegex(), "")
        return when {
            isHangulFirstPhoneme(s1, s2) -> true
            s1.contains(s2.toLowerCase(Locale.getDefault())) -> true
            isMixedHangulFirstPhoneme(s1, s2) -> true
            isJapanese(s2.toCharArray()) -> {
                val katakanaSearch = hiraganaToKatakana(s2)
                val katakanaItem = hiraganaToKatakana(s1)
                StringUtils.contains(katakanaSearch, katakanaItem)
            }
            else -> false
        }
    }

    @JvmStatic
    fun isStartWith(filterKeyword: String, search: String): Boolean {
        val s = filterKeyword.replace("\\s".toRegex(), "").toLowerCase(Locale.getDefault())
        if (isHangulFirstPhonemeStartWith(s, search)) {
            return true
        } else if (s.startsWith(search.toLowerCase(Locale.getDefault()))) {
            return true
        }
        return false
    }
}
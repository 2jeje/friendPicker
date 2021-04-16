package com.jeje.friendpicker.util

private const val MVS = '\u180e'            // Mongolian Vowel Separator
private const val ZWSP = '\u200b'           // Zero-width Space
private const val ZWNJ = '\u200c'           // Zero-width Non-joiner
private const val ZWJ = '\u200d'            // Zero-width Joiner
private const val WJ = '\u2060'             // Word Joiner
private const val ELLIPSIZE = '\u2026'
const val RLO = '\u202E'            // Right to Left Override

private val ZERO_WIDTH_CHARS = charArrayOf(MVS, ZWSP, ZWNJ, ZWJ, WJ)

fun String.trimZeroWidthChars(): String = trim(*ZERO_WIDTH_CHARS)
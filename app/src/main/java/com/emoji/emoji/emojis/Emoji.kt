package com.emoji.emoji.emojis

import androidx.annotation.DrawableRes
import com.emoji.emoji.ui.emojis.EmojiRange
import com.example.emoji.R

/**
 * An object that contains information about the processed emoji.
 */
data class Emoji(val emoji: String, @get:DrawableRes val resource: Int)

/**
 * A list of emojis that we can customize.
 */
val EMOJIS: List<Emoji> = listOf(
    Emoji("\ud83d\ude42", R.drawable.simple_smile), // 🙂
    Emoji("\ud83d\udc69\u200d\ud83d\udcbb", R.drawable.female_engineer), // 👩‍💻
    Emoji("\ud83d\ude0e", R.drawable.smiling_with_sunglasses) // 😎
)

/**
 * Generates a regular expression to find all emojis from the list.
 */
fun List<Emoji>.toRegex(): Regex {
    val pattern = joinToString(separator = "|") { emoji ->
        Regex.escape(emoji.emoji)
    }
    return Regex(pattern)
}

/**
 * Gets an Emoji object by the found emoji text.
 */
fun List<Emoji>.getEmojiByText(emojiText: String): Emoji? {
    return firstOrNull { it.emoji == emojiText }
}

/**
 * Finds all occurrences of emojis from the list in the text.
 */
fun List<Emoji>.findEmojisInText(text: String): List<EmojiRange> {
    return toRegex().findAll(text).mapNotNull { match ->
        getEmojiByText(match.value)?.let { emoji ->
            EmojiRange(emoji, match.range)
        }
    }.toList()
}




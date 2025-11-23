package com.emoji.emoji.ui.emojis

import com.emoji.emoji.emojis.Emoji

data class EmojiRange(
    val emoji: Emoji,
    val range: IntRange,
)
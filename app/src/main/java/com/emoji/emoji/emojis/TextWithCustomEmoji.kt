package com.emoji.emoji.ui.emojis

import androidx.compose.foundation.Image
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.sp
import com.emoji.emoji.emojis.EMOJIS
import com.emoji.emoji.emojis.Emoji
import com.emoji.emoji.emojis.findEmojisInText

@Composable
fun TextWithCustomEmoji(
    text: String,
    modifier: Modifier = Modifier,
    emojis: List<Emoji> = EMOJIS
) {
    val emojiRanges = emojis.findEmojisInText(text)

    val annotatedString = buildAnnotatedString {
        var lastIndex = 0

        emojiRanges.forEach { emojiRange ->
            append(text.substring(lastIndex, emojiRange.range.first))
            appendInlineContent(
                id = "emoji_${emojiRange.range.first}",
                alternateText = emojiRange.emoji.emoji
            )
            lastIndex = emojiRange.range.last + 1
        }

        if (lastIndex < text.length) {
            append(text.substring(lastIndex))
        }
    }

    val inlineContent = emojiRanges.associate { emojiRange ->
        "emoji_${emojiRange.range.first}" to InlineTextContent(
            placeholder = Placeholder(
                width = 20.sp,
                height = 20.sp,
                placeholderVerticalAlign = PlaceholderVerticalAlign.TextCenter
            )
        ) {
            Image(
                painter = painterResource(id = emojiRange.emoji.resource),
                contentDescription = emojiRange.emoji.emoji
            )
        }
    }

    Text(
        text = annotatedString,
        inlineContent = inlineContent,
        modifier = modifier
    )
}
package com.emoji.emoji.ui.emojis

import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp

internal const val ZERO_WIDTH_SEPARATOR = "\u2063"
internal const val MARKER = 'M'
internal const val MARKER_LENGTH = MARKER.toString().length + ZERO_WIDTH_SEPARATOR.length

internal fun TextFieldValue.toTextFieldValueWithEmojiMarkers(emojiRanges: List<EmojiRange>): TextFieldValue {
    val annotatedString = buildAnnotatedString {
        var lastIndex = 0

        // Sort ranges by their start position
        emojiRanges.sortedBy { it.range.first }.forEach { emojiRange ->
            // Append the text before the emoji
            if (lastIndex < emojiRange.range.first) {
                append(text.substring(lastIndex, emojiRange.range.first))
            }

            // Append the M marker and the zero-width separator
            append(MARKER)
            append(ZERO_WIDTH_SEPARATOR)

            /*
             * Append the emoji itself with a font size of 0.sp to completely ignore its size. The size
             * of our emoji will be equal to the size of the MARKER character.
             */
            withStyle(style = SpanStyle(fontSize = 0.sp)) {
                append(text.substring(
                    emojiRange.range.first,
                    emojiRange.range.last + 1
                ))
            }

            lastIndex = emojiRange.range.last + 1
        }

        // Append the remaining text
        if (lastIndex < text.length) {
            append(text.substring(lastIndex))
        }
    }

    // Count the number of markers added before the cursor position
    val markersBeforeCursorStart = emojiRanges.count { it.range.first < selection.start }
    val markersBeforeCursorEnd = emojiRanges.count { it.range.first < selection.end }

    // Recalculate the cursor position
    val newSelection = TextRange(
        start = selection.start + (markersBeforeCursorStart * MARKER_LENGTH),
        end = selection.end + (markersBeforeCursorEnd * MARKER_LENGTH)
    )

    return TextFieldValue(
        annotatedString = annotatedString,
        selection = newSelection,
        composition = composition?.let {comp ->
            val markersBeforeCompositionStart = emojiRanges.count { it.range.first < comp.start }
            val markersBeforeCompositionEnd = emojiRanges.count { it.range.first < comp.end }
            TextRange(
                comp.start + (markersBeforeCompositionStart * MARKER_LENGTH),
                comp.end + (markersBeforeCompositionEnd * MARKER_LENGTH)
            )
        }
    )
}

internal fun TextFieldValue.removeEmojiMarkers(): TextFieldValue {
    val markerSequence = "$MARKER$ZERO_WIDTH_SEPARATOR"
    val cleanText = text.replace(markerSequence, "")

    // Count the number of markers before the cursor position
    var markersBeforeCursor = 0
    var searchIndex = 0

    while (searchIndex < selection.start && searchIndex < text.length) {
        val markerIndex = text.indexOf(markerSequence, searchIndex)
        if (markerIndex == -1 || markerIndex >= selection.start) break

        markersBeforeCursor++
        searchIndex = markerIndex + MARKER_LENGTH
    }

    // Recalculate the cursor position
    val cursorOffset = markersBeforeCursor * MARKER_LENGTH
    val newSelection = TextRange(
        start = (selection.start - cursorOffset).coerceAtLeast(0),
        end = (selection.end - cursorOffset).coerceAtLeast(0)
    )

    return TextFieldValue(
        text = cleanText,
        selection = newSelection,
        composition = composition?.let {
            var markersBeforeComposition = 0
            var idx = 0
            while (idx < it.start && idx < text.length) {
                val markerIdx = text.indexOf(markerSequence, idx)
                if (markerIdx == -1 || markerIdx >= it.start) break
                markersBeforeComposition++
                idx = markerIdx + MARKER_LENGTH
            }
            val offset = markersBeforeComposition * MARKER_LENGTH
            TextRange(
                (it.start - offset).coerceAtLeast(0),
                (it.end - offset).coerceAtLeast(0)
            )
        }
    )
}
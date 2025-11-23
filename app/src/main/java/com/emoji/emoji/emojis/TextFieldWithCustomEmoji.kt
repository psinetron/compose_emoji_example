package com.emoji.emoji.ui.emojis

import androidx.compose.foundation.Image
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalTextToolbar
import androidx.compose.ui.platform.TextToolbar
import androidx.compose.ui.platform.TextToolbarStatus
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.rememberTextMeasurer
import com.emoji.emoji.emojis.EMOJIS
import com.emoji.emoji.emojis.findEmojisInText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextFieldWithCustomEmoji(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle = LocalTextStyle.current,
    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = false,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    minLines: Int = 1,
    colors: TextFieldColors = TextFieldDefaults.colors(),
) {
    val clipboardManager = LocalClipboardManager.current
    val originalTextToolbar = LocalTextToolbar.current

    var valueWithMarkers by remember(value) {
        mutableStateOf(value.toTextFieldValueWithEmojiMarkers(EMOJIS.findEmojisInText(value.text)))
    }

    // Update valueWithMarkers when value changes
    LaunchedEffect(value) {
        valueWithMarkers = value.toTextFieldValueWithEmojiMarkers(EMOJIS.findEmojisInText(value.text))
    }

    // Create a custom TextToolbar to intercept copy actions
    val customTextToolbar = remember(value, valueWithMarkers) {
        object : TextToolbar {
            override val status: TextToolbarStatus
                get() = originalTextToolbar.status

            override fun hide() {
                originalTextToolbar.hide()
            }

            override fun showMenu(
                rect: Rect,
                onCopyRequested: (() -> Unit)?,
                onPasteRequested: (() -> Unit)?,
                onCutRequested: (() -> Unit)?,
                onSelectAllRequested: (() -> Unit)?
            ) {
                originalTextToolbar.showMenu(
                    rect = rect,
                    onCopyRequested = onCopyRequested?.let {
                        {
                            // Intercept the copy action
                            copyCleanText(value, valueWithMarkers, clipboardManager)
                        }
                    },
                    onPasteRequested = onPasteRequested,
                    onCutRequested = onCutRequested?.let {
                        {
                            // Intercept the cut action
                            copyCleanText(value, valueWithMarkers, clipboardManager)
                            onCutRequested()
                        }
                    },
                    onSelectAllRequested = onSelectAllRequested
                )
            }
        }
    }

    CompositionLocalProvider(
        LocalTextToolbar provides customTextToolbar
    ) {
        BasicTextField(
            value = valueWithMarkers,
            onValueChange = { newValueWithMarkers ->
                val newClean = newValueWithMarkers.removeEmojiMarkers()
                onValueChange(newClean)
            },
            modifier = modifier,
            enabled = enabled,
            readOnly = readOnly,
            textStyle = textStyle.copy(color = Color.Transparent),
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            maxLines = maxLines,
            minLines = minLines,
            visualTransformation = VisualTransformation.None,
            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
            decorationBox = { innerTextField ->
                TextFieldDefaults.DecorationBox(
                    value = value.text,
                    innerTextField = {
                        Box {
                            innerTextField()
                            DisplayTextWithEmoji(
                                value = value.annotatedString,
                                textStyle = textStyle
                            )
                        }
                    },
                    enabled = enabled,
                    singleLine = singleLine,
                    visualTransformation = VisualTransformation.None,
                    interactionSource = remember { MutableInteractionSource() },
                    isError = isError,
                    label = label,
                    placeholder = placeholder,
                    leadingIcon = leadingIcon,
                    trailingIcon = trailingIcon,
                    colors = colors,
                    contentPadding = TextFieldDefaults.contentPaddingWithLabel()
                )
            }
        )
    }
}

private fun copyCleanText(
    value: TextFieldValue,
    valueWithMarkers: TextFieldValue,
    clipboardManager: ClipboardManager
) {
    val cleanText = if (valueWithMarkers.selection.collapsed) {
        value.text
    } else {
        val markerSequence = "$MARKER$ZERO_WIDTH_SEPARATOR"

        var markersBeforeStart = 0
        var idx = 0
        while (idx < valueWithMarkers.selection.start && idx < valueWithMarkers.text.length) {
            val markerIdx = valueWithMarkers.text.indexOf(markerSequence, idx)
            if (markerIdx == -1 || markerIdx >= valueWithMarkers.selection.start) break
            markersBeforeStart++
            idx = markerIdx + MARKER_LENGTH
        }

        var markersBeforeEnd = 0
        idx = 0
        while (idx < valueWithMarkers.selection.end && idx < valueWithMarkers.text.length) {
            val markerIdx = valueWithMarkers.text.indexOf(markerSequence, idx)
            if (markerIdx == -1 || markerIdx >= valueWithMarkers.selection.end) break
            markersBeforeEnd++
            idx = markerIdx + MARKER_LENGTH
        }

        val originalStart = (valueWithMarkers.selection.start - markersBeforeStart * MARKER_LENGTH)
            .coerceIn(0, value.text.length)
        val originalEnd = (valueWithMarkers.selection.end - markersBeforeEnd * MARKER_LENGTH)
            .coerceIn(0, value.text.length)

        value.text.substring(originalStart, originalEnd)
    }

    clipboardManager.setText(AnnotatedString(cleanText))
}

@Composable
private fun DisplayTextWithEmoji(
    value: AnnotatedString,
    textStyle: TextStyle,
) {
    val textMeasurer = rememberTextMeasurer()
    val density = LocalDensity.current

    val emojiRanges = EMOJIS.findEmojisInText(value.text)

    val annotatedString = buildAnnotatedString {
        var lastIndex = 0

        emojiRanges.forEach { emojiRange ->
            append(value.text.substring(lastIndex, emojiRange.range.first))
            appendInlineContent(
                id = "emoji_${emojiRange.range.first}",
                alternateText = emojiRange.emoji.emoji
            )
            lastIndex = emojiRange.range.last + 1
        }

        if (lastIndex < value.text.length) {
            append(value.text.substring(lastIndex))
        }
    }

    val inlineContent = emojiRanges.associate { emojiRange ->
        val originalEmojiMeasurement = textMeasurer.measure(
            text = MARKER.toString(),
            style = textStyle
        )
        val widthSp = with(density) {
            originalEmojiMeasurement.size.width.toFloat().toDp().toSp()
        }
        val heightSp = with(density) {
            originalEmojiMeasurement.size.height.toFloat().toDp().toSp()
        }

        "emoji_${emojiRange.range.first}" to InlineTextContent(
            placeholder = Placeholder(
                width = widthSp,
                height = heightSp,
                placeholderVerticalAlign = PlaceholderVerticalAlign.TextCenter
            )
        ) {
            Image(
                painter = painterResource(id = emojiRange.emoji.resource),
                contentDescription = emojiRange.emoji.emoji,
                modifier = Modifier.fillMaxSize()
            )
        }
    }

    Text(
        text = annotatedString,
        inlineContent = inlineContent,
        style = textStyle,
    )
}
# Custom Emoji in Jetpack Compose

This project demonstrates how to implement custom emoji rendering in Jetpack Compose applications. It provides solutions for displaying custom emoji images inline with text and handling custom emojis within a `TextField`.

## Features

*   **`TextWithCustomEmoji`**: A composable for displaying text with inline custom emojis.
*   **`TextFieldWithCustomEmoji`**: A custom `TextField` implementation that supports editing text with custom emojis. It handles cursor positioning and text selection correctly even with inline images.
*   **Emoji Parsing**: Automatically detects emoji characters in the input string and replaces them with corresponding drawable resources.
*   **Copy/Paste Support**: Handles copying and pasting of text containing custom emojis, ensuring the underlying text representation is preserved.

## How it works

The core logic involves parsing the text to find emoji sequences and replacing them with inline images.

### `TextWithCustomEmoji`

This component uses `buildAnnotatedString` and `appendInlineContent` to replace specific text ranges (where emojis are found) with placeholders. An `inlineContent` map is then provided to the `Text` composable, which renders the `Image` composable for each emoji.

### `TextFieldWithCustomEmoji`

Handling custom emojis in a `TextField` is more complex due to cursor movement and text editing. This implementation uses a technique involving "markers":

1.  **Markers**: Emojis in the visual text are preceded by a marker character (default 'M') and a zero-width separator. The actual emoji text is scaled to 0 size.
2.  **Visual Transformation**: The `TextField` displays a transformed version of the text where markers govern the size, and `inlineContent` is used to overlay the custom emoji image.
3.  **Cursor Handling**: The `EmojiHelper.kt` utility functions (`toTextFieldValueWithEmojiMarkers`, `removeEmojiMarkers`) map cursor positions between the raw text (without markers) and the visual text (with markers), ensuring the cursor moves logically around the custom emojis.
4.  **Clipboard Interception**: A custom `TextToolbar` is used to intercept copy and cut actions, stripping out the internal markers so that the user gets the original text.

## Project Structure

*   **`MainActivity.kt`**: The entry point of the application, showing usage examples of both `TextWithCustomEmoji` and `TextFieldWithCustomEmoji`.
*   **`ui/emojis/TextWithCustomEmoji.kt`**: Implementation of the static text component with custom emojis.
*   **`ui/emojis/TextFieldWithCustomEmoji.kt`**: Implementation of the editable text field with custom emojis.
*   **`ui/emojis/EmojiHelper.kt`**: Helper functions for managing text transformation and cursor mapping.
*   **`emojis/`**: Contains the emoji definitions and parsing logic (`Emoji.kt`, `EmojiRange.kt`, etc.).

## Usage

### Displaying Text

```kotlin
TextWithCustomEmoji(
    text = "Hello world \uD83D\uDE42",
)
```

### Editing Text

```kotlin
var inputText by remember { mutableStateOf(TextFieldValue("")) }

TextFieldWithCustomEmoji(
    value = inputText,
    onValueChange = { inputText = it },
    modifier = Modifier.fillMaxWidth()
)
```

## Requirements

*   Android SDK 24+
*   Jetpack Compose


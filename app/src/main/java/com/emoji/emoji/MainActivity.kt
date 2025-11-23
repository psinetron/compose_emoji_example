package com.emoji.emoji

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.emoji.emoji.ui.emojis.TextFieldWithCustomEmoji
import com.emoji.emoji.ui.emojis.TextWithCustomEmoji
import com.emoji.emoji.ui.theme.ExampleEmojiTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ExampleEmojiTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    var inputText by remember { mutableStateOf(TextFieldValue("")) }

    Column(modifier.padding(20.dp)) {
        TextWithCustomEmoji(
            text = "Hello $name! This is a custom emoji \uD83D\uDE42\uD83D\uDC69\u200D\uD83D\uDCBB\uD83D\uDE0E",
        )

        Text(
            text = "Hello $name! This is a native emoji \uD83D\uDE42\uD83D\uDC69\u200D\uD83D\uDCBB\uD83D\uDE0E",
        )

        Spacer(modifier = Modifier.height(20.dp))

        TextFieldWithCustomEmoji(
            value = inputText,
            onValueChange = { inputText = it },
            textStyle = TextStyle(fontSize = 20.sp),
            modifier = Modifier.fillMaxWidth()
        )
    }

}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ExampleEmojiTheme {
        Greeting("Android")
    }
}
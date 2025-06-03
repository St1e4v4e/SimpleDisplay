package me.st1e4v4e.simpledisplay

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import me.st1e4v4e.simpledisplay.ui.theme.SimpleDisplayTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MainScreen()
        }
    }
}

data class DisplaySettings(
    var text: String = "Hello World",
    var textSize: Int = 100,

    var colorMode: Int = 0,

    var speed: Int = 500,

    var fgColor: String = "000000",
    var bgColor: String = "ffffff"
)

@Composable
fun MainScreen() {
    val showDialog = remember { mutableStateOf(false) }

    var text by remember { mutableStateOf("Hello World") }
    var textSize by remember { mutableIntStateOf(100) }

    var colorMode by remember { mutableIntStateOf(0) }

    var speed by remember { mutableIntStateOf(500) }
    var fgColor by remember { mutableStateOf("000000") }
    var bgColor by remember { mutableStateOf("ffffff") }

    var currentFgColor by remember { mutableStateOf(fgColor) }
    var currentBgColor by remember { mutableStateOf(bgColor) }

    LaunchedEffect(speed) {
        while (true) {
            if (colorMode == 0) {
                currentFgColor = List(6) {
                    "0123456789abcdef".random()
                }.joinToString("")
                currentBgColor = List(6) {
                    "0123456789abcdef".random()
                }.joinToString("")
            } else {
                currentFgColor = fgColor
                currentBgColor = bgColor
            }
            kotlinx.coroutines.delay(speed.toLong())
        }
    }

    SimpleDisplayTheme {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures(
                        onDoubleTap = {
                            showDialog.value = !showDialog.value
                        }
                    )
                },
            color = Color("ff$currentBgColor".toLong(16)),
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = text,
                    color = Color("ff$currentFgColor".toLong(16)),
                    fontSize = textSize.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    lineHeight = (textSize * 1.2).sp,
                )
            }

            if (showDialog.value) {
                SettingsDialog(
                    DisplaySettings(
                        text = text,
                        textSize = textSize,
                        colorMode = colorMode,
                        speed = speed,
                        fgColor = fgColor,
                        bgColor = bgColor
                    ),
                    onDismissRequest = { showDialog.value = false },
                    onConfirmation = {
                        showDialog.value = false
                        text = it.text
                        textSize = it.textSize
                        colorMode = it.colorMode
                        speed = it.speed
                        fgColor = it.fgColor
                        bgColor = it.bgColor

                        currentFgColor = it.fgColor
                        currentBgColor = it.bgColor
                    }
                )
            }
        }
    }
}

@Composable
fun SettingsDialog(currSettings: DisplaySettings, onDismissRequest: () -> Unit, onConfirmation: (DisplaySettings) -> Unit) {
    var text by remember { mutableStateOf(currSettings.text) }
    var textSize by remember { mutableIntStateOf(currSettings.textSize) }

    var colorMode by remember { mutableIntStateOf(currSettings.colorMode) }
    val colorModes = listOf("Random", "Custom")

    var speed by remember { mutableIntStateOf(currSettings.speed) }

    var fgColor by remember { mutableStateOf(currSettings.fgColor) }
    var bgColor by remember { mutableStateOf(currSettings.bgColor) }

    val showAbout = remember { mutableStateOf(false) }

    Dialog(onDismissRequest = { onDismissRequest() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(if (colorMode == 0) 480.dp else 530.dp)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
            ) {
                Row {
                    Text(
                        text = "Settings",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .weight(1f)
                    )
                    TextButton(
                        onClick = {
                            showAbout.value = !showAbout.value
                        }
                    ) {
                        Text("About")
                    }
                }

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                ) {
                    // Text
                    OutlinedTextField(
                        value = text,
                        label = { Text("Text") },
                        onValueChange = { text = it },
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Text Size
                    OutlinedTextField(
                        value = textSize.toString(),
                        label = { Text("Text Size (sp)") },
                        onValueChange = {
                            value -> value.toIntOrNull()?.let {
                                textSize = value.toInt()
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Color Mode
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Color",
                            fontSize = 12.sp,
                        )
                        Spacer(
                            Modifier.width(8.dp)
                        )
                        SingleChoiceSegmentedButtonRow(
                            Modifier.fillMaxWidth()
                        ) {
                            colorModes.forEachIndexed { index, label ->
                                SegmentedButton(
                                    shape = SegmentedButtonDefaults.itemShape(
                                        index = index,
                                        count = colorModes.size
                                    ),
                                    onClick = { colorMode = index },
                                    selected = index == colorMode,
                                    label = { Text(label, fontSize = 12.sp) }
                                )
                            }
                        }
                    }

                    when (colorMode) {
                        // Random Color Mode
                        0 -> {
                            // Speed
                            OutlinedTextField(
                                value = speed.toString(),
                                label = { Text("Speed (ms)") },
                                onValueChange = { value ->
                                    value.toIntOrNull()?.let {
                                        speed = value.toInt()
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                            )
                        }
                        // Custom Color Mode
                        1 -> {
                            // Foreground Color
                            OutlinedTextField(
                                value = fgColor.toString(),
                                prefix = { Text("#") },
                                label = { Text("Foreground Color") },
                                onValueChange = { fgColor = it },
                                modifier = Modifier.fillMaxWidth(),
                            )

                            // Background Color
                            OutlinedTextField(
                                value = bgColor.toString(),
                                prefix = { Text("#") },
                                label = { Text("Background Color") },
                                onValueChange = { bgColor = it },
                                modifier = Modifier.fillMaxWidth(),
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    TextButton(
                        onClick = { onDismissRequest() },
                    ) { Text("Cancel") }

                    Spacer(modifier = Modifier.width(16.dp))

                    TextButton(
                        onClick = {
                            onConfirmation(
                                DisplaySettings(
                                    text = text,
                                    textSize = textSize,
                                    colorMode = colorMode,
                                    speed = speed,
                                    fgColor = fgColor,
                                    bgColor = bgColor
                                )
                            )
                                  },
                    ) { Text("OK") }
                }
            }
        }
    }

    if (showAbout.value) {
        AboutDialog(onDismissRequest = { showAbout.value = false })
    }
}

@Composable
fun AboutDialog(onDismissRequest: () -> Unit) {
    Dialog(onDismissRequest) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
            ) {
                Text(
                    text = "Simple Display",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = "Version 1.0.0\nMade by St1e4v4e",
                    fontSize = 12.sp,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                Text(
                    text = "A simple app to display custom text with various settings.",
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                TextButton(
                    onClick = onDismissRequest,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text("Close")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainPreview() {
//    SettingsDialog({}, {}, {})
//    AboutDialog({})
}
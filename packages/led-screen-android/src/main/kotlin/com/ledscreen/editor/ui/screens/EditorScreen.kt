package com.ledscreen.editor.ui.screens

import android.graphics.Bitmap
import android.graphics.Canvas as AndroidCanvas
import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ledscreen.editor.ui.theme.BorderColor
import com.ledscreen.editor.ui.theme.DarkBackground
import com.ledscreen.editor.ui.theme.LEDRed
import com.ledscreen.editor.ui.theme.SurfaceDark
import java.io.File
import kotlin.math.floor

const val GRID_WIDTH = 32
const val GRID_HEIGHT = 18
const val PIXEL_SIZE = 15f

@Composable
fun EditorScreen() {
    val context = LocalContext.current
    var pixels by remember { mutableStateOf<Map<String, String>>(emptyMap()) }
    var currentTool by remember { mutableStateOf("pen") }
    var currentColor by remember { mutableStateOf("#FF0000") }
    var isDrawing by remember { mutableStateOf(false) }

    val colors = listOf(
        "#FF0000", "#00FF00", "#0000FF", "#FFFF00",
        "#FF00FF", "#00FFFF", "#FFA500", "#800080",
        "#FFC0CB", "#A52A2A", "#808080", "#FFFFFF"
    )

    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(8.dp)
    ) {
        // Canvas
        Canvas(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .background(SurfaceDark)
                .border(2.dp, LEDRed)
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDrag = { change, _ ->
                            val x = floor((change.position.x / PIXEL_SIZE)).toInt()
                            val y = floor((change.position.y / PIXEL_SIZE)).toInt()

                            if (x in 0 until GRID_WIDTH && y in 0 until GRID_HEIGHT) {
                                val newPixels = pixels.toMutableMap()
                                if (currentTool == "pen") {
                                    newPixels["$x,$y"] = currentColor
                                } else if (currentTool == "eraser") {
                                    newPixels.remove("$x,$y")
                                }
                                pixels = newPixels
                            }
                        }
                    )
                }
        ) {
            drawLEDGrid(pixels)
        }

        // Toolbar
        Column(
            modifier = Modifier
                .width(120.dp)
                .fillMaxHeight()
                .background(SurfaceDark)
                .padding(8.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("Herramientas", fontSize = 12.sp, color = LEDRed)
            ToolButton("✏️", "pen", currentTool) { currentTool = "pen" }
            ToolButton("🧹", "eraser", currentTool) { currentTool = "eraser" }

            Text("Colores", fontSize = 12.sp, color = LEDRed, modifier = Modifier.paddingFromBaseline(12.dp))
            colors.chunked(3).forEach { row ->
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    row.forEach { color ->
                        ColorButton(color, currentColor == color) {
                            currentColor = color
                        }
                    }
                }
            }

            Text("Acciones", fontSize = 12.sp, color = LEDRed, modifier = Modifier.paddingFromBaseline(12.dp))
            Button(
                onClick = { pixels = emptyMap() },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = SurfaceDark)
            ) {
                Text("Limpiar", fontSize = 10.sp, color = LEDRed)
            }
            Button(
                onClick = {
                    exportAsImage(context, pixels)
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = SurfaceDark)
            ) {
                Text("Descargar", fontSize = 10.sp, color = LEDRed)
            }
        }
    }
}

@Composable
fun ToolButton(emoji: String, tool: String, current: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp)
            .background(if (current == tool) LEDRed else SurfaceDark)
            .border(1.dp, if (current == tool) LEDRed else BorderColor),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (current == tool) LEDRed else SurfaceDark
        )
    ) {
        Text(emoji, fontSize = 20.sp)
    }
}

@Composable
fun ColorButton(color: String, selected: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .size(30.dp)
            .border(2.dp, if (selected) Color.White else BorderColor),
        colors = ButtonDefaults.buttonColors(containerColor = Color(android.graphics.Color.parseColor(color)))
    ) {}
}

fun DrawScope.drawLEDGrid(pixels: Map<String, String>) {
    // Draw grid background
    drawRect(color = SurfaceDark)

    // Draw grid lines
    for (i in 0..GRID_WIDTH) {
        drawLine(
            color = BorderColor,
            start = androidx.compose.ui.geometry.Offset(i * PIXEL_SIZE, 0f),
            end = androidx.compose.ui.geometry.Offset(i * PIXEL_SIZE, GRID_HEIGHT * PIXEL_SIZE)
        )
    }

    for (i in 0..GRID_HEIGHT) {
        drawLine(
            color = BorderColor,
            start = androidx.compose.ui.geometry.Offset(0f, i * PIXEL_SIZE),
            end = androidx.compose.ui.geometry.Offset(GRID_WIDTH * PIXEL_SIZE, i * PIXEL_SIZE)
        )
    }

    // Draw pixels
    pixels.forEach { (key, color) ->
        val (x, y) = key.split(",")
        val xPos = x.toInt() * PIXEL_SIZE
        val yPos = y.toInt() * PIXEL_SIZE

        drawRect(
            color = Color(android.graphics.Color.parseColor(color)),
            topLeft = androidx.compose.ui.geometry.Offset(xPos + 1, yPos + 1),
            size = androidx.compose.ui.geometry.Size(PIXEL_SIZE - 2, PIXEL_SIZE - 2)
        )
    }
}

fun exportAsImage(context: android.content.Context, pixels: Map<String, String>) {
    val bitmap = Bitmap.createBitmap(1280, 720, Bitmap.Config.ARGB_8888)
    val canvas = AndroidCanvas(bitmap)

    canvas.drawColor(android.graphics.Color.BLACK)

    val paint = Paint()
    val pixelSize = 40

    pixels.forEach { (key, color) ->
        val (x, y) = key.split(",")
        paint.color = android.graphics.Color.parseColor(color)
        canvas.drawRect(
            (x.toInt() * pixelSize).toFloat(),
            (y.toInt() * pixelSize).toFloat(),
            ((x.toInt() + 1) * pixelSize).toFloat(),
            ((y.toInt() + 1) * pixelSize).toFloat(),
            paint
        )
    }

    val file = File(context.filesDir, "led-screen.png")
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, file.outputStream())
}

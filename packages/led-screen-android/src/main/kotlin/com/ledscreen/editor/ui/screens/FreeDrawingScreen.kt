package com.ledscreen.editor.ui.screens

import android.graphics.Bitmap
import android.graphics.Canvas as AndroidCanvas
import android.graphics.Paint
import android.graphics.PorterDuff
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Slider
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
import com.google.gson.Gson
import com.ledscreen.editor.ui.theme.BorderColor
import com.ledscreen.editor.ui.theme.DarkBackground
import com.ledscreen.editor.ui.theme.LEDRed
import com.ledscreen.editor.ui.theme.SurfaceDark
import java.io.File
import kotlin.math.floor

@Composable
fun FreeDrawingScreen() {
    val context = LocalContext.current
    var brushColor by remember { mutableStateOf("#FF0000") }
    var brushSize by remember { mutableStateOf(3f) }
    var frames by remember { mutableStateOf<List<Bitmap>>(emptyList()) }
    var isDrawing by remember { mutableStateOf(false) }
    var bitmap by remember { mutableStateOf(Bitmap.createBitmap(1024, 576, Bitmap.Config.ARGB_8888)) }

    val canvas = remember { AndroidCanvas(bitmap) }

    LaunchedEffect(Unit) {
        canvas.drawColor(android.graphics.Color.BLACK)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(8.dp)
    ) {
        // Canvas
        Canvas(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(SurfaceDark)
                .border(2.dp, LEDRed)
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDrag = { change, _ ->
                            val paint = Paint().apply {
                                color = android.graphics.Color.parseColor(brushColor)
                                strokeWidth = brushSize
                                strokeCap = Paint.Cap.ROUND
                                strokeJoin = Paint.Join.ROUND
                            }
                            canvas.drawPoint(change.position.x, change.position.y, paint)
                            change.consume()
                        }
                    )
                }
        ) {
            drawImage(bitmap.asImageBitmap())
        }

        // Controls
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(SurfaceDark)
                .padding(8.dp)
                .verticalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Brush controls
            Column(modifier = Modifier.weight(1f)) {
                Text("Pincel", fontSize = 12.sp, color = LEDRed)
                Button(
                    onClick = { /* Color picker */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp)
                        .background(Color(android.graphics.Color.parseColor(brushColor)))
                        .border(1.dp, BorderColor)
                ) {}
                Text("Tamaño: ${brushSize.toInt()}", fontSize = 10.sp, color = Color.White)
                Slider(
                    value = brushSize,
                    onValueChange = { brushSize = it },
                    valueRange = 1f..20f,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Frame controls
            Column(modifier = Modifier.weight(1f)) {
                Text("Fotogramas (${frames.size})", fontSize = 12.sp, color = LEDRed)
                Button(
                    onClick = {
                        val newBitmap = Bitmap.createBitmap(bitmap)
                        frames = frames + newBitmap
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = SurfaceDark)
                ) {
                    Text("Guardar", fontSize = 10.sp, color = LEDRed)
                }
                Button(
                    onClick = {
                        // Reproduce animation
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = SurfaceDark),
                    enabled = frames.isNotEmpty()
                ) {
                    Text("Reproducir", fontSize = 10.sp, color = LEDRed)
                }
            }

            // Actions
            Column(modifier = Modifier.weight(1f)) {
                Text("Acciones", fontSize = 12.sp, color = LEDRed)
                Button(
                    onClick = {
                        canvas.drawColor(android.graphics.Color.BLACK, PorterDuff.Mode.SRC)
                        frames = emptyList()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = LEDRed)
                ) {
                    Text("Limpiar", fontSize = 10.sp, color = Color.Black)
                }
                Button(
                    onClick = {
                        exportAnimation(context, frames)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = SurfaceDark),
                    enabled = frames.isNotEmpty()
                ) {
                    Text("Exportar", fontSize = 10.sp, color = LEDRed)
                }
            }
        }
    }
}

fun exportAnimation(context: android.content.Context, frames: List<Bitmap>) {
    val gson = Gson()
    // Implementation for exporting animation
}

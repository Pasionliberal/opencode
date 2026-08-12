package com.ledscreen.editor

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color as ComposeColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.gson.Gson
import java.io.File

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            App()
        }
    }
}

@Composable
fun App() {
    MaterialTheme {
        var selectedTab by remember { mutableStateOf(0) }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(ComposeColor(0xFF0a0a0a))
        ) {
            // Header
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(ComposeColor(0xFF1a1a1a))
                    .padding(16.dp)
            ) {
                Text(
                    "LED Screen Editor",
                    fontSize = 24.sp,
                    color = ComposeColor(0xFFFF0000),
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TabButton("Editor", selectedTab == 0) { selectedTab = 0 }
                    TabButton("Dibujo Libre", selectedTab == 1) { selectedTab = 1 }
                }
            }

            // Content
            Box(modifier = Modifier.weight(1f)) {
                when (selectedTab) {
                    0 -> EditorTab()
                    1 -> FreeDrawingTab()
                }
            }
        }
    }
}

@Composable
fun TabButton(text: String, isSelected: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .height(40.dp)
            .border(2.dp, ComposeColor(0xFFFF0000)),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) ComposeColor(0xFFFF0000) else ComposeColor.Transparent
        )
    ) {
        Text(
            text,
            color = if (isSelected) ComposeColor.Black else ComposeColor(0xFFFF0000),
            fontSize = 12.sp
        )
    }
}

@Composable
fun EditorTab() {
    val viewModel: EditorViewModel = viewModel()
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    var canvasRef by remember { mutableStateOf<LEDCanvas?>(null) }

    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(ComposeColor(0xFF0a0a0a))
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Canvas
        AndroidView(
            factory = { ctx ->
                LEDCanvas(ctx).apply {
                    canvasRef = this
                    currentTool = state.currentTool
                    currentColor = state.currentColor
                    pixels = state.pixels
                    onPixelChanged = { x, y, color ->
                        viewModel.setPixel(x, y, color)
                    }
                }
            },
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .border(2.dp, ComposeColor(0xFFFF0000))
                .background(ComposeColor(0xFF1a1a1a)),
            update = { canvas ->
                canvas.currentTool = state.currentTool
                canvas.currentColor = state.currentColor
                canvas.pixels = state.pixels
            }
        )

        // Toolbar
        Column(
            modifier = Modifier
                .width(120.dp)
                .fillMaxHeight()
                .background(ComposeColor(0xFF1a1a1a))
                .padding(8.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("Herramientas", fontSize = 11.sp, color = ComposeColor(0xFFFF0000))
            ToolButton("✏️", state.currentTool == "pen") {
                viewModel.setTool("pen")
            }
            ToolButton("🧹", state.currentTool == "eraser") {
                viewModel.setTool("eraser")
            }

            Text("Colores", fontSize = 11.sp, color = ComposeColor(0xFFFF0000), modifier = Modifier.padding(top = 12.dp))
            val colors = listOf(
                "#FF0000", "#00FF00", "#0000FF", "#FFFF00",
                "#FF00FF", "#00FFFF", "#FFA500", "#800080",
                "#FFC0CB", "#A52A2A", "#808080", "#FFFFFF"
            )
            colors.chunked(3).forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    row.forEach { color ->
                        ColorButton(color, state.currentColor == color) {
                            viewModel.setColor(color)
                        }
                    }
                }
            }

            Text("Acciones", fontSize = 11.sp, color = ComposeColor(0xFFFF0000), modifier = Modifier.padding(top = 12.dp))
            ActionButton("Limpiar") {
                viewModel.clearCanvas()
            }
            ActionButton("Descargar") {
                exportImage(context, state.pixels)
            }
        }
    }
}

@Composable
fun FreeDrawingTab() {
    val viewModel: EditorViewModel = viewModel()
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    var bitmap by remember { mutableStateOf(Bitmap.createBitmap(800, 600, Bitmap.Config.ARGB_8888)) }
    var brushColor by remember { mutableStateOf(Color.parseColor("#FF0000")) }
    var brushSize by remember { mutableStateOf(5f) }

    LaunchedEffect(Unit) {
        val canvas = Canvas(bitmap)
        canvas.drawColor(Color.BLACK)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ComposeColor(0xFF0a0a0a))
            .padding(8.dp)
    ) {
        // Canvas con dibujo
        AndroidView(
            factory = { ctx ->
                object : android.view.View(ctx) {
                    override fun onDraw(canvas: android.graphics.Canvas) {
                        super.onDraw(canvas)
                        canvas.drawBitmap(bitmap, 0f, 0f, null)
                    }
                }.apply {
                    setOnTouchListener { _, event ->
                        val paint = android.graphics.Paint().apply {
                            color = brushColor
                            strokeWidth = brushSize
                            strokeCap = android.graphics.Paint.Cap.ROUND
                        }
                        when (event.action) {
                            android.view.MotionEvent.ACTION_DOWN,
                            android.view.MotionEvent.ACTION_MOVE -> {
                                val canvas = Canvas(bitmap)
                                canvas.drawPoint(event.x, event.y, paint)
                                invalidate()
                            }
                        }
                        true
                    }
                }
            },
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(ComposeColor(0xFF1a1a1a))
                .border(2.dp, ComposeColor(0xFFFF0000))
        )

        // Controles
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(ComposeColor(0xFF1a1a1a))
                .padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Color", fontSize = 10.sp, color = ComposeColor(0xFFFF0000))
                Button(
                    onClick = { brushColor = Color.parseColor("#FF0000") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(35.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ComposeColor(0xFFFF0000))
                ) {}
            }

            Column(modifier = Modifier.weight(1f)) {
                Text("Tamaño", fontSize = 10.sp, color = ComposeColor(0xFFFF0000))
                Text("${brushSize.toInt()}px", fontSize = 9.sp, color = ComposeColor.White)
            }

            Column(modifier = Modifier.weight(1f)) {
                Text("Acciones", fontSize = 10.sp, color = ComposeColor(0xFFFF0000))
                ActionButton("Limpiar") {
                    val canvas = Canvas(bitmap)
                    canvas.drawColor(Color.BLACK)
                    viewModel.clearFrames()
                }
            }
        }
    }
}

@Composable
fun ToolButton(emoji: String, isSelected: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp)
            .border(if (isSelected) 2.dp else 1.dp, ComposeColor(0xFFFF0000)),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) ComposeColor(0xFFFF0000) else ComposeColor(0xFF2a2a2a)
        )
    ) {
        Text(emoji, fontSize = 20.sp)
    }
}

@Composable
fun ColorButton(color: String, isSelected: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .size(30.dp)
            .border(2.dp, if (isSelected) ComposeColor.White else ComposeColor(0xFF333333)),
        colors = ButtonDefaults.buttonColors(
            containerColor = ComposeColor(android.graphics.Color.parseColor(color))
        )
    ) {}
}

@Composable
fun ActionButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(36.dp)
            .border(2.dp, ComposeColor(0xFFFF0000)),
        colors = ButtonDefaults.buttonColors(containerColor = ComposeColor(0xFF2a2a2a))
    ) {
        Text(text, fontSize = 10.sp, color = ComposeColor(0xFFFF0000))
    }
}

fun exportImage(context: android.content.Context, pixels: Map<String, String>) {
    val bitmap = Bitmap.createBitmap(1280, 720, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    canvas.drawColor(Color.BLACK)

    val paint = android.graphics.Paint()
    pixels.forEach { (key, color) ->
        val (x, y) = key.split(",")
        paint.color = Color.parseColor(color)
        val left = (x.toInt() * 40).toFloat()
        val top = (y.toInt() * 40).toFloat()
        canvas.drawRect(left, top, left + 40, top + 40, paint)
    }

    val file = File(context.cacheDir, "led-screen.png")
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, file.outputStream())
}

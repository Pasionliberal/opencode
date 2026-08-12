package com.ledscreen.editor

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class EditorState(
    val pixels: Map<String, String> = emptyMap(),
    val currentTool: String = "pen",
    val currentColor: String = "#FF0000",
    val brushSize: Float = 3f,
    val frames: List<Bitmap> = emptyList(),
    val canvasWidth: Int = 32,
    val canvasHeight: Int = 18,
)

class EditorViewModel : ViewModel() {
    private val _state = MutableStateFlow(EditorState())
    val state: StateFlow<EditorState> = _state

    fun setPixel(x: Int, y: Int, color: String?) {
        val newPixels = _state.value.pixels.toMutableMap()
        if (color == null) {
            newPixels.remove("$x,$y")
        } else {
            newPixels["$x,$y"] = color
        }
        _state.value = _state.value.copy(pixels = newPixels)
    }

    fun setTool(tool: String) {
        _state.value = _state.value.copy(currentTool = tool)
    }

    fun setColor(color: String) {
        _state.value = _state.value.copy(currentColor = color)
    }

    fun setBrushSize(size: Float) {
        _state.value = _state.value.copy(brushSize = size)
    }

    fun clearCanvas() {
        _state.value = _state.value.copy(pixels = emptyMap())
    }

    fun addFrame(bitmap: Bitmap) {
        _state.value = _state.value.copy(frames = _state.value.frames + bitmap)
    }

    fun clearFrames() {
        _state.value = _state.value.copy(frames = emptyList())
    }
}

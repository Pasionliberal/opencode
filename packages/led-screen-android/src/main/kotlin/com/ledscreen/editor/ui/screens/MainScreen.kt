package com.ledscreen.editor.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ledscreen.editor.ui.theme.BorderColor
import com.ledscreen.editor.ui.theme.DarkBackground
import com.ledscreen.editor.ui.theme.LEDRed
import com.ledscreen.editor.ui.theme.SurfaceDark

@Composable
fun MainScreen() {
    var selectedTab by remember { mutableStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        // Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(SurfaceDark)
                .padding(16.dp)
        ) {
            Text(
                "LED Screen Editor",
                fontSize = 24.sp,
                color = LEDRed,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // Tab selector
            TabRow(
                selectedTabIndex = selectedTab,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                containerColor = SurfaceDark,
                contentColor = LEDRed,
                divider = {}
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Editor") },
                    modifier = Modifier.background(
                        if (selectedTab == 0) LEDRed else Color.Transparent
                    )
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Dibujo Libre") },
                    modifier = Modifier.background(
                        if (selectedTab == 1) LEDRed else Color.Transparent
                    )
                )
            }
        }

        // Content
        Box(modifier = Modifier.weight(1f)) {
            when (selectedTab) {
                0 -> EditorScreen()
                1 -> FreeDrawingScreen()
            }
        }
    }
}

package com.example.mywayapp.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TitleBar(name: String) {
    Text(text = name, fontSize = 25.sp, color = Color.White)
}

@Composable
fun ActionButton(onClick: () -> Unit) {
    FloatingActionButton(
        onClick = { onClick() }, containerColor = Color(0xFF1976D2), contentColor = Color.White
    ) {
        Icon(imageVector = Icons.Default.Add, contentDescription = "Agrega")
    }
}

@Composable
fun MainIconButton(icon: ImageVector, onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        Icon(imageVector = icon, contentDescription = null, tint = Color.White)
    }
}

@Composable
fun ProfileIconButton(icon: ImageVector, onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(35.dp),
            tint = Color.White
        )
    }
}
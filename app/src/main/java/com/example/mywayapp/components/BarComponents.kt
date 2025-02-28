package com.example.mywayapp.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.mywayapp.model.Usuarios

@Composable
fun TitleBar(name: String) {
    Text(text = name, fontSize = 25.sp, color = Color.White)
}

@Composable
fun ActionButton(onClick: () -> Unit) {
    FloatingActionButton(
        onClick = { onClick() },
        containerColor = Color(0.129f, 0.302f, 0.986f, 1f),
        contentColor = Color.White
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
fun ProfileIconButton(icon: ImageVector, state: Usuarios, onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        if (state.iconoPerfil.isEmpty()) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(45.dp),
                tint = Color(0.984f, 0.988f, 0.988f, 1f)
            )
        } else {
            AsyncImage(
                model = state.iconoPerfil.ifEmpty { },
                contentDescription = null,
                modifier = Modifier
                    .size(45.dp)
                    .clip(CircleShape)
                    .background(Color(0.984f, 0.988f, 0.988f, 1f)),
            )
        }
    }
}
package com.example.mywayapp.views

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.mywayapp.components.Alert
import com.example.mywayapp.components.MainButton
import com.example.mywayapp.components.MainIconButton
import com.example.mywayapp.components.MainTextField
import com.example.mywayapp.components.Space
import com.example.mywayapp.components.SpaceW
import com.example.mywayapp.components.TitleBar
import com.example.mywayapp.viewModels.UsuariosViewModel

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ProfileView(navController: NavController, viewModel: UsuariosViewModel) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { TitleBar(name = "Editar Perfil") },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0f, 0.129f, 0.302f, 1f)
                ),
                navigationIcon = {
                    MainIconButton(icon = Icons.AutoMirrored.Filled.ArrowBack) {
                        navController.popBackStack()
                    }
                })
        },
    ) {
        ContentProfileView(paddingValues = it, navController, viewModel)
    }
}

@Composable
fun ContentProfileView(
    paddingValues: PaddingValues,
    navController: NavController,
    viewModel: UsuariosViewModel
) {
    LaunchedEffect(Unit) {
        viewModel.loadProfileIcons()
    }

    val state = viewModel.state.collectAsState().value
    val nombreFocusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val iconos = viewModel.iconos.collectAsState().value

    Column(
        modifier = Modifier
            .padding(paddingValues)
            .padding(10.dp)
            .fillMaxSize()
            .verticalScroll(scrollState), horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Space(16.dp)

        // Imagen actual del perfil
        if (state.iconoPerfil.isEmpty()) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(Color.Gray),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = "Logo",
                    tint = Color.White,
                    modifier = Modifier.size(50.dp)
                )
            }
        } else {
            AsyncImage(
                model = state.iconoPerfil.ifEmpty { },
                contentDescription = "Perfil",
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(Color.Gray),
                contentScale = ContentScale.Crop
            )
        }

        Space(16.dp)

        Text(
            text = "Selecciona un avatar:",
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )

        Space(16.dp)

        // Lista de iconos
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            items(iconos) { iconUrl ->
                AsyncImage(
                    model = iconUrl,
                    contentDescription = "Icono",
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(Color.LightGray)
                        .clickable {
                            viewModel.updateIconProfile(iconUrl) { success, message ->
                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                            }
                        },
                    contentScale = ContentScale.Crop
                )
            }
        }

        Space(20.dp)

        MainTextField(
            value = state.nombre,
            onValue = { viewModel.onValueChange("nombre", it) },
            label = "Nombre:",
            keyboardType = KeyboardType.Text,
            focusRequester = nombreFocusRequester,
            maxLength = 9
        )

        Space(10.dp)

        MainTextField(
            value = state.apellido,
            onValue = { viewModel.onValueChange("apellido", it) },
            label = "Apellido:",
            keyboardType = KeyboardType.Text,
            focusRequester = remember { FocusRequester() },
            maxLength = 50
        )

        Space(20.dp)

        Row(
            modifier = Modifier.fillMaxSize(), horizontalArrangement = Arrangement.Center
        ) {
            MainButton(
                name = "Guardar",
                backColor = Color(0.129f, 0.302f, 0.986f, 1f),
                color = Color.White
            ) {
                if (state.nombre != "" && state.apellido != "") {
                    viewModel.updateUsuario { success, message ->
                        if (success) {
                            Toast.makeText(
                                context, message, Toast.LENGTH_SHORT
                            ).show()
                            navController.popBackStack()
                        } else {
                            Toast.makeText(context, "Error: $message", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    focusManager.moveFocus(FocusDirection.Down)
                    viewModel.cambiaAlert()
                }
            }
            SpaceW()
            MainButton(
                name = "Cancelar",
                backColor = Color(1f, 0.329f, 0.439f, 1f),
                color = Color.White
            ) {
                navController.popBackStack()
            }
        }
    }
    if (state.showAlert) {
        Alert(title = "¡Atención!",
            message = "Todos los campos deben ser llenados.",
            confirmText = "Aceptar",
            onConfirmClick = {
                viewModel.cancelAlert()
            }) { }
    }
}
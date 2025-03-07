package com.example.mywayapp.views

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.mywayapp.components.ActionButton
import com.example.mywayapp.components.ProfileIconButton
import com.example.mywayapp.components.TitleBar
import com.example.mywayapp.model.Usuarios
import com.example.mywayapp.viewModels.HabitosViewModel
import com.example.mywayapp.viewModels.UsuariosViewModel

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeView(
    navController: NavController,
    viewModelHabits: HabitosViewModel,
    viewModelUsers: UsuariosViewModel,
    usuario: Usuarios
) {
    val state = viewModelUsers.state.collectAsState().value
    viewModelHabits.onUsuarioCargado(usuario.uidUsuario)

    Scaffold(topBar = {
        CenterAlignedTopAppBar(
            title = { TitleBar(name = "MyWayApp") },
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = Color(0f, 0.129f, 0.302f, 1f)
            ),
            actions = {
                ProfileIconButton(icon = Icons.Filled.AccountCircle, state = state) {
                    navController.navigate("UpdateProfile/${usuario.uidUsuario}")
                }
            },
        )
    }, floatingActionButton = {
        ActionButton(onClick = {
            viewModelHabits.limpiar()
            navController.navigate("Add")
        })
    }) {
        ContentHomeView(
            paddingValues = it,
            viewModelHabits,
            viewModelUsers,
            navController
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ContentHomeView(
    paddingValues: PaddingValues,
    viewModelHabits: HabitosViewModel,
    viewModelUsers: UsuariosViewModel,
    navController: NavController
) {
    val habitosList by viewModelHabits.habitosUsuario.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        if (habitosList.isEmpty()) {
            item {
                Box(
                    modifier = Modifier.fillParentMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No hay hábitos para mostrar :)",
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }
            }
        } else {
            items(habitosList) { usuarioHabito ->
                UsuarioHabitoItem(viewModelUsers, usuarioHabito, navController)
            }
        }
    }
}
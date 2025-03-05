package com.example.mywayapp.views

import android.annotation.SuppressLint
import android.content.Context
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.mywayapp.FCMHelper
import com.example.mywayapp.components.ActionButton
import com.example.mywayapp.components.ProfileIconButton
import com.example.mywayapp.components.TitleBar
import com.example.mywayapp.model.Usuarios
import com.example.mywayapp.viewModels.HabitosViewModel
import com.example.mywayapp.viewModels.UsuarioHabitosViewModel
import com.example.mywayapp.viewModels.UsuariosViewModel
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeView(
    navController: NavController,
    viewModel: HabitosViewModel,
    viewModelUsers: UsuariosViewModel,
    usuario: Usuarios
) {
    val state = viewModelUsers.state.collectAsState().value

    val usuarioHabitosViewModel =
        remember { UsuarioHabitosViewModel() } // Instanciamos el ViewModel para la relación usuario-hábito

    LaunchedEffect(usuario.uidUsuario) { // Cuando se inicie la pantalla, obtenemos la lista de hábitos del usuario
        usuarioHabitosViewModel.fetchUsuarioHabitos(usuario.uidUsuario)
    }

    val usuarioHabitosList by usuarioHabitosViewModel.usuarioHabitosList.collectAsState() // Convertimos las fechas de inicio a LocalDate.
    val markedDates = usuarioHabitosList.mapNotNull { habit ->
        try { // Suponiendo que habit.fechaInicio está en "dd/MM/yyyy"
            LocalDate.parse(habit.fechaInicio, DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        } catch (e: Exception) {
            null
        }
    }

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
            viewModel.limpiar()
            navController.navigate("Add")
        })
    }) {
        ContentHomeView(
            paddingValues = it,
            usuarioHabitosViewModel = usuarioHabitosViewModel,
            viewModelUsers
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ContentHomeView(
    paddingValues: PaddingValues,
    usuarioHabitosViewModel: UsuarioHabitosViewModel,
    viewModelUsers: UsuariosViewModel
) {
    val state = viewModelUsers.state.collectAsState().value

    val usuarioHabitosList =
        usuarioHabitosViewModel.usuarioHabitosList.collectAsState(initial = emptyList()).value

    // Estado de SwipeRefresh
    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = false)

    // Función para refrescar los datos
    fun onRefresh() {
        // Aquí puedes llamar a la función para cargar o actualizar los datos
        usuarioHabitosViewModel.fetchUsuarioHabitos(state.uidUsuario) // Pasar el id adecuado
    }

    SwipeRefresh(
        state = swipeRefreshState,
        onRefresh = { onRefresh() } // Llama a la función onRefresh
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (usuarioHabitosList.isEmpty()) {
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
                items(usuarioHabitosList) { usuarioHabito ->
                    UsuarioHabitoItem(viewModelUsers, usuarioHabito)

//                    val state = viewModelUsers.state.collectAsState().value
//                    val context = LocalContext.current
//                    val sharedPref = context.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
//                    val progreso = sharedPref.getInt(
//                        "diasTranscurridos",
//                        -1
//                    ) // -1 es el valor por defecto si no se encuentra el valor
//                    println(progreso)
//                    // Verificar si ya se ha guardado el valor previamente y si el progreso ha cambiado
//                    if (progreso != -1 && progreso > 0 && state.tokenFCM != null) {
//                        val fcmHelper = FCMHelper(context)
//
//                        // Definir los mensajes según los días transcurridos
//                        val mensaje = when {
//                            progreso == 1 -> "¡Ha pasado 1 día! ¡Sigue así, lo estás logrando!"
//                            progreso in 2..3 -> "¡Llevas $progreso días! ¡Sigue avanzando!"
//                            progreso in 4..6 -> "¡Estás a punto de cumplir una semana! ¡Sigue con esa actitud!"
//                            progreso == 7 -> "¡Ya llevas una semana! ¡Tu progreso es increíble!"
//                            progreso in 8..30 -> "¡Un mes de esfuerzo! ¡Sigue con esa actitud!"
//                            progreso in 31..180 -> "¡6 meses! Estás cerca de tu meta, no pares."
//                            progreso in 181..365 -> "¡Un año de esfuerzo! Estás muy cerca de lograrlo."
//                            else -> "¡Sigue así, gran trabajo! ¡El progreso es el camino!"
//                        }
//
//                        // Enviar la notificación
//                        fcmHelper.sendNotification(state.tokenFCM, "Avance en tu hábito", mensaje)
//                    }
                }
            }
        }
    }
}


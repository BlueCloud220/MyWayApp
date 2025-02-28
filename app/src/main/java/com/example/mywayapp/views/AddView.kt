package com.example.mywayapp.views

import android.annotation.SuppressLint
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mywayapp.components.Alert
import com.example.mywayapp.components.DatePickerDocked
import com.example.mywayapp.components.HabitoDropdown
import com.example.mywayapp.components.MainButton
import com.example.mywayapp.components.MainIconButton
import com.example.mywayapp.components.ReadOnlyTextField
import com.example.mywayapp.components.Space
import com.example.mywayapp.components.SpaceW
import com.example.mywayapp.components.TitleBar
import com.example.mywayapp.model.Habitos
import com.example.mywayapp.model.Usuarios
import com.example.mywayapp.viewModels.HabitosViewModel
import com.example.mywayapp.viewModels.UsuarioHabitosViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AddView(
    navController: NavController,
    habitosViewModel: HabitosViewModel,
    usuario: Usuarios  // Recibimos el usuario logueado
) {
    val usuarioHabitosViewModel =
        remember { UsuarioHabitosViewModel() } // Instanciamos el ViewModel para gestionar la relación usuario-hábito

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { TitleBar(name = "Agregar Hábito") },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0f, 0.129f, 0.302f, 1f)
                ),
                navigationIcon = {
                    MainIconButton(icon = Icons.AutoMirrored.Filled.ArrowBack) {
                        navController.popBackStack()
                    }
                }
            )
        }
    ) {
        ContentAddView(
            paddingValues = it,
            navController = navController,
            habitosViewModel = habitosViewModel,
            usuarioHabitosViewModel = usuarioHabitosViewModel,
            usuario = usuario
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ContentAddView(
    paddingValues: PaddingValues,
    navController: NavController,
    habitosViewModel: HabitosViewModel,
    usuarioHabitosViewModel: UsuarioHabitosViewModel,
    usuario: Usuarios
) {
    val nombreFocusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val habitosState = habitosViewModel.state.collectAsState().value
    val usuarioHabitosState = usuarioHabitosViewModel.state.collectAsState().value
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    val showErrorAlert =
        rememberSaveable { mutableStateOf(false) } // Estados para controlar la alerta de error en el guardado
    val errorAlertMessage = rememberSaveable { mutableStateOf("") }

    LaunchedEffect(Unit) { // Actualizamos la fecha si está vacía para que el estado se actualice y pase la validación
        if (habitosState.fechaInicio.isEmpty()) {
            val today = getTodayDate()
            habitosViewModel.onValueChange("fechaInicio", today)
            usuarioHabitosViewModel.onValueChange("fechaInicio", today)
        }
    }

    Column(
        modifier = Modifier
            .padding(paddingValues)
            .padding(10.dp)
            .fillMaxSize()
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val selectedNombre =
            rememberSaveable { mutableStateOf(habitosState.nombre) } // Variables para almacenar la selección actual
        val selectedDescripcion = rememberSaveable { mutableStateOf(habitosState.descripcion) }

        HabitoDropdown(viewModel = habitosViewModel) { nombre, descripcion, uidHabito -> // Se asume que el callback del Dropdown retorna también el uid del hábito
            selectedNombre.value = nombre
            selectedDescripcion.value = descripcion
            habitosViewModel.onValueChange("nombre", nombre)
            habitosViewModel.onValueChange("descripcion", descripcion)
            usuarioHabitosViewModel.onHabitoSeleccionado(
                Habitos(uidHabito = uidHabito, nombre = nombre, descripcion = descripcion)
            )
        }

        Space(10.dp)

        ReadOnlyTextField(
            value = selectedDescripcion.value,
            label = "Descripción:"
        )

        Space(10.dp)

        DatePickerDocked(
            value = habitosState.fechaInicio.ifEmpty { getTodayDate() },
            label = "Fecha de inicio:",
            onValue = { date ->
                habitosViewModel.onValueChange("fechaInicio", date)
                usuarioHabitosViewModel.onValueChange("fechaInicio", date)
            }
        )

        Space(20.dp)

        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.Center
        ) {
            MainButton(
                name = "Guardar",
                backColor = Color(0.129f, 0.302f, 0.986f, 1f),
                color = Color(0.984f, 0.988f, 0.988f, 1f)
            ) {
                if (usuarioHabitosState.uidHabito.isNotEmpty() && usuarioHabitosState.fechaInicio.isNotEmpty()) {
                    usuarioHabitosViewModel.onValueChange(
                        "uidUsuario",
                        usuario.uidUsuario
                    ) // Asignamos el uid del usuario logueado al estado de usuario-hábito
                    usuarioHabitosViewModel.saveUsuarioHabito { success, message ->
                        if (success) {
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                            habitosViewModel.limpiar()
                            usuarioHabitosViewModel.limpiar()
                            navController.popBackStack()
                        } else {
                            // En caso de error, mostramos la alerta personalizada
                            errorAlertMessage.value = message
                            showErrorAlert.value = true
                        }
                    }
                } else {
                    focusManager.moveFocus(FocusDirection.Down)
                    habitosViewModel.cambiaAlert()
                }
            }
            SpaceW()
            MainButton(
                name = "Cancelar",
                backColor = Color(1f, 0.329f, 0.439f, 1f),
                color = Color(0.984f, 0.988f, 0.988f, 1f)
            ) {
                navController.popBackStack()
            }
        }
    }
    if (showErrorAlert.value) {
        Alert(
            title = "¡Atención!",
            message = errorAlertMessage.value,
            confirmText = "Aceptar",
            onConfirmClick = { showErrorAlert.value = false }
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun getTodayDate(): String {
    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    return LocalDate.now().format(formatter)
}
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mywayapp.FCMHelper
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
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AddView(
    navController: NavController, viewModel: HabitosViewModel, usuario: Usuarios
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { TitleBar(name = "Agregar Hábito") },
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
        ContentAddView(
            paddingValues = it, navController, viewModel, usuario = usuario
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ContentAddView(
    paddingValues: PaddingValues,
    navController: NavController,
    viewModel: HabitosViewModel,
    usuario: Usuarios
) {
    val state = viewModel.state.collectAsState().value
    val scrollState = rememberScrollState()
    val habitosList by viewModel.habitos.collectAsState()
    var selectedHabito by remember { mutableStateOf<Habitos?>(null) }
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    val alertMessage = rememberSaveable { mutableStateOf("") }

    LaunchedEffect(Unit) {
        if (state.fechaInicio.isEmpty()) {
            val today = getTodayDate()
            viewModel.onValueChange("fechaInicio", today)
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
        HabitoDropdown(habitosList) { uidHabito, nombre, descripcion ->
            selectedHabito =
                Habitos(uidHabito, nombre, descripcion)
        }

        selectedHabito?.let { viewModel.onHabitoSeleccionado(it) }

        Space(10.dp)

        ReadOnlyTextField(
            value = selectedHabito?.descripcion ?: "No hay descripción",
            label = "Descripción:"
        )

        Space(10.dp)

        DatePickerDocked(
            value = state.fechaInicio.ifEmpty { getTodayDate() },
            label = "Fecha de inicio:",
            onValue = { date ->
                viewModel.onValueChange("fechaInicio", date)
            }
        )

        Space(20.dp)

        Row(
            modifier = Modifier.fillMaxSize(), horizontalArrangement = Arrangement.Center
        ) {
            MainButton(
                name = "Guardar",
                backColor = Color(0.129f, 0.302f, 0.986f, 1f),
                color = Color(0.984f, 0.988f, 0.988f, 1f)
            ) {
                if (state.nombre != "" && state.descripcion != "" && state.fechaInicio != "") {
                    viewModel.saveHabit(usuario.uidUsuario) { success, message ->
                        if (success) {
                            Toast.makeText(
                                context, message, Toast.LENGTH_SHORT
                            ).show()
                            viewModel.limpiar()
                            navController.popBackStack()
                            val fcmHelper = FCMHelper(context)
                            fcmHelper.sendNotification(
                                usuario.tokenFCM,
                                "¡Nuevo comienzo!",
                                "Acabas de identificar un mal hábito. Este es el primer paso para cambiar. ¡Vamos por más, tú puedes hacerlo!"
                            )
                        } else {
                            alertMessage.value = message
                            viewModel.cambiaAlert()
                        }
                    }
                } else {
                    focusManager.moveFocus(FocusDirection.Down)
                    alertMessage.value = "Todos los campos deben ser llenados."
                    viewModel.cambiaAlert()
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
    if (state.showAlert) {
        Alert(title = "¡Atención!",
            message = alertMessage.value,
            confirmText = "Aceptar",
            onConfirmClick = {
                viewModel.cancelAlert()
            }) { }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun getTodayDate(): String {
    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    return LocalDate.now().format(formatter)
}
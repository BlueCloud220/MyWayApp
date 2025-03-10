package com.example.mywayapp.views

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mywayapp.FCMHelper
import com.example.mywayapp.components.AlertOutlinedTextField
import com.example.mywayapp.components.CalendarWithHabitTracking
import com.example.mywayapp.components.MainIconButton
import com.example.mywayapp.components.RelapseButton
import com.example.mywayapp.components.Space
import com.example.mywayapp.components.TitleBar
import com.example.mywayapp.model.Usuarios
import com.example.mywayapp.viewModels.HabitosViewModel
import com.example.mywayapp.viewModels.RecaidasViewModel
import java.time.LocalDate
import java.time.format.DateTimeParseException

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateView(
    navController: NavController,
    uidHabito: String,
    usuario: Usuarios,
    viewModelHabits: HabitosViewModel,
    viewModelRelapses: RecaidasViewModel
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { TitleBar(name = "Seguimiento Hábito") },
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
        ContentUpdateView(
            paddingValues = it,
            uidHabito,
            usuario,
            viewModelHabits,
            viewModelRelapses
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ContentUpdateView(
    paddingValues: PaddingValues,
    uidHabito: String,
    usuario: Usuarios,
    viewModelHabits: HabitosViewModel,
    viewModelRelapses: RecaidasViewModel
) {
    val stateRelapses = viewModelRelapses.state.collectAsState().value

    LaunchedEffect(usuario.uidUsuario, uidHabito) {
        if (stateRelapses.fechaRecaida.isEmpty()) {
            val today = getTodayDate()
            viewModelRelapses.onValueChange("fechaRecaida", today)
        }

        viewModelHabits.loadHabit(usuario.uidUsuario, uidHabito)
        viewModelRelapses.onUsuarioHabitoCargado(usuario.uidUsuario, uidHabito)
    }

    val state = viewModelHabits.state.collectAsState().value
    val context = LocalContext.current
    val recaidasList by viewModelRelapses.recaidas.collectAsState()
    val formatter = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")

    // Convertir la fecha de inicio a LocalDate
    val startDate = if (state.fechaInicio.isNullOrBlank()) {
        LocalDate.now()
    } else {
        try {
            LocalDate.parse(state.fechaInicio, formatter)
        } catch (e: DateTimeParseException) {
            LocalDate.now()
        }
    }

    // Generar la lista de fechas cumplidas
    val completedDays = (0 until state.rachaDias + 1).map { days ->
        startDate.plusDays(days.toLong()).format(formatter)
    }

    // Convertir las fechas cumplidas a LocalDate para pasarlas a completedDays
    val completedDaysLocalDate = completedDays.mapNotNull { dateStr ->
        runCatching { LocalDate.parse(dateStr, formatter) }.getOrNull()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(10.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(2.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CalendarWithHabitTracking(
                    state.fechaInicio,
                    completedDays = completedDaysLocalDate,
                    relapseDays = recaidasList.mapNotNull { recaida ->
                        runCatching { LocalDate.parse(recaida.fechaRecaida, formatter) }.getOrNull()
                    },
                    onRelapseRecorded = {}
                )

                Space(50.dp)

                RelapseButton("He recaído", Color.Red) {
                    viewModelRelapses.cambiaAlert()
                }
            }
        }

        if (stateRelapses.showAlert) {
            AlertOutlinedTextField(
                title = "¡Atención!",
                viewModelRelapses = viewModelRelapses,
                confirmText = "Aceptar",
                onConfirmClick = {
                    if (stateRelapses.fechaRecaida != "" && stateRelapses.motivo != "") {
                        viewModelRelapses.saveRecaida(
                            usuario.uidUsuario,
                            uidHabito
                        ) { success, message ->
                            if (success) {
                                viewModelRelapses.cancelAlert()
                                viewModelRelapses.limpiar()
                                Toast.makeText(
                                    context, message, Toast.LENGTH_SHORT
                                ).show()
                                val fcmHelper = FCMHelper(context)
                                fcmHelper.sendNotification(
                                    usuario.tokenFCM,
                                    "¡No te rindas!",
                                    "Las recaídas son parte del proceso. Levántate y sigue adelante. ¡Tú eres más fuerte que esto!"
                                )
                            } else {
                                Toast.makeText(
                                    context, message, Toast.LENGTH_SHORT
                                ).show()
                                viewModelRelapses.cancelAlert()
                                viewModelRelapses.onValueChange("motivo", "")
                            }
                        }
                    } else {
                        Toast.makeText(
                            context, "Debes especificar el motivo de tu recaída para poder continuar", Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            ) {}
        }
    }
}
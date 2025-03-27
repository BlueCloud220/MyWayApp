package com.example.mywayapp.views

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import java.time.format.DateTimeFormatter

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
    val stateRelapses by viewModelRelapses.state.collectAsState()
    val state by viewModelHabits.state.collectAsState()
    val recaidasList by viewModelRelapses.recaidas.collectAsState()
    val context = LocalContext.current
    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    LaunchedEffect(usuario.uidUsuario, uidHabito) {
        if (stateRelapses.fechaRecaida.isEmpty()) {
            viewModelRelapses.onValueChange("fechaRecaida", LocalDate.now().format(formatter))
        }
        viewModelHabits.loadHabit(usuario.uidUsuario, uidHabito)
        viewModelRelapses.onUsuarioHabitoCargado(usuario.uidUsuario, uidHabito)
    }

    val startDate = state.fechaInicio?.let {
        runCatching { LocalDate.parse(it, formatter) }.getOrNull() ?: LocalDate.now()
    } ?: LocalDate.now()

    val completedDays = (0..state.rachaDias).map { days ->
        startDate.plusDays(days.toLong()).format(formatter)
    }

    val completedDaysLocalDate = completedDays.mapNotNull {
        runCatching { LocalDate.parse(it, formatter) }.getOrNull()
    }

    val relapseDays = recaidasList.mapNotNull {
        runCatching { LocalDate.parse(it.fechaRecaida, formatter) }.getOrNull()
    }

    val sharedPref = context.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
    val key = "diasTranscurridos_${state.uidHabito}"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CalendarWithHabitTracking(
            state.fechaInicio,
            completedDays = completedDaysLocalDate,
            relapseDays = relapseDays,
            onRelapseRecorded = {},
        )

        Legend()

        Space(20.dp)

        RelapseButton("He recaído", Color.Red) {
            viewModelRelapses.cambiaAlert()
        }
    }

    if (stateRelapses.showAlert) {
        AlertOutlinedTextField(
            title = "¡Atención!",
            viewModelRelapses = viewModelRelapses,
            confirmText = "Aceptar",
            dismissText = "Cancelar",
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
                            FCMHelper(context).sendNotification(
                                usuario.tokenFCM,
                                "¡No te rindas!",
                                "Las recaídas son parte del proceso. ¡Levántate y sigue adelante!"
                            )
                            viewModelHabits.updateStreakHabit(
                                rachaDias = (state.rachaDias-1),
                                usuario.uidUsuario
                            ) { success, message ->
                            }
                            val editor: SharedPreferences.Editor = sharedPref.edit()
                            editor.putInt(key, state.rachaDias-1)
                            editor.apply()
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
                        context,
                        "Debes especificar el motivo de tu recaída para poder continuar",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            },
            onDismissClick = {
                viewModelRelapses.cancelAlert()
                viewModelRelapses.onValueChange("motivo", "")
            }
        )
    }
}

@Composable
fun Legend() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            LegendItem("Inicio del hábito", Color(0f, 0.129f, 0.302f, 1f))
            LegendItem("Recaída", Color(1f, 0.329f, 0.439f, 1f))
        }

        Spacer(modifier = Modifier.height(8.dp))

        LegendItem("Día completado", Color(0f, 0.922f, 0.780f, 1f))
    }
}

@Composable
fun LegendItem(label: String, color: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(16.dp)
                .background(color, shape = MaterialTheme.shapes.small)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = label, style = MaterialTheme.typography.bodyMedium)
    }
}
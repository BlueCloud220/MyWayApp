package com.example.mywayapp.views

import android.annotation.SuppressLint
import android.widget.Toast
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mywayapp.components.Alert
import com.example.mywayapp.components.DatePickerDocked
import com.example.mywayapp.components.MainButton
import com.example.mywayapp.components.MainIconButton
import com.example.mywayapp.components.Space
import com.example.mywayapp.components.SpaceW
import com.example.mywayapp.components.TitleBar
import com.example.mywayapp.viewModels.HabitosViewModel

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { TitleBar(name = "Agregar Hábito") },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                ),
                navigationIcon = {
                    MainIconButton(icon = Icons.AutoMirrored.Filled.ArrowBack) {
                        navController.popBackStack()
                    }
                })
        },
    ) {
    }
}

@Composable
fun ContentAddView(
) {
    val state = viewModel.state.collectAsState().value
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .padding(paddingValues)
            .padding(10.dp)
            .fillMaxSize()
    ) {

        Space(10.dp)

        )

        Space(10.dp)

        DatePickerDocked(
            label = "Fecha de inicio:",
        )

        Space(20.dp)

        Row(
            modifier = Modifier.fillMaxSize(), horizontalArrangement = Arrangement.Center
        ) {
                if (state.nombre != "" && state.descripcion != "" && state.fechaInicio != "") {
                        if (success) {
                            Toast.makeText(
                                context, message, Toast.LENGTH_SHORT
                            ).show()
                            viewModel.limpiar()
                            navController.popBackStack()
                        } else {
                        }
                    }
                } else {
                    focusManager.moveFocus(FocusDirection.Down)
                    viewModel.cambiaAlert()
                }
            }
            SpaceW()
                navController.popBackStack()
            }
        }
    }
    if (state.showAlert) {
        Alert(title = "¡Atención!",
            confirmText = "Aceptar",
            onConfirmClick = {
                viewModel.cancelAlert()
            }) { }
    }
}
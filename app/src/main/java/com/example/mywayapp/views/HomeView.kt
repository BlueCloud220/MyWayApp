package com.example.mywayapp.views

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.mywayapp.components.ActionButton
import com.example.mywayapp.components.Alert
import com.example.mywayapp.components.ProfileIconButton
import com.example.mywayapp.components.TitleBar
import com.example.mywayapp.model.Habitos
import com.example.mywayapp.model.Usuarios
import com.example.mywayapp.ui.theme.Purple40
import com.example.mywayapp.viewModels.HabitosViewModel

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeView(navController: NavController, viewModel: HabitosViewModel, usuario: Usuarios) {
    Scaffold(topBar = {
        CenterAlignedTopAppBar(
            title = { TitleBar(name = "MyWayApp") },
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = Color(0xFF1976D2)
            ),
            actions = {
                ProfileIconButton(icon = Icons.Filled.AccountCircle) {
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
        ContentHomeView(paddingValues = it, navController, viewModel)
    }
}

@Composable
fun ContentHomeView(
    paddingValues: PaddingValues, navController: NavController, viewModel: HabitosViewModel
) {
    val habitosList = viewModel.habitos.collectAsState(initial = emptyList())

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        if (habitosList.value.isEmpty()) {
            item {
                Text(
                    modifier = Modifier
                        .padding(top = 300.dp)
                        .fillMaxSize(),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    text = "No hay hábitos para mostrar"
                )
            }
        } else {
            items(habitosList.value) { habito ->
                HabitoItem(navController, viewModel, habito)
            }
        }
    }
}

@Composable
fun HabitoItem(navController: NavController, viewModel: HabitosViewModel, habito: Habitos) {
    val state = viewModel.state.collectAsState().value
    val context = LocalContext.current

    ListItem(leadingContent = {
        IconButton(onClick = {
            viewModel.onValueChange("uidHabito", habito.uidHabito)
            viewModel.onValueChange("nombre", habito.nombre)
            viewModel.onValueChange("descripcion", habito.descripcion)
            viewModel.onValueChange("rachaDias", habito.rachaDias.toString())
            viewModel.onValueChange("fechaInicio", habito.fechaInicio)
            viewModel.onValueChange("fechaFin", habito.fechaFin)
            if (!state.estado) {
                viewModel.onValueChange("estado", "true")
                viewModel.updateHabito { success, message ->
                    Toast.makeText(
                        context,
                        "¡Felicidades! has completado este hábito.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                viewModel.onValueChange("estado", "false")
                viewModel.updateHabito { success, message ->
                    Toast.makeText(
                        context,
                        "Desmarcaste como 'completado' este hábito.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }) {
            Icon(
                imageVector = if (habito.estado == true) Icons.Default.CheckCircle else Icons.Default.Close,
                contentDescription = "Hábito",
                modifier = Modifier.size(50.dp),
                tint = Color(0xFF64B5F6)
            )
        }
    }, headlineContent = {
        Text(
            text = habito.nombre, fontSize = 15.sp, fontWeight = FontWeight.Bold
        )
    }, supportingContent = {
        Text(
            text = "Racha de días: " + if (habito.rachaDias == 0) "Iniciado" else habito.rachaDias.toString(),
            fontSize = 12.sp
        )
    }, trailingContent = {
        Row {
            IconButton(onClick = {
                viewModel.cambiaAlert()
            }) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Eliminar",
                    modifier = Modifier.size(30.dp),
                    tint = Color(219, 15, 0, 255)
                )
            }
            if (state.showAlert) {
                Alert(title = "¡Atención!",
                    message = "¿Está seguro que desea eliminar este hábito?",
                    confirmText = "Aceptar",
                    dismissText = "Cancelar",
                    onConfirmClick = {
                        viewModel.deleteHabito(habito.uidHabito) { success, message ->
                            if (success) {
                                Toast.makeText(
                                    context,
                                    message,
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                Toast.makeText(context, "Error: $message", Toast.LENGTH_SHORT)
                                    .show()
                            }
                            viewModel.cancelAlert()
                        }
                    },
                    onDismissClick = { viewModel.cancelAlert() })
            }
            IconButton(onClick = { navController.navigate("Update/${habito.uidHabito}") }) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Editar",
                    modifier = Modifier.size(30.dp),
                    tint = Color(0xFF64B5F6)
                )
            }
        }

    }, modifier = Modifier.fillMaxSize()
    )
}




package com.example.mywayapp.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mywayapp.ui.theme.Purple80
import com.example.mywayapp.viewModels.HabitosViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@Composable
fun Space(size: Dp) {
    Spacer(modifier = Modifier.height(size))
}

@Composable
fun SpaceW(size: Dp = 35.dp) {
    Spacer(modifier = Modifier.width(size))
}

@Composable
fun MainTextField(
    value: String,
    onValue: (String) -> Unit,
    label: String,
    keyboardType: KeyboardType,
    focusRequester: FocusRequester,
    maxLength: Int
) {
    OutlinedTextField(
        value = value,
        onValueChange = {
            if (it.length <= maxLength) {
                onValue(it)
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 15.dp)
            .focusRequester(focusRequester),
        label = {
            Row {
                Text(text = "*", color = Color.Red)
                Spacer(Modifier.width(2.dp))
                Text(text = label)
            }
        },
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        singleLine = true,
        maxLines = 1,
    )
}

@Composable
fun MainButton(name: String, backColor: Color, color: Color, onClick: () -> Unit) {
    Button(
        modifier = Modifier.width(135.dp), onClick = onClick, colors = ButtonDefaults.buttonColors(
            contentColor = color, containerColor = backColor
        )
    ) {
        Text(text = name, fontSize = 15.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun Alert(
    title: String,
    message: String,
    confirmText: String,
    dismissText: String? = null,
    onConfirmClick: () -> Unit,
    onDismissClick: (() -> Unit)? = null
) {
    AlertDialog(
        onDismissRequest = { onDismissClick?.invoke() ?: onConfirmClick() },
        confirmButton = {
            dismissText?.let {
                Button(
                    onClick = { onDismissClick?.invoke() },
                    colors = ButtonColors(
                        Color(1f, 0.329f, 0.439f, 1f),
                        Color(0.984f, 0.988f, 0.988f, 1f),
                        Purple80,
                        Color(0.984f, 0.988f, 0.988f, 1f)
                    )
                ) {
                    Text(text = it)
                }
            }
        },
        dismissButton = {
            Button(
                onClick = { onConfirmClick() },
                colors = ButtonColors(
                    Color(0.129f, 0.302f, 0.986f, 1f),
                    Color(0.984f, 0.988f, 0.988f, 1f),
                    Purple80,
                    Color(0.984f, 0.988f, 0.988f, 1f)
                )
            ) {
                Text(text = confirmText)
            }
        },
        title = { Text(text = title) },
        text = { Text(text = message) },
        containerColor = Color.White
    )
}

@Composable
fun DatePickerDocked(value: String, onValue: (String) -> Unit, label: String) {
    var showModal by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = value,
        onValueChange = {},
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 15.dp),
        readOnly = true,
        label = {
            Row {
                Text(text = "*", color = Color.Red)
                Spacer(Modifier.width(2.dp))
                Text(text = label)
            }
        },
        trailingIcon = {
            IconButton(onClick = { showModal = true }) {
                Icon(
                    imageVector = Icons.Default.DateRange, contentDescription = "Seleccionar fecha"
                )
            }
        },
    )

    if (showModal) {
        DatePickerModal(onDateSelected = { selectedDate ->
            val formattedDate = selectedDate?.let(::formatDate) ?: ""
            onValue(formattedDate)
        }, onDismiss = { showModal = false })
    }
}

fun formatDate(millis: Long): String {
    return SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }.format(Date(millis))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerModal(
    onDateSelected: (Long?) -> Unit, onDismiss: () -> Unit
) {
    val calendar = Calendar.getInstance()
    val startOfYearMillis: Long = calendar.timeInMillis

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = startOfYearMillis,
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis <= startOfYearMillis
            }
        })

    DatePickerDialog(onDismissRequest = onDismiss, confirmButton = {
        TextButton(onClick = {
            onDateSelected(datePickerState.selectedDateMillis)
            onDismiss()
        }) {
            Text("Aceptar")
        }
    }, dismissButton = {
        TextButton(onClick = onDismiss) {
            Text("Cancelar")
        }
    }) {
        DatePicker(state = datePickerState)
    }
}

@Composable
fun HabitoDropdown(
    viewModel: HabitosViewModel,
    onHabitoSelected: (nombre: String, descripcion: String, uidHabito: String) -> Unit
) {
    val habitosList = viewModel.habitos.collectAsState(initial = emptyList())
    var expanded by remember { mutableStateOf(false) }
    var selectedHabito by rememberSaveable { mutableStateOf("") } // Ahora se guarda la selección

    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = selectedHabito,
            onValueChange = {},
            label = { Text("Seleccionar Hábito") },
            readOnly = true,
            trailingIcon = {
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = "Expandir")
                }
            },
            modifier = Modifier
                .fillMaxWidth() // Asegura que ocupe todo el ancho disponible
                .padding(horizontal = 15.dp)
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth() // Asegura que el DropdownMenu tenga el mismo ancho
        ) {
            habitosList.value.forEach { habito ->
                DropdownMenuItem(
                    text = { Text(habito.nombre) },
                    onClick = {
                        selectedHabito = habito.nombre
                        expanded = false
                        onHabitoSelected(habito.nombre, habito.descripcion, habito.uidHabito)
                    }
                )
            }
        }
    }
}

@Composable
fun ReadOnlyTextField(
    value: String,
    label: String
) {
    OutlinedTextField(
        value = value,
        onValueChange = {}, // No permite cambios
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 15.dp)
            .height(120.dp), // Más alto para evitar scroll interno
        label = { Text(label) },
        readOnly = true, // Bloquea la edición
        maxLines = 5, // Permite ver más texto sin cortar
        textStyle = TextStyle(color = Color.Gray) // Diferenciar del resto
    )
}

package com.example.mywayapp.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mywayapp.model.Habitos
import com.example.mywayapp.model.Recaidas
import com.example.mywayapp.ui.theme.Purple80
import com.example.mywayapp.viewModels.RecaidasViewModel
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
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
        title = { Text(text = title, color = Color.Red, fontWeight = FontWeight.Bold) },
        text = { Text(text = message) },
        containerColor = Color.White
    )
}

@Composable
fun AlertOutlinedTextField(
    title: String,
    viewModelRelapses: RecaidasViewModel,
    confirmText: String,
    dismissText: String? = null,
    onConfirmClick: () -> Unit,
    onDismissClick: (() -> Unit)? = null
) {
    val state = viewModelRelapses.state.collectAsState().value

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
        title = { Text(text = title, color = Color.Red, fontWeight = FontWeight.Bold) },
        text = {
            Column {
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = state.motivo,
                    onValueChange = { viewModelRelapses.onValueChange("motivo", it) },
                    label = { Text("Motivo de su recaída:") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        },
        containerColor = Color.White
    )
}

@RequiresApi(Build.VERSION_CODES.O)
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

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerModal(
    onDateSelected: (Long?) -> Unit, onDismiss: () -> Unit
) {
    val startOfDayMillis = LocalDate.now()
        .atStartOfDay(ZoneOffset.UTC)
        .toInstant()
        .toEpochMilli()

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = startOfDayMillis,
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis >= startOfDayMillis
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
    habitosList: List<Habitos>,
    onHabitoSelected: (uidHabito: String, nombre: String, descripcion: String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedHabito by rememberSaveable { mutableStateOf("") }

    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = selectedHabito,
            onValueChange = {},
            label = {
                Row {
                    Text(text = "*", color = Color.Red)
                    Spacer(Modifier.width(2.dp))
                    Text(text = "Seleccionar Hábito")
                }
            },
            readOnly = true,
            trailingIcon = {
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = "Expandir")
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 15.dp)
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth()
        ) {
            habitosList.forEach { habito ->
                DropdownMenuItem(
                    text = { Text(habito.nombre) },
                    onClick = {
                        selectedHabito = habito.nombre
                        expanded = false
                        onHabitoSelected(habito.uidHabito, habito.nombre, habito.descripcion)
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
        onValueChange = {},
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 15.dp),
        label = {
            Row {
                Text(text = "*", color = Color.Red)
                Spacer(Modifier.width(2.dp))
                Text(label)
            }
        },
        readOnly = true,
        textStyle = TextStyle(color = Color.Gray)
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalendarWithHabitTracking(
    startHabit: String,
    completedDays: List<LocalDate>,
    relapseDays: List<LocalDate>,
    onRelapseRecorded: (LocalDate) -> Unit
) {
    val currentMonth = YearMonth.now()

    val startDate = if (startHabit.isNullOrBlank()) {
        LocalDate.now()
    } else {
        try {
            val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
            LocalDate.parse(startHabit, formatter)
        } catch (e: DateTimeParseException) {
            LocalDate.now()
        }
    }

    // Formateador para obtener el mes en español
    val monthFormatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale("es", "ES"))
    val formattedMonth = currentMonth.format(monthFormatter).toUpperCase()

    // Determinar el primer y último día del mes
    val firstDayOfMonth = currentMonth.atDay(1)
    val lastDayOfMonth = currentMonth.atEndOfMonth()

    // Obtener el día de la semana en que empieza el mes (lunes = 1, domingo = 7)
    val startDayOfWeek =
        firstDayOfMonth.dayOfWeek.value % 7  // Modificar para que domingo sea 6, lunes 0, etc.

    // Calcular los días necesarios del mes anterior y posterior
    val previousMonthDays =
        (startDayOfWeek + 6) % 7  // Días del mes anterior para llenar hasta el inicio del mes
    val nextMonthDays =
        (42 - (previousMonthDays + lastDayOfMonth.dayOfMonth)) % 7  // Días del mes siguiente para completar 42

    // Crear una lista con todos los días de la cuadrícula, incluyendo los días del mes anterior y posterior
    val totalDays = mutableListOf<LocalDate>()

    // Agregar días del mes anterior
    val previousMonth = currentMonth.minusMonths(1)
    for (i in 1..previousMonthDays) {
        totalDays.add(previousMonth.atEndOfMonth().minusDays(i.toLong()))
    }

    // Agregar días del mes actual
    for (i in 1..lastDayOfMonth.dayOfMonth) {
        totalDays.add(currentMonth.atDay(i))
    }

    // Agregar días del mes siguiente
    val nextMonth = currentMonth.plusMonths(1)
    for (i in 1..nextMonthDays) {
        totalDays.add(nextMonth.atDay(i))
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(1.dp)
            .shadow(4.dp, shape = MaterialTheme.shapes.medium),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(16.dp)
        ) {
            // Título del mes actual
            Text(
                text = formattedMonth,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Días de la semana
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(start = 13.dp, bottom = 8.dp)
            ) {
                val dayNames = listOf("Lun", "Mar", "Mié", "Jue", "Vie", "Sáb", "Dom")
                dayNames.forEach { dayName ->
                    Text(
                        text = dayName.toUpperCase(),
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.weight(0.8f),
                        color = Color.Gray
                    )
                }
            }

            // Calendario de días del mes
            LazyVerticalGrid(
                columns = GridCells.Fixed(7),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 600.dp)
                    .padding(2.dp)
            ) {
                items(totalDays.size) { index ->
                    val date = totalDays[index]
                    val isStartHabit = date == startDate
                    val isCompleted = completedDays.contains(date)
                    val isRelapse = relapseDays.contains(date)

                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .padding(4.dp)
                            .clip(CircleShape)
                            .background(
                                when {
                                    isStartHabit -> Color(0f, 0.129f, 0.302f, 1f)
                                    isRelapse -> Color(0xFFF44336) // Rojo
                                    isCompleted -> Color(0xFF4CAF50) // Verde
                                    else -> Color.LightGray
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = date.dayOfMonth.toString(),
                            color = Color.White,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RelapseButton(name: String, backColor: Color, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = backColor),
        shape = CircleShape,
        modifier = Modifier
            .size(200.dp)
            .shadow(8.dp, shape = CircleShape)
    ) {
        Text(
            text = name,
            color = Color.White,
            style = TextStyle(
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        )
    }
}

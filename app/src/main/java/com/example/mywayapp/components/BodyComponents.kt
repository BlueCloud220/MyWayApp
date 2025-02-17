package com.example.mywayapp.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mywayapp.ui.theme.Purple40
import com.example.mywayapp.ui.theme.Purple80
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
                    colors = ButtonColors(Color(219, 15, 0, 255), Color.White, Purple80, Color.White)
                ) {
                    Text(text = it)
                }
            }
        },
        dismissButton = {
            Button(
                onClick = { onConfirmClick() },
                colors = ButtonColors(Color(0, 118, 4, 255), Color.White, Purple80, Color.White)
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
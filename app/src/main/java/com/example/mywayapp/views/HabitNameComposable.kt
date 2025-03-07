package com.example.mywayapp.views

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mywayapp.components.Space
import com.example.mywayapp.model.Habitos
import com.example.mywayapp.viewModels.UsuariosViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun UsuarioHabitoItem(
    viewModel: UsuariosViewModel,
    usuarioHabito: Habitos,
    navController: NavController
) {
    val habitName =
        usuarioHabito.nombre // Llama al composable que obtiene el nombre real del hábito a partir del uid
    val (progreso, progresoMaximo) = calcularProgreso(
        usuarioHabito.fechaInicio
    ) // Calculamos el progreso y el máximo
    val progresoPorcentaje =
        (progreso.toFloat() / progresoMaximo.toFloat()) // Progreso como porcentaje
    val motivacion = when {
        progresoPorcentaje < 0.2 -> "¡Vamos! Empieza el hábito."
        progresoPorcentaje < 0.5 -> "¡Sigues avanzando!"
        progresoPorcentaje < 0.8 -> "¡Casi lo logras!"
        else -> "¡Excelente, lo has hecho muy bien!"
    }
    val barraColor = when {
        progresoPorcentaje < 0.1 -> Color(1f, 0.329f, 0.439f, 1f)
        progresoPorcentaje < 0.5 -> Color(0.992f, 0.886f, 0.310f, 1f)
        progresoPorcentaje < 0.8 -> Color(0.129f, 0.302f, 0.986f, 1f)
        else -> Color(0f, 0.922f, 0.780f, 1f)
    }
    val fechaMeta = calcularFechaMeta(usuarioHabito.fechaInicio, progresoMaximo)

    val context = LocalContext.current
    val state = viewModel.state.collectAsState().value
    val tokenFCM = state.tokenFCM

    // Obtener SharedPreferences
    val sharedPref = context.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)

    // Guardar el valor actual de diasTranscurridos para futuras comparaciones
    val editor: SharedPreferences.Editor = sharedPref.edit()
    editor.putInt("diasTranscurridos", progreso)
    editor.apply() // Guarda el valor de forma asíncrona

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
            .shadow(4.dp, shape = MaterialTheme.shapes.medium)
            .clickable { // Hace que toda la card sea interactiva
                navController.navigate("Update/${usuarioHabito.uidHabito}")
            },
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Hábito: $habitName",
                modifier = Modifier.fillMaxWidth(),
                color = Color(0.129f, 0.302f, 0.986f, 1f)
            )
            Space(8.dp)

            Text(
                text = "Fecha de inicio: ${usuarioHabito.fechaInicio}",
                modifier = Modifier.fillMaxWidth()
            )
            Space(8.dp)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Meta para alcanzar: $fechaMeta",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
                Text(text = " | ", style = MaterialTheme.typography.bodySmall, color = Color.Red)
                Text(
                    text = motivacion,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
            Space(12.dp)

            LinearProgressIndicator(
                progress = progresoPorcentaje,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = barraColor,
                trackColor = Color.Gray.copy(alpha = 0.3f)
            )
            Space(8.dp)

            Text(
                text = "Racha: $progreso de $progresoMaximo día(s)",
                modifier = Modifier.fillMaxWidth(),
                color = Color.Black
            )
        }
    }
}


@Composable
@RequiresApi(Build.VERSION_CODES.O)
fun calcularProgreso(fechaInicio: String): Pair<Int, Int> {
    return try {
        val fechaInicioParsed =
            LocalDate.parse(fechaInicio, DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        val fechaActual = LocalDate.now()
        val diasTranscurridos = ChronoUnit.DAYS.between(fechaInicioParsed, fechaActual).toInt()

        val progresoMaximo = when { // Determinar el rango de progreso
            diasTranscurridos < 1 -> 1 // 1 día
            diasTranscurridos in 1..3 -> 3 // 3 días
            diasTranscurridos in 4..7 -> 7 // 1 semana
            diasTranscurridos in 8..30 -> 30 // 1 mes
            diasTranscurridos in 31..180 -> 180 // 6 meses
            diasTranscurridos in 181..365 -> 365 // 1 año
            else -> 365 // Límite de 1 año
        }

        Pair(diasTranscurridos, progresoMaximo)
    } catch (e: Exception) {
        Pair(0, 1) // En caso de error, regresamos 0 y el mínimo de progreso como 1 día.
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun calcularFechaMeta(fechaInicio: String, progresoMaximo: Int): String {
    return try {
        // Convertimos la fecha de inicio a LocalDate
        val fechaInicioParsed =
            LocalDate.parse(fechaInicio, DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        // Sumamos el número máximo de días al progreso
        val fechaMeta = fechaInicioParsed.plusDays(progresoMaximo.toLong())
        // Retornamos la fecha de meta en formato dd/MM/yyyy
        fechaMeta.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
    } catch (e: Exception) {
        // En caso de error, retornamos "Error"
        "Error"
    }
}
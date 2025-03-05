package com.example.mywayapp

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.mywayapp.navigation.NavManager
import com.example.mywayapp.ui.theme.MyWayAppTheme
import com.example.mywayapp.viewModels.HabitosViewModel
import com.example.mywayapp.viewModels.UsuariosViewModel
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModelHabitos: HabitosViewModel by viewModels()
        val viewModelUsuarios: UsuariosViewModel by viewModels()

        viewModelUsuarios.usuario.observe(this) { usuario ->
            setContent {
                MyWayAppTheme {
                    NavManager(viewModelHabitos, viewModelUsuarios, usuario)
                }
            }

            askNotificationPermission(viewModelUsuarios)
        }
    }

    // Función para solicitar permisos de notificación
    fun askNotificationPermission(viewModelUsuarios: UsuariosViewModel) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // Si el permiso no ha sido concedido, lo solicitamos
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                    REQUEST_CODE_PERMISSION
                )
            } else {
                // Si el permiso ya fue concedido, obtenemos el token de FCM
                getFCMToken(viewModelUsuarios)
            }
        } else {
            // Si la versión es anterior a TIRAMISU, no se necesita solicitar el permiso
            getFCMToken(viewModelUsuarios)
        }
    }

    // Función para obtener el token de FCM
    private fun getFCMToken(viewModelUsuarios: UsuariosViewModel) {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Toast.makeText(this, "Failed to get FCM token", Toast.LENGTH_SHORT).show()
                return@addOnCompleteListener
            }
            val token = task.result

            viewModelUsuarios.updateTokenFCM(token) { success, message ->
                if (success) {
                    println("Token guardado en la base de datos")
                } else {
                    println("Error al guardar el token: $message")
                }
            }
        }
    }

    // Código para manejar los permisos (en caso de que el usuario lo deniegue o lo conceda)
    companion object {
        private const val REQUEST_CODE_PERMISSION = 100
    }
}
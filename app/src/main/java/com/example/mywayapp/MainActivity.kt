package com.example.mywayapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.example.mywayapp.navigation.NavManager
import com.example.mywayapp.ui.theme.MyWayAppTheme
import com.example.mywayapp.viewModels.HabitosViewModel
import com.example.mywayapp.viewModels.UsuariosViewModel

class MainActivity : ComponentActivity() {
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
        }
    }
}
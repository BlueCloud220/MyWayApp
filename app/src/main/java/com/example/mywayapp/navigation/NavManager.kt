package com.example.mywayapp.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.mywayapp.model.Usuarios
import com.example.mywayapp.viewModels.HabitosViewModel
import com.example.mywayapp.viewModels.RecaidasViewModel
import com.example.mywayapp.viewModels.UsuariosViewModel
import com.example.mywayapp.views.AddView
import com.example.mywayapp.views.HomeView
import com.example.mywayapp.views.LoginView
import com.example.mywayapp.views.ProfileView
import com.example.mywayapp.views.RegisterView
import com.example.mywayapp.views.UpdateView

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NavManager(
    viewModelUsuarios: UsuariosViewModel,
    viewModelHabitos: HabitosViewModel,
    viewModelRecaidas: RecaidasViewModel,
    usuario: Usuarios
) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "Login") {

        composable("Login") {
            LoginView(navController, viewModelUsuarios)
        }

        composable("Register") {
            RegisterView(navController, viewModelUsuarios)
        }

        composable("Home") {
            HomeView(navController, viewModelHabitos, viewModelUsuarios, usuario)
        }

        composable("Add") {
            AddView(navController, viewModelHabitos, usuario)
        }

        composable(
            route = "Update/{uidHabito}",
            arguments = listOf(navArgument("uidHabito") { type = NavType.StringType })
        ) { backStackEntry ->
            val uidHabito = backStackEntry.arguments?.getString("uidHabito") ?: ""
            UpdateView(navController, uidHabito, usuario, viewModelHabitos, viewModelRecaidas)
        }

        composable(
            route = "UpdateProfile/{uidUsuario}",
            arguments = listOf(navArgument("uidUsuario") { type = NavType.StringType })
        ) {
            ProfileView(navController, viewModelUsuarios)
        }

    }
}
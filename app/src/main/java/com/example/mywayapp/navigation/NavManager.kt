package com.example.mywayapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.mywayapp.model.Usuarios
import com.example.mywayapp.viewModels.HabitosViewModel
import com.example.mywayapp.viewModels.UsuariosViewModel
import com.example.mywayapp.views.AddView
import com.example.mywayapp.views.HomeView
import com.example.mywayapp.views.LoginView
import com.example.mywayapp.views.ProfileView
import com.example.mywayapp.views.RegisterView
import com.example.mywayapp.views.UpdateView

@Composable
fun NavManager(
    viewModelHabitos: HabitosViewModel,
    viewModelUsuarios: UsuariosViewModel,
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
            HomeView(navController, viewModelHabitos, usuario)
        }

        composable("Add") {
            AddView(navController, viewModelHabitos)
        }

        composable(
            route = "Update/{uidHabito}",
            arguments = listOf(navArgument("uidHabito") { type = NavType.StringType })
        ) { backStackEntry ->
            val uidHabito = backStackEntry.arguments?.getString("uidHabito") ?: ""
            UpdateView(navController, uidHabito, viewModelHabitos)
        }

        composable(
            route = "UpdateProfile/{uidUsuario}",
            arguments = listOf(navArgument("uidUsuario") { type = NavType.StringType })
        ) { backStackEntry ->
            val uidUsuario = backStackEntry.arguments?.getString("uidUsuario") ?: ""
            ProfileView(navController, uidUsuario, viewModelUsuarios)
        }

    }
}
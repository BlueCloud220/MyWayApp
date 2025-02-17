package com.example.mywayapp.model

data class Usuarios(
    val uidUsuario: String = "",
    val nombre: String = "",
    val apellido: String = "",
    val iconoPerfil: String = "",
    val nombreUsuario: String = "",
    val contrasena: String = "",
    val tokenFCM: String = "",
    val showAlert: Boolean = false
)

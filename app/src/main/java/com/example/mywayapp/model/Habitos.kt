package com.example.mywayapp.model

data class Habitos(
    val uidHabito: String = "",
    val nombre: String = "",
    val descripcion: String = "",
    val rachaDias: Int = 0,
    val estado: Boolean = false,
    val fechaInicio: String = "",
    val fechaFin: String = "",
    val showAlert: Boolean = false
)

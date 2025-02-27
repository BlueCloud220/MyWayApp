package com.example.mywayapp.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mywayapp.data.repository.UsuarioHabitosRepository
import com.example.mywayapp.model.Habitos
import com.example.mywayapp.model.UsuarioHabitos
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UsuarioHabitosViewModel : ViewModel() {

    private val repository = UsuarioHabitosRepository()

    private val _state = MutableStateFlow(
        UsuarioHabitos(
            uidPrincipal = "",
            uidUsuario = "",
            uidHabito = "",
            fechaInicio = "",
            estado = false
        )
    )
    val state: StateFlow<UsuarioHabitos> = _state

    val usuarioHabitosList: StateFlow<List<UsuarioHabitos>> = repository.usuarioHabitosList

    fun fetchUsuarioHabitos(uidUsuario: String) {
        repository.fetchUsuarioHabitos(uidUsuario)
    }

    // Función para guardar el hábito
    fun saveUsuarioHabito(onComplete: (Boolean, String) -> Unit) {
        val usuarioHabito = _state.value

        // Validar que los campos requeridos no estén vacíos
        if (usuarioHabito.uidUsuario.isBlank() || usuarioHabito.uidHabito.isBlank() || usuarioHabito.fechaInicio.isBlank()) {
            onComplete(false, "Todos los campos son obligatorios")
            return
        }

        viewModelScope.launch {
            repository.saveUsuarioHabito(usuarioHabito, onComplete)
        }
    }


    // Función para eliminar el hábito
    fun deleteUsuarioHabito(uidUsuario: String, onComplete: (Boolean, String) -> Unit) {
        repository.deleteUsuarioHabito(uidUsuario, onComplete)
    }

    // Función para actualizar el hábito
    fun updateUsuarioHabito(onComplete: (Boolean, String) -> Unit) {
        val usuarioHabito = _state.value
        repository.updateUsuarioHabito(usuarioHabito, onComplete)
    }

    // Función que maneja el cambio de valor en los campos (similar a onValueChange en otros ViewModels)
    fun onValueChange(field: String, value: String) {
        _state.value = when (field) {
            "uidUsuario" -> _state.value.copy(uidUsuario = value)
            "uidHabito" -> _state.value.copy(uidHabito = value)
            "fechaInicio" -> _state.value.copy(fechaInicio = value)
            "estado" -> _state.value.copy(estado = value.toBooleanStrictOrNull() ?: false)
            else -> _state.value
        }
    }

    // Funciones para manejar el estado del alert (opcional)
    fun cambiaAlert() {
        _state.value = _state.value.copy(estado = true)
    }

    fun cancelAlert() {
        _state.value = _state.value.copy(estado = false)
    }

    // Función para limpiar el estado
    fun limpiar() {
        _state.value = UsuarioHabitos()
    }

    // Estado para almacenar el hábito seleccionado
    private val _selectedHabito = MutableStateFlow<Habitos?>(null)
    val selectedHabito: StateFlow<Habitos?> = _selectedHabito

    // Función para actualizar el hábito seleccionado
    fun onHabitoSeleccionado(habito: Habitos) {
        _selectedHabito.value = habito
        _state.value = _state.value.copy(uidHabito = habito.uidHabito) // Asignar el ID del hábito
    }

}

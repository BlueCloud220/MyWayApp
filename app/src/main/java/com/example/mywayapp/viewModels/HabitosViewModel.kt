package com.example.mywayapp.viewModels

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mywayapp.data.repository.HabitosRepository
import com.example.mywayapp.model.Habitos
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HabitosViewModel : ViewModel() {

    private val repository = HabitosRepository()

    private val _uidUsuario = mutableStateOf("")
    val uidUsuario: State<String> = _uidUsuario

    fun onUsuarioCargado(uidUsuario: String) {
        _uidUsuario.value = uidUsuario
        if (uidUsuario.isNotEmpty()) {
            fetchHabitsUser(uidUsuario)
            fetchHabits()
        }
    }

    val habitos = repository.habitos
    private fun fetchHabits() {
        viewModelScope.launch {
            repository.fetchHabits()
        }
    }

    val habitosUsuario = repository.habitosUsuario
    private fun fetchHabitsUser(uidUsuario: String) {
        viewModelScope.launch {
            repository.fetchHabitsUser(uidUsuario)
        }
    }

    private val _state = MutableStateFlow(
        Habitos(
            uidHabito = "",
            nombre = "",
            descripcion = "",
            rachaDias = 0,
            estado = false,
            fechaInicio = "",
            fechaFin = ""
        )
    )

    val state: StateFlow<Habitos> = _state

    private val _selectedHabito = mutableStateOf<Habitos?>(null)
    val selectedHabito: State<Habitos?> = _selectedHabito

    fun onHabitoSeleccionado(habito: Habitos) {
        _state.value = _state.value.copy(
            nombre = habito.nombre,
            descripcion = habito.descripcion
        )
    }

    fun loadHabit(uidUsuario: String, uidHabito: String) {
        viewModelScope.launch {
            repository.fetchHabitById(uidUsuario, uidHabito)
            repository.habito.collect { habito ->
                _state.value = habito
            }
        }
    }

    // Guardar habito
    fun saveHabit(uidUsuario: String, onComplete: (Boolean, String) -> Unit) {
        val habito = _state.value
        repository.saveHabit(habito, uidUsuario, onComplete)
    }

    // Eliminar habito
    fun deleteHabit(uidHabito: String, uidUsuario: String, onComplete: (Boolean, String) -> Unit) {
        repository.deleteHabit(uidHabito, uidUsuario, onComplete)
    }

    // Actualizar habito
    fun updateHabit(uidUsuario: String, onComplete: (Boolean, String) -> Unit) {
        val habito = _state.value
        repository.updateHabit(habito, uidUsuario, onComplete)
    }

    fun onValueChange(field: String, value: String) {
        _state.value = when (field) {
            "uidHabito" -> _state.value.copy(uidHabito = value)
            "nombre" -> _state.value.copy(nombre = value)
            "descripcion" -> _state.value.copy(descripcion = value)
            "rachaDias" -> _state.value.copy(rachaDias = value.toIntOrNull() ?: 0)
            "estado" -> _state.value.copy(estado = value.toBooleanStrictOrNull() ?: false)
            "fechaInicio" -> _state.value.copy(fechaInicio = value)
            "fechaFin" -> _state.value.copy(fechaFin = value)
            else -> _state.value
        }
    }

    fun cambiaAlert() {
        _state.value = _state.value.copy(showAlert = true)
    }

    fun cancelAlert() {
        _state.value = _state.value.copy(showAlert = false)
    }

    fun limpiar() {
        _state.value = Habitos()
    }
}

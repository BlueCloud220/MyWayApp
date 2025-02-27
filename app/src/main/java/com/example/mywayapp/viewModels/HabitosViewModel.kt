package com.example.mywayapp.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mywayapp.data.repository.HabitosRepository
import com.example.mywayapp.model.Habitos
import com.example.mywayapp.views.getTodayDate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HabitosViewModel : ViewModel() {

    private val repository = HabitosRepository()

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

    val habitos = repository.habitos

    init {
        if (state.value.fechaInicio.isEmpty()) {
            onValueChange("fechaInicio", getTodayDate())
        }
        if (habitos.value.isEmpty()) {
            fetchHabitos()
        }
    }

    private fun fetchHabitos() {
        viewModelScope.launch {
            repository.fetchHabitos()
        }
    }

    fun loadHabito(uidHabito: String) {
        viewModelScope.launch {
            repository.fetchHabitoById(uidHabito)
            repository.habito.collect { habito ->
                _state.value = habito
            }
        }
    }

    // Guardar habito
    fun saveHabito(onComplete: (Boolean, String) -> Unit) {
        val habito = _state.value
        repository.saveHabito(habito, onComplete)
    }

    // Eliminar habito
    fun deleteHabito(uidHabito: String, onComplete: (Boolean, String) -> Unit) {
        repository.deleteHabito(uidHabito, onComplete)
    }

    // Actualizar habito
    fun updateHabito(onComplete: (Boolean, String) -> Unit) {
        val habito = _state.value
        repository.updateHabito(habito, onComplete)
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

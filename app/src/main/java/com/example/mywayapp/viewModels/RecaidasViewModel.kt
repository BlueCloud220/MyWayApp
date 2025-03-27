package com.example.mywayapp.viewModels

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mywayapp.data.repository.RecaidasRepository
import com.example.mywayapp.model.Recaidas
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RecaidasViewModel : ViewModel() {

    private val repository = RecaidasRepository()

    private val _state = MutableStateFlow(
        Recaidas(
            uidRecaida = "",
            fechaRecaida = "",
            motivo = "",
        )
    )
    val state: StateFlow<Recaidas> = _state

    private val _uidUsuario = mutableStateOf("")
    val uidUsuario: State<String> = _uidUsuario

    private val _uidHabito = mutableStateOf("")
    val uidHabito: State<String> = _uidHabito

    fun onUsuarioHabitoCargado(uidUsuario: String, uidHabito: String) {
        _uidUsuario.value = uidUsuario
        _uidHabito.value = uidHabito
        if (uidUsuario.isNotEmpty() && uidHabito.isNotEmpty()) {
            fetchRelapsesUser(uidUsuario, uidHabito)
        }
    }

    val recaidas = repository.recaidas

    fun fetchRelapsesUser(uidUsuario: String, uidHabito: String) {
        viewModelScope.launch {
            repository.fetchRelapsesUser(uidUsuario, uidHabito)
        }
    }

    fun loadRelapse(uidRecaida: String) {
        viewModelScope.launch {
            repository.fetchRecaidaById(uidRecaida)
            repository.recaida.collect { recaida ->
                _state.value = recaida
            }
        }
    }

    // Guardar recaida
    fun saveRecaida(uidUsuario: String, uidHabito: String, onComplete: (Boolean, String) -> Unit) {
        val recaida = _state.value
        repository.saveRecaida(recaida, uidUsuario, uidHabito, onComplete)
    }

    fun onValueChange(field: String, value: String) {
        _state.value = when (field) {
            "uidRecaida" -> _state.value.copy(uidRecaida = value)
            "fechaRecaida" -> _state.value.copy(fechaRecaida = value)
            "motivo" -> _state.value.copy(motivo = value)
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
        _state.value = Recaidas()
    }
}

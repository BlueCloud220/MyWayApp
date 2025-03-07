package com.example.mywayapp.viewModels

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

//    val recaidas = repository.recaidas
//
//    init {
//        if (state.value.fechaRecaida.isEmpty()) {
//            onValueChange("fechaRecaida", getTodayDate())
//        }
//        if (recaidas.value.isEmpty()) {
//            fetchRecaidas()
//        }
//    }

    private fun fetchRecaidas() {
        viewModelScope.launch {
            repository.fetchRecaidas()
        }
    }

    fun loadRecaida(uidRecaida: String) {
        viewModelScope.launch {
            repository.fetchRecaidaById(uidRecaida)
            repository.recaida.collect { recaida ->
                _state.value = recaida
            }
        }
    }

    // Guardar recaida
    fun saveRecaida(onComplete: (Boolean, String) -> Unit) {
        val recaida = _state.value
        repository.saveRecaida(recaida, onComplete)
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

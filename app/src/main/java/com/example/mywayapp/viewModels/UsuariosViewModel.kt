package com.example.mywayapp.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mywayapp.data.repository.UsuariosRepository
import com.example.mywayapp.model.Usuarios
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UsuariosViewModel : ViewModel() {

    private val repository = UsuariosRepository()

    private val _state = MutableStateFlow(
        Usuarios(
            uidUsuario = "",
            nombre = "",
            apellido = "",
            iconoPerfil = "",
            nombreUsuario = "",
            contrasena = "",
            tokenFCM = ""
        )
    )
    val state: StateFlow<Usuarios> = _state

    val usuarios = repository.usuarios

    private val _usuario = MutableLiveData<Usuarios>()
    val usuario: LiveData<Usuarios> get() = _usuario

    init {
        viewModelScope.launch {
            repository.usuario.collect { usuario ->
                _usuario.postValue(usuario)
            }
        }
    }

    fun loadUsuarioAuth(nombreUsuario: String, contrasena: String): Boolean {
        var result = false
        viewModelScope.launch {
            repository.fetchUsuarioByCredentials(nombreUsuario, contrasena)
            repository.usuario.collect { usuario ->
                if (usuario.uidUsuario.isNotEmpty()) {
                    _state.value = usuario
                    result = true
                }
            }
        }
        return result
    }

//    fun loadUsuarioAuth() {
//        viewModelScope.launch {
//            repository.fetchUsuarioAuth()
//        }
//    }
//
//    fun loadUsuario(uidUsuario: String) {
//        viewModelScope.launch {
//            repository.fetchUsuarioById(uidUsuario)
//            repository.usuario.collect { usuario ->
//                _state.value = usuario
//            }
//        }
//    }

    // Guardar usuario
    fun saveUsuario(onComplete: (Boolean, String) -> Unit) {
        val usuario = _state.value
        repository.saveUsuario(usuario, onComplete)
    }

    // Eliminar usuario
    fun deleteUsuario(uidUsuario: String, onComplete: (Boolean, String) -> Unit) {
        repository.deleteUsuario(uidUsuario, onComplete)
    }

    // Actualizar usuario
    fun updateUsuario(onComplete: (Boolean, String) -> Unit) {
        val usuario = _state.value
        repository.updateUsuario(usuario, onComplete)
    }

    fun onValueChange(field: String, value: String) {
        _state.value = when (field) {
            "uidUsuario" -> _state.value.copy(uidUsuario = value)
            "nombre" -> _state.value.copy(nombre = value)
            "apellido" -> _state.value.copy(apellido = value)
            "iconoPerfil" -> _state.value.copy(iconoPerfil = value)
            "nombreUsuario" -> _state.value.copy(nombreUsuario = value)
            "contrasena" -> _state.value.copy(contrasena = value)
            "tokenFCM" -> _state.value.copy(tokenFCM = value)
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
        _state.value = Usuarios()
    }
}

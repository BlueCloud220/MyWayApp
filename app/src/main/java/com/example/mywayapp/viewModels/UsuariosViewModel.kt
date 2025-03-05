package com.example.mywayapp.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mywayapp.data.repository.UsuariosRepository
import com.example.mywayapp.model.Usuarios
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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

    private val _usuario = MutableLiveData<Usuarios>()
    val usuario: LiveData<Usuarios> get() = _usuario

    init {
        viewModelScope.launch {
            repository.usuario.collect { usuario ->
                _usuario.postValue(usuario)
            }
        }
    }

    private val _authSuccess = MutableStateFlow(false)
    val authSuccess: StateFlow<Boolean> = _authSuccess
    private val _authError = MutableStateFlow(false)
    val authError: StateFlow<Boolean> get() = _authError

    fun loadUsuarioAuth(nombreUsuario: String, contrasena: String) {
        viewModelScope.launch {
            repository.fetchUsuarioByCredentials(nombreUsuario, contrasena)
            repository.usuario.collect { usuario ->
                if (usuario.uidUsuario.isNotEmpty()) {
                    _authSuccess.value = true
                    _state.value = usuario
                    _authError.value = false
                } else {
                    _authSuccess.value = false
                    _authError.value = true
                }
            }
        }
    }

    private val _iconos = MutableStateFlow<List<String>>(emptyList())
    val iconos: StateFlow<List<String>> = _iconos.asStateFlow()

    private var lastFetchedIcons: List<String> = emptyList()


    fun loadProfileIcons() {

        viewModelScope.launch {
            val nuevosIconos = repository.fetchProfileIcons()
            if (nuevosIconos != lastFetchedIcons) {
                _iconos.value = nuevosIconos
                lastFetchedIcons = nuevosIconos
            }
        }
    }

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

    // Actualizar el token FCM
    fun updateIconProfile(iconUrl: String, onComplete: (Boolean, String) -> Unit) {
        _state.value = _state.value.copy(iconoPerfil = iconUrl)

        updateUsuario(onComplete)
    }

    // Actualizar el token FCM
    fun updateTokenFCM(token: String, onComplete: (Boolean, String) -> Unit) {
        _state.value = _state.value.copy(tokenFCM = token)

        updateUsuario(onComplete)
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

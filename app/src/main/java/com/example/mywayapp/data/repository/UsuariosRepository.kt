package com.example.mywayapp.data.repository

import com.example.mywayapp.model.Usuarios
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object FirebaseManagerUsuarios {
    private val db = FirebaseFirestore.getInstance()

    fun getUserCollection(): CollectionReference = db.collection("usuarios")
}

class UsuariosRepository {

    private val collectionRef = FirebaseManagerUsuarios.getUserCollection()

    private val _usuarios = MutableStateFlow<List<Usuarios>>(emptyList())
    val usuarios: StateFlow<List<Usuarios>> = _usuarios

    private val _usuario = MutableStateFlow(Usuarios())
    val usuario: StateFlow<Usuarios> = _usuario

    fun fetchUsuarioByCredentials(nombreUsuario: String, password: String) {
        collectionRef.whereEqualTo("nombreUsuario", nombreUsuario)
            .whereEqualTo("contrasena", password).get()
            .addOnSuccessListener { snapshot ->
                val usuario = snapshot.documents.firstOrNull()?.toObject<Usuarios>()
                _usuario.value = usuario ?: Usuarios()
            }
            .addOnFailureListener {
                _usuario.value = Usuarios()
            }
    }

    fun saveUsuario(usuario: Usuarios, onComplete: (Boolean, String) -> Unit) {
        val batch = FirebaseFirestore.getInstance().batch()

        val newDocRef =
            collectionRef.document()  // Aquí se crea un nuevo documento con un ID generado automáticamente
        val updatedUsuario = usuario.copy(uidUsuario = newDocRef.id)

        // Asignamos los datos del usuario con el UID actualizado
        batch.set(newDocRef, updatedUsuario)

        batch.commit()
            .addOnSuccessListener {
                onComplete(true, "Usuario registrado correctamente")
            }
            .addOnFailureListener { exception ->
                onComplete(false, exception.message ?: "Error desconocido")
            }
    }

    fun deleteUsuario(uidUsuario: String, onComplete: (Boolean, String) -> Unit) {
        collectionRef.document(uidUsuario).delete()
            .addOnSuccessListener {
                onComplete(true, "Usuario eliminado con éxito")
            }
            .addOnFailureListener { exception ->
                onComplete(false, exception.message ?: "Error desconocido")
            }
    }

    fun updateUsuario(usuario: Usuarios, onComplete: (Boolean, String) -> Unit) {
        if (usuario.uidUsuario.isNotEmpty()) {
            collectionRef.document(usuario.uidUsuario)
                .set(usuario)
                .addOnSuccessListener {
                    onComplete(true, "Perfil editado con éxito")
                }
                .addOnFailureListener { exception ->
                    onComplete(false, exception.message ?: "Error desconocido")
                }
        } else {
            onComplete(false, "El ID de usuario es inválido.")
        }
    }

}

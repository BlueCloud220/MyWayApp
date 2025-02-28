package com.example.mywayapp.data.repository

import com.example.mywayapp.model.Usuarios
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object FirebaseManagerUsuarios {
    private val db = FirebaseFirestore.getInstance()
    private val storageRef = FirebaseStorage.getInstance()

    fun getUserCollection(): CollectionReference = db.collection("usuarios")
    fun getIconsCollection(): StorageReference = storageRef.reference.child("iconosPerfil")
}

class UsuariosRepository {

    private val collectionRef = FirebaseManagerUsuarios.getUserCollection()
    private val collectionIconsRef = FirebaseManagerUsuarios.getIconsCollection()

    private val _usuario = MutableStateFlow(Usuarios())
    val usuario: StateFlow<Usuarios> = _usuario

    private val _iconos = MutableStateFlow<List<String>>(emptyList())
    val iconos: StateFlow<List<String>> = _iconos

    fun fetchProfileIcons() {
        collectionIconsRef.listAll()
            .addOnSuccessListener { listResult ->
                val urls = mutableListOf<String>()
                listResult.items.forEach { item ->
                    item.downloadUrl.addOnSuccessListener { uri ->
                        urls.add(uri.toString())
                        _iconos.value = urls
                    }
                }
            }
            .addOnFailureListener {
                _iconos.value = emptyList()
            }
    }

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
            collectionRef.document()
        val updatedUsuario = usuario.copy(uidUsuario = newDocRef.id)

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

package com.example.mywayapp.data.repository

import com.example.mywayapp.model.Usuarios
import com.google.firebase.auth.FirebaseAuth
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

    fun fetchUsuarioAuth() {
        val currentUser = FirebaseAuth.getInstance().currentUser

        if (currentUser != null) {
            collectionRef.document(currentUser.uid).get()
                .addOnSuccessListener { snapshot ->
                    val usuario = snapshot.toObject<Usuarios>()
                    _usuario.value = usuario ?: Usuarios()
                }
                .addOnFailureListener {
                    _usuario.value = Usuarios()
                }
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

//    fun fetchUsuarioById(uidUsuario: String) {
//        collectionRef.document(uidUsuario).get()
//            .addOnSuccessListener { snapshot ->
//                val usuario = snapshot.toObject<Usuarios>()
//                _usuario.value = usuario ?: Usuarios()
//            }
//            .addOnFailureListener {
//                _usuario.value = Usuarios()
//            }
//    }

    fun saveUsuario(usuario: Usuarios, onComplete: (Boolean, String) -> Unit) {
        val batch = FirebaseFirestore.getInstance().batch()

        val newDocRef = collectionRef.document()
        batch.set(newDocRef, usuario)

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
        collectionRef.document(usuario.uidUsuario).set(usuario)
            .addOnSuccessListener {
                onComplete(true, "Perfil editado con éxito")
            }
            .addOnFailureListener { exception ->
                onComplete(false, exception.message ?: "Error desconocido")
            }
    }
}

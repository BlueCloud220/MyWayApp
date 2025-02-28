package com.example.mywayapp.data.repository

import com.example.mywayapp.model.UsuarioHabitos
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object FirebaseManagerUsuarioHabitos {
    private val db = FirebaseFirestore.getInstance()

    fun getUsuarioHabitosCollection(): CollectionReference = db.collection("usuario_habitos")
}

class UsuarioHabitosRepository {

    private val collectionUsuarioHabitosRef =
        FirebaseManagerUsuarioHabitos.getUsuarioHabitosCollection()

    private val _usuarioHabitosList = MutableStateFlow<List<UsuarioHabitos>>(emptyList())
    val usuarioHabitosList: StateFlow<List<UsuarioHabitos>> = _usuarioHabitosList

    fun fetchUsuarioHabitos(uidUsuario: String) {
        FirebaseManagerUsuarioHabitos.getUsuarioHabitosCollection()
            .document(uidUsuario)
            .collection("habitos")
            .addSnapshotListener { snapshot, error ->
                if (error == null && snapshot != null) {
                    val list = snapshot.documents.mapNotNull { it.toObject<UsuarioHabitos>() }
                    _usuarioHabitosList.value = list
                }
            }
    }


    fun saveUsuarioHabito(usuarioHabito: UsuarioHabitos, onComplete: (Boolean, String) -> Unit) {
        val docRef = FirebaseManagerUsuarioHabitos.getUsuarioHabitosCollection()
            .document(usuarioHabito.uidUsuario)
            .collection("habitos")
            .document(usuarioHabito.uidHabito)

        docRef.get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                onComplete(false, "El hábito ya está asociado al usuario")
            } else {
                // Si no existe, asignamos un id para uidPrincipal y guardamos el documento
                val uidPrincipal = docRef.id  // O puedes generar un id distinto si lo prefieres
                val updatedUsuarioHabito = usuarioHabito.copy(uidPrincipal = uidPrincipal)
                docRef.set(updatedUsuarioHabito)
                    .addOnSuccessListener { onComplete(true, "Hábito guardado correctamente") }
                    .addOnFailureListener { exception ->
                        onComplete(false, exception.message ?: "Error desconocido")
                    }
            }
        }.addOnFailureListener {
            onComplete(false, it.message ?: "Error al verificar la existencia del hábito")
        }
    }

    fun deleteUsuarioHabito(uidUsuario: String, onComplete: (Boolean, String) -> Unit) {
        collectionUsuarioHabitosRef
            .document(uidUsuario)  // Aquí deberías tener el UID del usuario
            .delete()
            .addOnSuccessListener {
                onComplete(true, "Hábito eliminado correctamente")
            }
            .addOnFailureListener {
                onComplete(false, "Error al eliminar el hábito")
            }
    }

    fun updateUsuarioHabito(usuarioHabito: UsuarioHabitos, onComplete: (Boolean, String) -> Unit) {
        collectionUsuarioHabitosRef
            .document(usuarioHabito.uidUsuario)
            .collection("habitos")
            .document(usuarioHabito.uidHabito)
            .set(usuarioHabito) // Aquí puedes hacer set de nuevo para actualizar
            .addOnSuccessListener {
                onComplete(true, "Hábito actualizado correctamente")
            }
            .addOnFailureListener {
                onComplete(false, "Error al actualizar el hábito")
            }
    }
}
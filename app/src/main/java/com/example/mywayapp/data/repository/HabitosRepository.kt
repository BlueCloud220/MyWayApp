package com.example.mywayapp.data.repository

import com.example.mywayapp.model.Habitos
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object FirebaseManagerHabitos {
    private val db = FirebaseFirestore.getInstance()

    fun getUserCollection(): CollectionReference = db.collection("usuarios")
    fun getHabitsCollection(): CollectionReference = db.collection("habitos")
    fun getRelapsesCollection(): CollectionReference = db.collection("recaidas")
}

class HabitosRepository {

    private val collectionHabitsRef = FirebaseManagerHabitos.getHabitsCollection()
    private val collectionUsersRef = FirebaseManagerUsuarios.getUserCollection()

    private val _habitos = MutableStateFlow<List<Habitos>>(emptyList())
    val habitos: StateFlow<List<Habitos>> = _habitos

    private val _habito = MutableStateFlow(Habitos())
    val habito: StateFlow<Habitos> = _habito

    fun fetchHabitos() {
        collectionHabitsRef.addSnapshotListener { snapshot, error ->
            if (error == null && snapshot != null) {
                val habitosList = snapshot.documents.mapNotNull { it.toObject<Habitos>() }
                _habitos.value = habitosList
            }
        }
    }

    fun fetchHabitoById(uidHabito: String) {
        collectionHabitsRef.document(uidHabito).get()
            .addOnSuccessListener { snapshot ->
                val habito = snapshot.toObject<Habitos>()
                _habito.value = habito ?: Habitos()
            }
            .addOnFailureListener {
                _habito.value = Habitos()
            }
    }

    fun saveHabito(habito: Habitos, onComplete: (Boolean, String) -> Unit) {
        val batch = FirebaseFirestore.getInstance().batch()

        val newDocRef = collectionHabitsRef.document()
        batch.set(newDocRef, habito)

        val updatedHabito = habito.copy(uidHabito = newDocRef.id)
        batch.set(newDocRef, updatedHabito)

        batch.commit()
            .addOnSuccessListener {
                onComplete(true, "Hábito guardado con éxito")
            }
            .addOnFailureListener { exception ->
                onComplete(false, exception.message ?: "Error desconocido")
            }
    }

    fun deleteHabito(uidHabito: String, onComplete: (Boolean, String) -> Unit) {
        collectionHabitsRef.document(uidHabito).delete()
            .addOnSuccessListener {
                onComplete(true, "Hábito eliminado con éxito")
            }
            .addOnFailureListener { exception ->
                onComplete(false, exception.message ?: "Error desconocido")
            }
    }

    fun updateHabito(habito: Habitos, onComplete: (Boolean, String) -> Unit) {
        collectionHabitsRef.document(habito.uidHabito).set(habito)
            .addOnSuccessListener {
                onComplete(true, "Hábito editado con éxito")
            }
            .addOnFailureListener { exception ->
                onComplete(false, exception.message ?: "Error desconocido")
            }
    }
}

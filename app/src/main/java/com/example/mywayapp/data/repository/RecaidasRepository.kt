package com.example.mywayapp.data.repository

import com.example.mywayapp.model.Recaidas
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object FirebaseManagerRecaidas {
    private val db = FirebaseFirestore.getInstance()

    fun getRelapsesCollection(): CollectionReference = db.collection("recaidas")
}

class RecaidasRepository {

    private val collectionRelapsesRef = FirebaseManagerRecaidas.getRelapsesCollection()

    private val _recaidas = MutableStateFlow<List<Recaidas>>(emptyList())
    val recaidas: StateFlow<List<Recaidas>> = _recaidas

    private val _recaida = MutableStateFlow(Recaidas())
    val recaida: StateFlow<Recaidas> = _recaida

    fun fetchRecaidas() {
        collectionRelapsesRef.addSnapshotListener { snapshot, error ->
            if (error == null && snapshot != null) {
                val recaidasList = snapshot.documents.mapNotNull { it.toObject<Recaidas>() }
                _recaidas.value = recaidasList
            }
        }
    }

    fun fetchRecaidaById(uidRecaida: String) {
        collectionRelapsesRef.document(uidRecaida).get()
            .addOnSuccessListener { snapshot ->
                val recaida = snapshot.toObject<Recaidas>()
                _recaida.value = recaida ?: Recaidas()
            }
            .addOnFailureListener {
                _recaida.value = Recaidas()
            }
    }

    fun saveRecaida(recaida: Recaidas, onComplete: (Boolean, String) -> Unit) {
        val batch = FirebaseFirestore.getInstance().batch()

        val newDocRef = collectionRelapsesRef.document()
        batch.set(newDocRef, recaida)

        val updatedRecaida = recaida.copy(uidRecaida = newDocRef.id)
        batch.set(newDocRef, updatedRecaida)

        batch.commit()
            .addOnSuccessListener {
                onComplete(true, "Hábito guardado con éxito")
            }
            .addOnFailureListener { exception ->
                onComplete(false, exception.message ?: "Error desconocido")
            }
    }
}

package com.example.mywayapp.data.repository

import android.annotation.SuppressLint
import com.example.mywayapp.model.Recaidas
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object FirebaseManagerRecaidas {
    @SuppressLint("StaticFieldLeak")
    private val db = FirebaseFirestore.getInstance()

    fun getUserCollection(): CollectionReference = db.collection("usuarios")
}

class RecaidasRepository {
    private val collectionRelapsesRef = FirebaseManagerHabitos.getHabitsCollection()

    private fun getUserRelapsesCollection(
        uidUsuario: String,
        uidHabito: String
    ): CollectionReference {
        return FirebaseManagerRecaidas.getUserCollection()
            .document(uidUsuario)
            .collection("habitos")
            .document(uidHabito)
            .collection("recaidas")
    }

    private val _recaidas = MutableStateFlow<List<Recaidas>>(emptyList())
    val recaidas: StateFlow<List<Recaidas>> = _recaidas

    private val _recaida = MutableStateFlow(Recaidas())
    val recaida: StateFlow<Recaidas> = _recaida

    fun fetchRelapsesUser(uidUsuario: String, uidHabito: String) {
        val userRelapsesRef = getUserRelapsesCollection(uidUsuario, uidHabito)
        userRelapsesRef.addSnapshotListener { snapshot, error ->
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

    fun saveRecaida(
        recaida: Recaidas,
        uidUsuario: String,
        uidHabito: String,
        onComplete: (Boolean, String) -> Unit
    ) {
        val userRelapsesRef = getUserRelapsesCollection(uidUsuario, uidHabito)

        // Obtener todos los hábitos del usuario
        userRelapsesRef.get().addOnSuccessListener { querySnapshot ->
            val relapseExists = querySnapshot.documents.any { document ->
                val existingRelapse = document.toObject(Recaidas::class.java)
                existingRelapse?.fechaRecaida?.equals(
                    recaida.fechaRecaida,
                    ignoreCase = true
                ) == true
            }

            if (relapseExists) {
                onComplete(false, "Lo siento, ya has registrado una recaída el día de hoy")
            } else {
                // Guardar el nuevo hábito con un uid único
                val newRelapseRef = userRelapsesRef.document()
                val updatedRecaida = recaida.copy(uidRecaida = newRelapseRef.id)

                newRelapseRef.set(updatedRecaida)
                    .addOnSuccessListener { onComplete(true, "Recaída registrada con éxito") }
                    .addOnFailureListener { exception ->
                        onComplete(false, exception.message ?: "Error desconocido")
                    }
            }
        }.addOnFailureListener {
            onComplete(false, it.message ?: "Error al verificar la existencia de la recaída")
        }
    }
}

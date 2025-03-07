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
}

class HabitosRepository {
    private val collectionHabitsRef = FirebaseManagerHabitos.getHabitsCollection()

    fun getUserHabitsCollection(uidUsuario: String): CollectionReference {
        return FirebaseManagerHabitos.getUserCollection().document(uidUsuario).collection("habitos")
    }

    private val _habitos = MutableStateFlow<List<Habitos>>(emptyList())
    val habitos: StateFlow<List<Habitos>> = _habitos

    private val _habito = MutableStateFlow(Habitos())
    val habito: StateFlow<Habitos> = _habito

    private val _habitosUsuario = MutableStateFlow<List<Habitos>>(emptyList())
    val habitosUsuario: StateFlow<List<Habitos>> = _habitosUsuario

    fun fetchHabits() {
        collectionHabitsRef.addSnapshotListener { snapshot, error ->
            if (error == null && snapshot != null) {
                val habitosList = snapshot.documents.mapNotNull { it.toObject<Habitos>() }
                _habitos.value = habitosList
            }
        }
    }

    // Método para obtener los hábitos de un usuario
    fun fetchHabitsUser(uidUsuario: String) {
        val userHabitsRef = getUserHabitsCollection(uidUsuario)
        userHabitsRef.addSnapshotListener { snapshot, error ->
            if (error == null && snapshot != null) {
                val habitosList = snapshot.documents.mapNotNull { it.toObject<Habitos>() }
                _habitosUsuario.value = habitosList
            }
        }
    }

    // Método para obtener un hábito específico por su ID
    fun fetchHabitById(uidUsuario: String, uidHabito: String) {
        val userHabitsRef = getUserHabitsCollection(uidUsuario)
        userHabitsRef.document(uidHabito).get()
            .addOnSuccessListener { snapshot ->
                val habito = snapshot.toObject<Habitos>()
                _habito.value = habito ?: Habitos()
            }
            .addOnFailureListener {
                _habito.value = Habitos()
            }
    }

    // Método para guardar un hábito en la subcolección "habitos" de un usuario
    fun saveHabit(habito: Habitos, uidUsuario: String, onComplete: (Boolean, String) -> Unit) {
        val userHabitsRef = getUserHabitsCollection(uidUsuario)

        // Obtener todos los hábitos del usuario
        userHabitsRef.get().addOnSuccessListener { querySnapshot ->
            val habitExists = querySnapshot.documents.any { document ->
                val existingHabit = document.toObject(Habitos::class.java)
                existingHabit?.nombre?.equals(habito.nombre, ignoreCase = true) == true
            }

            if (habitExists) {
                onComplete(false, "Ya está registrado el hábito, intenta con otro")
            } else {
                // Guardar el nuevo hábito con un uid único
                val newHabitRef = userHabitsRef.document()
                val updatedHabito = habito.copy(uidHabito = newHabitRef.id)

                newHabitRef.set(updatedHabito)
                    .addOnSuccessListener { onComplete(true, "Hábito guardado con éxito") }
                    .addOnFailureListener { exception ->
                        onComplete(false, exception.message ?: "Error desconocido")
                    }
            }
        }.addOnFailureListener {
            onComplete(false, it.message ?: "Error al verificar la existencia del hábito")
        }
    }

    // Método para eliminar un hábito de un usuario
    fun deleteHabit(uidHabito: String, uidUsuario: String, onComplete: (Boolean, String) -> Unit) {
        val userHabitsRef = getUserHabitsCollection(uidUsuario)
        userHabitsRef.document(uidHabito).delete()
            .addOnSuccessListener {
                onComplete(true, "Hábito eliminado con éxito")
            }
            .addOnFailureListener { exception ->
                onComplete(false, exception.message ?: "Error desconocido")
            }
    }

    // Método para actualizar un hábito en la subcolección de un usuario
    fun updateHabit(habito: Habitos, uidUsuario: String, onComplete: (Boolean, String) -> Unit) {
        val userHabitsRef = getUserHabitsCollection(uidUsuario)
        userHabitsRef.document(habito.uidHabito).set(habito)
            .addOnSuccessListener {
                onComplete(true, "Hábito editado con éxito")
            }
            .addOnFailureListener { exception ->
                onComplete(false, exception.message ?: "Error desconocido")
            }
    }
}

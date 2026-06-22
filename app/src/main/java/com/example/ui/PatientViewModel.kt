package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.Patient
import com.example.data.PatientRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class FilterMode {
    ALL, RECENT, AZ, CRITICAL
}

class PatientViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: PatientRepository
    
    val searchQuery = MutableStateFlow("")
    val activeFilter = MutableStateFlow(FilterMode.ALL)

    init {
        val database = AppDatabase.getDatabase(application)
        repository = PatientRepository(database.patientDao())
    }

    // Combina os fluxos reativos de busca, filtros e banco de dados local
    val patients: StateFlow<List<Patient>> = repository.allPatients
        .combine(searchQuery) { list, query ->
            if (query.isBlank()) {
                list
            } else {
                list.filter {
                    it.fullName.contains(query, ignoreCase = true) || 
                    it.notes.contains(query, ignoreCase = true)
                }
            }
        }
        .combine(activeFilter) { list, filter ->
            when (filter) {
                FilterMode.ALL -> list
                FilterMode.RECENT -> list.take(10) // Últimos 10 cadastrados
                FilterMode.AZ -> list.sortedBy { it.fullName.lowercase() }
                FilterMode.CRITICAL -> list.filter {
                    it.notes.contains("crítico", ignoreCase = true) ||
                    it.notes.contains("urgente", ignoreCase = true) ||
                    it.notes.contains("urgência", ignoreCase = true) ||
                    it.notes.contains("emergência", ignoreCase = true) ||
                    it.notes.contains("alta", ignoreCase = true)
                }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun addPatient(fullName: String, birthDate: String, notes: String) {
        viewModelScope.launch {
            if (fullName.isNotBlank()) {
                val newPatient = Patient(
                    fullName = fullName.trim(),
                    birthDate = birthDate.trim(),
                    notes = notes.trim()
                )
                repository.insert(newPatient)
            }
        }
    }

    fun deletePatient(patient: Patient) {
        viewModelScope.launch {
            repository.delete(patient)
        }
    }
}

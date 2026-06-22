package com.example.data

import kotlinx.coroutines.flow.Flow

class PatientRepository(private val patientDao: PatientDao) {
    val allPatients: Flow<List<Patient>> = patientDao.getAllPatients()

    suspend fun insert(patient: Patient) {
        patientDao.insertPatient(patient)
    }

    suspend fun delete(patient: Patient) {
        patientDao.deletePatient(patient)
    }

    suspend fun deleteById(id: Int) {
        patientDao.deletePatientById(id)
    }
}

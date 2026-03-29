package com.thortech.clockr.data

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await

class FirestoreRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val entriesCollection = firestore.collection("time_entries")

    suspend fun syncTimeEntry(timeEntry: TimeEntry) {
        if (timeEntry.userId.isEmpty()) return
        
        try {
            val docRef = entriesCollection.document(timeEntry.id.toString())
            docRef.set(timeEntry.toMap(), SetOptions.merge()).await()
        } catch (e: Exception) {
            // Handle error (e.g., log it or return a result)
            e.printStackTrace()
        }
    }

    suspend fun deleteTimeEntry(entryId: Long) {
        try {
            entriesCollection.document(entryId.toString()).delete().await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

package com.thortech.clockr.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AuthRepository {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    
    private val _currentUser = MutableStateFlow<FirebaseUser?>(auth.currentUser)
    val currentUser: StateFlow<FirebaseUser?> = _currentUser

    init {
        auth.addAuthStateListener { firebaseAuth ->
            _currentUser.value = firebaseAuth.currentUser
        }
    }

    fun signInAnonymously(onComplete: (Boolean) -> Unit) {
        auth.signInAnonymously()
            .addOnCompleteListener { task ->
                onComplete(task.isSuccessful)
            }
    }

    fun signOut() {
        auth.signOut()
    }
}

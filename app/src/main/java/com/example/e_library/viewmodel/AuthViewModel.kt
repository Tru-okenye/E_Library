package com.example.e_library.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.e_library.data.firebase.AuthService
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    object Success : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel(
    private val authService: AuthService = AuthService()
) : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    fun login(email: String, password: String) {
        _authState.value = AuthState.Loading
        viewModelScope.launch {
            val result = authService.login(email, password)
            _authState.value = if (result.isSuccess) AuthState.Success
            else AuthState.Error(result.exceptionOrNull()?.message ?: "Login failed")
        }
    }

    fun register(email: String, password: String) {
        _authState.value = AuthState.Loading
        viewModelScope.launch {
            val result = authService.register(email, password)
            _authState.value = if (result.isSuccess) AuthState.Success
            else AuthState.Error(result.exceptionOrNull()?.message ?: "Registration failed")
        }
    }

//    fun saveUserProfile(uid: String, data: Map<String, Any>) {
//        viewModelScope.launch {
//            FirebaseFirestore.getInstance()
//                .collection("users")
//                .document(uid)
//                .set(data)
//                .addOnSuccessListener {
//                    // You can optionally log success
//                }
//                .addOnFailureListener {
//                    _authState.value = AuthState.Error("Failed to save profile: ${it.message}")
//                }
//        }
//    }




    fun logout() {
        authService.logout()
    }

    fun isUserLoggedIn(): Boolean = authService.isUserLoggedIn()

    fun resetState() {
        _authState.value = AuthState.Idle
    }

    fun getCurrentUserEmail(): String? {
        return FirebaseAuth.getInstance().currentUser?.email
    }

    val currentUser
        get() = FirebaseAuth.getInstance().currentUser

    val userProfile = MutableStateFlow<Map<String, Any>?>(null)

    fun fetchUserProfile() {
        val uid = currentUser?.uid ?: return
        db.collection("users").document(uid).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    userProfile.value = document.data
                } else {
                    Log.e("Profile", "No profile data found")
                }
            }
            .addOnFailureListener { e ->
                Log.e("Profile", "Error fetching profile: $e")
            }
    }

    fun saveUserProfile(uid: String, profileData: Map<String, Any>) {
        Firebase.firestore.collection("users")
            .document(uid)
            .set(profileData, SetOptions.merge())
            .addOnSuccessListener {
                Log.d("Register", "User profile successfully saved")
            }
            .addOnFailureListener {
                Log.e("Register", "Error saving user profile", it)
            }
    }


}


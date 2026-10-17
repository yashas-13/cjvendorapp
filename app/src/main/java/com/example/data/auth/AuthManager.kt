package com.example.data.auth

import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import com.example.R
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

sealed interface AuthState {
    data object Unauthenticated : AuthState
    data object Loading : AuthState
    data class Authenticated(val user: FirebaseUser, val role: String = "customer") : AuthState
    data class Error(val message: String) : AuthState
}

class AuthManager(
    private val context: Context,
    private val auth: FirebaseAuth? = try { FirebaseAuth.getInstance() } catch (e: Exception) { null }
) {
    companion object {
        private const val TAG = "AuthManager"
    }

    private val credentialManager = try {
        CredentialManager.create(context)
    } catch (e: Exception) {
        null
    }

    private val _authState = MutableStateFlow<AuthState>(
        auth?.currentUser?.let { AuthState.Authenticated(it) } ?: AuthState.Unauthenticated
    )
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    val currentUser: FirebaseUser?
        get() = auth?.currentUser

    val isUserAuthenticated: Boolean
        get() = auth?.currentUser != null

    init {
        auth?.addAuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            _authState.value = if (user != null) {
                AuthState.Authenticated(user)
            } else {
                AuthState.Unauthenticated
            }
        }
    }

    /**
     * Interactive One-Tap Google Sign-In using Credential Manager
     */
    suspend fun signInWithGoogle(): Result<FirebaseUser> {
        val serverClientId = try {
            context.getString(R.string.default_web_client_id)
        } catch (e: Exception) {
            Log.e(TAG, "default_web_client_id string not found in generated resources", e)
            return Result.failure(IllegalStateException("Google Client ID not configured"))
        }

        _authState.value = AuthState.Loading

        val googleIdOption = GetSignInWithGoogleOption.Builder(serverClientId)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        val cm = credentialManager ?: return Result.failure(IllegalStateException("Credential Manager not available"))
        val firebaseAuth = auth ?: return Result.failure(IllegalStateException("Firebase Auth not available"))

        return try {
            val response = cm.getCredential(
                context = context,
                request = request
            )
            val credential = response.credential
            val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
            val idToken = googleIdTokenCredential.idToken

            val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
            val user = suspendCoroutine<FirebaseUser> { continuation ->
                firebaseAuth.signInWithCredential(firebaseCredential)
                    .addOnSuccessListener { authResult ->
                        val signedInUser = authResult.user
                        if (signedInUser != null) {
                            continuation.resume(signedInUser)
                        } else {
                            continuation.resumeWithException(IllegalStateException("Firebase User is null after sign in"))
                        }
                    }
                    .addOnFailureListener { exception ->
                        continuation.resumeWithException(exception)
                    }
            }

            _authState.value = AuthState.Authenticated(user)
            Result.success(user)
        } catch (e: GetCredentialCancellationException) {
            Log.w(TAG, "Google Sign-In was cancelled by the user", e)
            _authState.value = AuthState.Unauthenticated
            Result.failure(e)
        } catch (e: GetCredentialException) {
            Log.e(TAG, "Credential Manager error: ${e.message}", e)
            _authState.value = AuthState.Error(e.message ?: "Authentication failed")
            Result.failure(e)
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected sign-in failure: ${e.message}", e)
            _authState.value = AuthState.Error(e.message ?: "Unexpected sign-in error")
            Result.failure(e)
        }
    }

    fun signOut() {
        auth?.signOut()
        _authState.value = AuthState.Unauthenticated
    }
}

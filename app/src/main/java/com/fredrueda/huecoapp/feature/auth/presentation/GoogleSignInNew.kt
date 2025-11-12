package com.fredrueda.huecoapp.feature.auth.presentation

import android.app.Activity
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.launch

@Composable
fun rememberGoogleCredentialSignIn(
    activity: Activity,
    webClientId: String,
    onResult: (Boolean, String?) -> Unit
): () -> Unit {
    val scope = rememberCoroutineScope()

    return {
        scope.launch {
            try {
                val credentialManager = CredentialManager.create(activity)

                val googleIdOption = GetGoogleIdOption.Builder()
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(webClientId)
                    .build()

                val request = GetCredentialRequest.Builder()
                    .addCredentialOption(googleIdOption)
                    .build()

                val result = credentialManager.getCredential(activity, request)
                val credential = result.credential

                val googleIdTokenCredential = try {
                    GoogleIdTokenCredential.createFrom(credential.data)
                } catch (e: GoogleIdTokenParsingException) {
                    Log.e("GoogleSignIn", "Error parsing credential: ${e.message}")
                    null
                }

                googleIdTokenCredential?.idToken?.let { token ->
                    val auth = FirebaseAuth.getInstance()
                    val firebaseCredential = GoogleAuthProvider.getCredential(token, null)
                    auth.signInWithCredential(firebaseCredential)
                        .addOnCompleteListener(activity) { task ->
                            if (task.isSuccessful) {
                                onResult(true, token)
                            } else {
                                onResult(false, task.exception?.message)
                            }
                        }
                } ?: onResult(false, "Token nulo")
            } catch (e: Exception) {
                Log.e("GoogleSignIn", "Error general: ${e.message}")
                onResult(false, e.message)
            }
        }
    }
}

package com.fredrueda.huecoapp.feature.auth.presentation

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth

@Composable
fun rememberFacebookLoginLauncher(
    activity: Activity,
    onResult: (Boolean) -> Unit
): () -> Unit {
    val callbackManager = remember { CallbackManager.Factory.create() }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {}

    DisposableEffect(Unit) {
        val callback = object : FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult) {
                val credential = FacebookAuthProvider.getCredential(result.accessToken.token)
                FirebaseAuth.getInstance().signInWithCredential(credential)
                    .addOnCompleteListener(activity) { task ->
                        onResult(task.isSuccessful)
                    }
            }
            override fun onCancel() = onResult(false)
            override fun onError(error: FacebookException) = onResult(false)
        }
        LoginManager.getInstance().registerCallback(callbackManager, callback)
        onDispose { LoginManager.getInstance().unregisterCallback(callbackManager) }
    }

    return {
        LoginManager.getInstance()
            .logInWithReadPermissions(activity, listOf("email", "public_profile"))
    }
}

package com.fredrueda.huecoapp.feature.auth.presentation

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult

@Composable
fun rememberFacebookSignIn(
    activity: Activity,
    onResult: (success: Boolean, token: String?, message: String?) -> Unit
): () -> Unit {
    val callbackManager = remember { CallbackManager.Factory.create() }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { }

    DisposableEffect(Unit) {
        val callback = object : FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult) {
                val token = result.accessToken.token
                onResult(true, token, null)
            }

            override fun onCancel() {
                onResult(false, null, "Cancelado por el usuario")
            }

            override fun onError(error: FacebookException) {
                onResult(false, null, error.message)
            }
        }
        LoginManager.getInstance().registerCallback(callbackManager, callback)

        onDispose {
            LoginManager.getInstance().unregisterCallback(callbackManager)
        }
    }

    return {
        LoginManager.getInstance()
            .logInWithReadPermissions(activity, listOf("email", "public_profile"))
    }
}

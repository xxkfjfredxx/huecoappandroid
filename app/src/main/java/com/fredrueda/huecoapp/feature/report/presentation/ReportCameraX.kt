package com.fredrueda.huecoapp.feature.report.presentation

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.camera.core.AspectRatio
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * CameraX listo para Compose:
 * - Usa UNA sola instancia de PreviewView (evita pantalla negra).
 * - ImplementationMode.COMPATIBLE + FIT_CENTER.
 * - Bind de casos de uso en main executor y en orden correcto.
 * - Ajusta targetRotation con el display actual.
 * - Libera executor y unbind en onDispose.
 * - IMPORTANTE: Asume que el permiso de cámara YA fue concedido antes de mostrar este composable.
 */
@Composable
fun ReportCameraX(
    modifier: Modifier = Modifier,
    onCapture: (bitmap: Bitmap, base64: String) -> Unit,
    onClose: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val scope = rememberCoroutineScope()
    val mainExecutor = ContextCompat.getMainExecutor(context)

    // PreviewView único y recordado
    val previewView = remember(context) {
        PreviewView(context).apply {
            implementationMode = PreviewView.ImplementationMode.COMPATIBLE
            scaleType = PreviewView.ScaleType.FIT_CENTER
            layoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }
    }

    // Executor para captura
    val cameraExecutor: ExecutorService = remember { Executors.newSingleThreadExecutor() }

    // Caso de uso de captura
    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }

    // Arranque de la cámara (permiso ya concedido antes de mostrar este composable)
    LaunchedEffect(Unit) {
        val providerFuture = ProcessCameraProvider.getInstance(context)
        providerFuture.addListener({
            val cameraProvider = providerFuture.get()
            try {
                val rotation = previewView.display?.rotation ?: android.view.Surface.ROTATION_0

                val preview = Preview.Builder()
                    .setTargetAspectRatio(AspectRatio.RATIO_4_3)
                    .setTargetRotation(rotation)
                    .build().also { p ->
                        p.setSurfaceProvider(previewView.surfaceProvider)
                    }

                val capture = ImageCapture.Builder()
                    .setTargetAspectRatio(AspectRatio.RATIO_4_3)
                    .setTargetRotation(rotation)
                    .setCaptureMode(CAPTURE_MODE_MINIMIZE_LATENCY)
                    .build()

                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    CameraSelector.DEFAULT_BACK_CAMERA,
                    preview,
                    capture
                )

                imageCapture = capture
                Log.d("CameraX", "Preview y captura vinculadas correctamente")
            } catch (e: Exception) {
                Log.e("CameraX", "Fallo al iniciar CameraX: ${e.message}", e)
                Toast.makeText(context, "No se pudo iniciar la cámara", Toast.LENGTH_SHORT).show()
            }
        }, mainExecutor)
    }

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        AndroidView(
            factory = { previewView },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        )

        Row(
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceEvenly
        ) {
            OutlinedButton(onClick = onClose) { Text("Cancelar") }

            Button(
                onClick = {
                    val capture = imageCapture ?: run {
                        Toast.makeText(context, "Cámara no lista", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    val photoFile = File(context.cacheDir, "photo_${System.currentTimeMillis()}.jpg")
                    val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

                    // Asegura rotación correcta por si el usuario giró el dispositivo
                    capture.targetRotation =
                        previewView.display?.rotation ?: android.view.Surface.ROTATION_0

                    capture.takePicture(
                        outputOptions,
                        cameraExecutor,
                        object : ImageCapture.OnImageSavedCallback {
                            override fun onError(exc: ImageCaptureException) {
                                Log.e("CameraX", "Error en captura: ${exc.message}", exc)
                                scope.launch(Dispatchers.Main) {
                                    Toast.makeText(context, "Error al capturar", Toast.LENGTH_SHORT).show()
                                }
                            }

                            override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                                val bitmap = decodeBitmapSafely(photoFile)
                                val base64 = bitmapToBase64(bitmap)
                                scope.launch(Dispatchers.Main) {
                                    onCapture(bitmap, base64)
                                    onClose()
                                }
                            }
                        }
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD000))
            ) {
                Text("Tomar foto", color = Color.Black)
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            try {
                ProcessCameraProvider.getInstance(context).get().unbindAll()
            } catch (_: Exception) { /* ignore */ }
            cameraExecutor.shutdown()
        }
    }
}

/* ---------- Utilidades seguras ---------- */

private fun decodeBitmapSafely(file: File): Bitmap {
    val probe = BitmapFactory.Options().apply { inJustDecodeBounds = true }
    BitmapFactory.decodeFile(file.absolutePath, probe)
    val maxDim = 2048
    var sample = 1
    while (probe.outWidth / sample > maxDim || probe.outHeight / sample > maxDim) sample *= 2
    val opts = BitmapFactory.Options().apply { inSampleSize = sample }
    return BitmapFactory.decodeFile(file.absolutePath, opts)
}

fun bitmapToBase64(bitmap: Bitmap): String {
    val out = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, 75, out)
    return Base64.encodeToString(out.toByteArray(), Base64.DEFAULT)
}

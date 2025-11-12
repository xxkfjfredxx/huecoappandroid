package com.fredrueda.huecoapp.feature.report.presentation

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import coil.compose.rememberAsyncImagePainter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportFormSheet(
    onDismiss: () -> Unit,
    onSubmit: (String, String?) -> Unit
) {
    val context = LocalContext.current
    var description by remember { mutableStateOf("") }
    var imageBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var imageUri by remember { mutableStateOf<String?>(null) }
    var imageBase64 by remember { mutableStateOf<String?>(null) }
    var showCamera by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
    var cameraPermissionGranted by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        )
    }

    val coroutineScope = rememberCoroutineScope()

    // --- Launcher para pedir permiso de CÁMARA ---
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        cameraPermissionGranted = granted
        if (granted) {
            showCamera = true
        } else {
            Toast.makeText(context, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show()
        }
    }

    // --- Selector desde galería ---
    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let {
            imageUri = it.toString()
            val stream = context.contentResolver.openInputStream(it)
            val bitmap = BitmapFactory.decodeStream(stream)
            imageBitmap = bitmap
            imageBase64 = bitmapToBase64(bitmap)
        }
    }

    if (showCamera) {
        // IMPORTANTE: aquí sólo entramos si el permiso ya está concedido
        ReportCameraX(
            onCapture = { bitmap, base64 ->
                imageBitmap = bitmap
                imageBase64 = base64
                showCamera = false
            },
            onClose = { showCamera = false }
        )
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .imePadding()
                .navigationBarsPadding()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    "📸 Nuevo reporte de hueco",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }

            // --- Banner informativo si falta permiso ---
           /* if (!cameraPermissionGranted) {
                item {
                    AssistChip(
                        onClick = { cameraPermissionLauncher.launch(Manifest.permission.CAMERA) },
                        label = { Text("Conceder permiso de cámara para tomar foto") }
                    )
                }
            }

            */

            // --- Vista previa de imagen ---
            if (imageBitmap != null || imageUri != null) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 180.dp, max = 300.dp)
                            .clip(MaterialTheme.shapes.medium)
                    ) {
                        when {
                            imageBitmap != null -> Image(
                                bitmap = imageBitmap!!.asImageBitmap(),
                                contentDescription = "Foto tomada",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                            imageUri != null -> Image(
                                painter = rememberAsyncImagePainter(imageUri),
                                contentDescription = "Foto seleccionada",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }
            }

            // --- Botón de selección ---
            item {
                Button(
                    onClick = { showDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD000)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Seleccionar o tomar imagen", color = Color.Black)
                }
            }

            // --- Campo de descripción ---
            item {
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descripción del hueco") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
            }

            // --- Enviar ---
            item {
                Button(
                    onClick = { onSubmit(description, imageBase64) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD000))
                ) {
                    Text("Enviar reporte", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }

            // --- Cancelar ---
            item {
                TextButton(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) {
                    Text("Cancelar", color = Color.Gray)
                }
            }
        }

        // --- Diálogo de opciones ---
        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                confirmButton = {},
                title = { Text("Selecciona una opción") },
                text = {
                    Column {
                        Text(
                            "Tomar foto",
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    showDialog = false
                                    if (cameraPermissionGranted) {
                                        showCamera = true
                                    } else {
                                        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                                    }
                                }
                                .padding(12.dp)
                        )
                        Divider()
                        Text(
                            "Seleccionar desde galería",
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    showDialog = false
                                    galleryLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                }
                                .padding(12.dp)
                        )
                    }
                }
            )
        }
    }
}

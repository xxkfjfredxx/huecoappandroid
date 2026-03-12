package com.fredrueda.huecoapp.feature.huecos.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun DenunciaDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit
) {
    var selectedMotivo by remember { mutableStateOf("obscene") }
    var comentario by remember { mutableStateOf("") }
    
    val motivos = listOf(
        "spam" to "Contenido spam / falso",
        "obscene" to "Imagen obscena / inapropiada",
        "offensive" to "Lenguaje ofensivo",
        "wrong_location" to "Ubicación incorrecta",
        "other" to "Otro motivo"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Reportar contenido inapropiado", fontWeight = FontWeight.Bold) },
        text = {
            Column {
                Text("¿Por qué deseas reportar este reporte de hueco?")
                Spacer(Modifier.height(8.dp))
                
                motivos.forEach { (key, label) ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedMotivo = key }
                            .padding(vertical = 4.dp)
                    ) {
                        RadioButton(
                            selected = (selectedMotivo == key),
                            onClick = { selectedMotivo = key }
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(label)
                    }
                }
                
                Spacer(Modifier.height(16.dp))
                OutlinedTextField(
                    value = comentario,
                    onValueChange = { comentario = it },
                    label = { Text("Comentario adicional (opcional)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(selectedMotivo, comentario) }) {
                Text("Enviar reporte")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

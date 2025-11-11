package com.fredrueda.huecoapp.feature.home.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.fredrueda.huecoapp.feature.home.model.HomeItem

@Preview(showBackground = true)
@Composable
fun HomeScreen(
    huecos: List<HomeItem> = emptyList(),
    onReportClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF7F7F7))
    ) {
        LazyColumn(
            contentPadding = PaddingValues(top = 8.dp, bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(huecos) { item ->
                HomeCard(item)
            }
        }

        FloatingActionButton(
            onClick = onReportClick,
            containerColor = Color(0xFFFFD000),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .navigationBarsPadding()
        ) {
            Icon(
                Icons.Default.Add,
                contentDescription = "Reportar hueco",
                tint = Color.Black
            )
        }
    }
}

@Preview(showBackground = true, name = "HomeScreen with data")
@Composable
fun HomeScreenPreview() {
    val huecos = listOf(
        HomeItem(1, "Hueco en la 10", "Hueco grande y peligroso", "Pendiente", "2024-05-20", "Reportado"),
        HomeItem(2, "Hueco en la 20", "Hueco mediano", "En reparación", "2024-05-19", "Siguiendo"),
        HomeItem(3, "Hueco en la 30", "Hueco pequeño", "Arreglado", "2024-05-18", "Reincidente")
    )
    HomeScreen(huecos = huecos)
}

package com.fredrueda.huecoapp.feature.home.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HomeDrawerContent(
    selectedRoute: String,
    onItemClick: (DrawerItem) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(vertical = 24.dp)
    ) {
        Text(
            text = "HuecoApp",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFFFD000),
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 24.dp)
        )

        Divider(thickness = 1.dp, color = Color(0xFFE0E0E0))

        drawerItems.forEach { item ->
            NavigationDrawerItem(
                label = {
                    Text(
                        text = item.title,
                        fontSize = 16.sp,
                        fontWeight = if (item.route == selectedRoute) FontWeight.Bold else FontWeight.Normal
                    )
                },
                selected = item.route == selectedRoute,
                onClick = { onItemClick(item) },
                icon = {
                    Icon(
                        item.icon,
                        contentDescription = item.title,
                        tint = if (item.route == selectedRoute) Color(0xFFFFD000) else Color.Black
                    )
                },
                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
            )
        }
    }
}

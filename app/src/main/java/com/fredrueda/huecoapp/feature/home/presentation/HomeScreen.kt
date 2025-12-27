package com.fredrueda.huecoapp.feature.home.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.fredrueda.huecoapp.feature.home.data.remote.dto.HuecoHomeDto
import com.fredrueda.huecoapp.feature.home.domain.mapper.toHomeItem

@OptIn(ExperimentalMaterialApi::class)
@Preview(showBackground = true)
@Composable
fun HomeScreen(
    huecos: List<HuecoHomeDto> = emptyList(),
    onItemClick: (HuecoHomeDto) -> Unit = {},
    isRefreshing: Boolean = false,
    isLoading: Boolean = false,
    isLoadingMore: Boolean = false,      // <- NUEVO
    onRefresh: () -> Unit = {},
    onLoadMore: () -> Unit = {},         // <- NUEVO
    modifier: Modifier = Modifier
) {
    val pullRefreshState = rememberPullRefreshState(
        refreshing = isRefreshing,
        onRefresh = onRefresh
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .pullRefresh(pullRefreshState)
    ) {

        // LOADING GRANDE AL INICIAR
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color(0xFFFFD000))
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(top = 8.dp, bottom = 80.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                itemsIndexed(huecos) { index, item ->
                    HomeCard(item = item.toHomeItem(), onClick = { onItemClick(item) })

                    if (index == huecos.lastIndex - 2 && !isLoadingMore) {
                        onLoadMore()
                    }
                }

                // Ítem de loading al final cuando se cargan más elementos
                if (isLoadingMore) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }
            }
        }

        PullRefreshIndicator(
            refreshing = isRefreshing,
            state = pullRefreshState,
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }
}

@Preview(showBackground = true, name = "HomeScreen with data")
@Composable
fun HomeScreenPreview() {
    HomeScreen(huecos = emptyList())
}

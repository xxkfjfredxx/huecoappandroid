package com.fredrueda.huecoapp.feature.home.presentation

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.fredrueda.huecoapp.feature.map.presentation.MapScreen
import com.fredrueda.huecoapp.feature.profile.presentation.ProfileScreen
import com.fredrueda.huecoapp.ui.components.DrawerWithMapHandling
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainHomeScreen(
    onLogout: () -> Unit = {},
    onNavigateToMap: () -> Unit = {},
    onNavigateToDetail: (Int) -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var selectedRoute by rememberSaveable { mutableStateOf("home") }
    val homeViewModel: HomeViewModel = hiltViewModel()
    val homeState by homeViewModel.state.collectAsState()
    LaunchedEffect(Unit) {
        homeViewModel.loadInitial()
    }

    DrawerWithMapHandling(
        selectedRoute = selectedRoute,
        drawerState = drawerState,
        onCloseDrawer = { drawerState.close() },
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.fillMaxWidth(0.75f),
                drawerContainerColor = Color.White
            ) {
                HomeDrawerContent(
                    selectedRoute = selectedRoute
                ) { item ->
                    selectedRoute = item.route
                    scope.launch { drawerState.close() }
                    if (item == DrawerItem.Logout) onLogout()
                }
            }
        }
    ) { innerPadding ->
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            when (selectedRoute) {
                                "home" -> "Mis reportes"
                                "map" -> "Mapa de huecos"
                                "profile" -> "Perfil"
                                else -> ""
                            },
                            fontSize = 20.sp
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                scope.launch {
                                    if (drawerState.isClosed) drawerState.open()
                                    else drawerState.close()
                                }
                            }
                        ) {
                            Icon(Icons.Default.Menu, contentDescription = "Abrir menú")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.White,
                        titleContentColor = Color.Black
                    )
                )
            },
            containerColor = Color(0xFFF7F7F7),
            floatingActionButton = {
                if (selectedRoute == "home") {
                    ExtendedFloatingActionButton(
                        onClick = onNavigateToMap,
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Reportar hueco",
                                tint = Color.Black
                            )
                        },
                        text = { Text("Reporta aquí") },
                        containerColor = Color(0xFFFFD000),
                        contentColor = Color.Black
                    )
                }

            }
        ) { innerScaffoldPadding ->

            Crossfade(targetState = selectedRoute, label = "transition") { route ->
                when (route) {
                    "home" -> {
                        val tabs = listOf("Mis reportes", "Seguidos")
                        val pagerState = rememberPagerState(
                            initialPage = 0,
                            pageCount = { tabs.size }
                        )

                        Column(
                            modifier = Modifier
                                .padding(innerScaffoldPadding)
                                .fillMaxSize()
                        ) {

                            TabRow(
                                selectedTabIndex = pagerState.currentPage,
                                containerColor = Color.White,
                                contentColor = Color.Black,
                                indicator = { tabPositions ->
                                    TabRowDefaults.Indicator(
                                        modifier = Modifier.tabIndicatorOffset(tabPositions[pagerState.currentPage]),
                                        color = Color(0xFFFFD000)
                                    )
                                }
                            ) {
                                tabs.forEachIndexed { index, title ->
                                    Tab(
                                        selected = pagerState.currentPage == index,
                                        onClick = {
                                            scope.launch {
                                                pagerState.animateScrollToPage(index)
                                            }
                                        },
                                        text = {
                                            Text(
                                                text = title,
                                                color = if (pagerState.currentPage == index) Color.Black else Color.Gray
                                            )
                                        }
                                    )
                                }
                            }

                            HorizontalPager(
                                state = pagerState,
                                modifier = Modifier.fillMaxSize()
                            ) { page ->
                                when (page) {
                                    0 -> {
                                        // TAB: MIS REPORTES
                                        HomeScreen(
                                            huecos = homeState.misReportes,
                                            onItemClick = {
                                                onNavigateToDetail(it.id)
                                            },
                                            isRefreshing = homeState.isRefreshing,
                                            isLoading = homeState.isLoading,
                                            isLoadingMore = homeState.isLoadingMoreMisReportes,
                                            onRefresh = { homeViewModel.refreshMisReportes() },
                                            onLoadMore = { homeViewModel.loadMoreMisReportes() },
                                            modifier = Modifier.fillMaxSize()
                                        )
                                    }

                                    1 -> {
                                        // TAB: SEGUIDOS
                                        HomeScreen(
                                            huecos = homeState.seguidos,
                                            onItemClick = {
                                                onNavigateToDetail(it.id)
                                            },
                                            isRefreshing = homeState.isRefreshing,
                                            isLoading = homeState.isLoading,
                                            isLoadingMore = homeState.isLoadingMoreSeguidos,
                                            onRefresh = { homeViewModel.refreshSeguidos() },
                                            onLoadMore = { homeViewModel.loadMoreSeguidos() },
                                            modifier = Modifier.fillMaxSize()
                                        )
                                    }
                                }
                            }
                        }
                    }

                    "map" -> MapScreen(
                        onNavigateToDetail = onNavigateToDetail,
                        modifier = Modifier
                            .padding(innerScaffoldPadding)
                            .fillMaxSize()
                    )

                    "profile" -> ProfileScreen(modifier = Modifier.padding(innerScaffoldPadding))
                }

            }
        }
    }
}

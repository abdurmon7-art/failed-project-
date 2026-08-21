package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.local.SearchHistoryEntity
import com.example.ui.components.DisclaimerDialog
import com.example.ui.components.ErrorCard
import com.example.ui.components.GamingTopBar
import com.example.ui.components.HeroBannerCard
import com.example.ui.components.HistoryBottomSheet
import com.example.ui.components.LoadingScreen
import com.example.ui.components.PlayerProfileView
import com.example.ui.components.SearchCard
import com.example.ui.theme.DarkBg
import com.example.ui.viewmodel.CheckerUiState
import com.example.ui.viewmodel.PlayerCheckerViewModel

@Composable
fun MainCheckerScreen(
    viewModel: PlayerCheckerViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val inputUid by viewModel.inputUid.collectAsState()
    val selectedServer by viewModel.selectedServer.collectAsState()
    val selectedTab by viewModel.selectedTab.collectAsState()
    val showHistorySheet by viewModel.showHistorySheet.collectAsState()
    val showDisclaimerDialog by viewModel.showDisclaimerDialog.collectAsState()
    val historyList by viewModel.searchHistory.collectAsState()
    val favoriteList by viewModel.favoriteHistory.collectAsState()

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBg)
            .testTag("main_checker_screen"),
        topBar = {
            GamingTopBar(
                onOpenHistory = { viewModel.setShowHistorySheet(true) },
                onOpenDisclaimer = { viewModel.setShowDisclaimerDialog(true) },
                historyCount = historyList.size
            )
        },
        containerColor = DarkBg
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.TopCenter
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .widthIn(max = 650.dp)
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(top = 12.dp, bottom = 32.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Tactical Hero Banner
                item {
                    HeroBannerCard()
                }

                // Search Input Card
                item {
                    val isLoading = uiState is CheckerUiState.Loading
                    SearchCard(
                        uidInput = inputUid,
                        onUidChange = { viewModel.onUidChange(it) },
                        selectedServer = selectedServer,
                        onServerChange = { viewModel.onServerChange(it) },
                        onSearch = { viewModel.searchPlayer() },
                        onClear = { viewModel.clearInput() },
                        onDemoSelect = { uid, serverCode ->
                            viewModel.setDemoUid(uid, serverCode)
                        },
                        isLoading = isLoading
                    )
                }

                // Dynamic State: Loading, Error, Success
                item {
                    when (val state = uiState) {
                        is CheckerUiState.Loading -> {
                            LoadingScreen(
                                uid = state.uid,
                                server = state.server,
                                statusMessage = state.statusMessage
                            )
                        }
                        is CheckerUiState.Error -> {
                            ErrorCard(
                                message = state.message,
                                uid = state.uid,
                                currentServer = state.server,
                                isNotFound = state.isNotFound,
                                onRetry = { viewModel.searchPlayer(state.uid, state.server) },
                                onServerSwitch = { newServer ->
                                    viewModel.onServerChange(newServer)
                                    viewModel.searchPlayer(state.uid, newServer)
                                }
                            )
                        }
                        is CheckerUiState.Success -> {
                            val isFavorited = favoriteList.any { it.uid == state.player.uid && it.server == state.player.server }
                            val existingEntity = historyList.find { it.uid == state.player.uid && it.server == state.player.server }

                            PlayerProfileView(
                                player = state.player,
                                selectedTab = selectedTab,
                                onTabSelected = { viewModel.setSelectedTab(it) },
                                isFavorite = isFavorited,
                                onToggleFavorite = {
                                    if (existingEntity != null) {
                                        viewModel.toggleFavorite(existingEntity)
                                    } else {
                                        val newEntity = SearchHistoryEntity(
                                            uid = state.player.uid,
                                            server = state.player.server,
                                            playerName = state.player.name,
                                            playerLevel = state.player.level,
                                            playerLikes = state.player.likes,
                                            isFavorite = true
                                        )
                                        viewModel.toggleFavorite(newEntity)
                                    }
                                },
                                onSearchAgain = {
                                    viewModel.clearInput()
                                }
                            )
                        }
                        is CheckerUiState.Idle -> {
                            // Empty state guidance
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }
        }
    }

    // Bottom Sheet for Recents & Bookmarks
    if (showHistorySheet) {
        HistoryBottomSheet(
            historyList = historyList,
            favoriteList = favoriteList,
            onSelectEntry = { entity ->
                viewModel.loadFromHistory(entity)
            },
            onToggleFavorite = { entity ->
                viewModel.toggleFavorite(entity)
            },
            onDeleteItem = { id ->
                viewModel.deleteHistoryItem(id)
            },
            onClearAll = {
                viewModel.clearAllHistory()
            },
            onDismiss = {
                viewModel.setShowHistorySheet(false)
            }
        )
    }

    // Info & Disclaimer Dialog
    if (showDisclaimerDialog) {
        DisclaimerDialog(
            onDismiss = { viewModel.setShowDisclaimerDialog(false) }
        )
    }
}

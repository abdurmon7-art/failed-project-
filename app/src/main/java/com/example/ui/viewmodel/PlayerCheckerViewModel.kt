package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.api.FreeFireRepository
import com.example.data.local.SearchHistoryEntity
import com.example.data.model.PlayerInfo
import com.example.data.model.ServerRegion
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface CheckerUiState {
    object Idle : CheckerUiState
    data class Loading(
        val uid: String,
        val server: ServerRegion,
        val statusMessage: String = "Connecting to Free Fire API servers..."
    ) : CheckerUiState
    data class Success(val player: PlayerInfo) : CheckerUiState
    data class Error(
        val message: String,
        val uid: String,
        val server: ServerRegion,
        val isNotFound: Boolean = false
    ) : CheckerUiState
}

class PlayerCheckerViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = FreeFireRepository(application)

    private val _uiState = MutableStateFlow<CheckerUiState>(CheckerUiState.Idle)
    val uiState: StateFlow<CheckerUiState> = _uiState.asStateFlow()

    private val _inputUid = MutableStateFlow("")
    val inputUid: StateFlow<String> = _inputUid.asStateFlow()

    private val _selectedServer = MutableStateFlow(ServerRegion.DEFAULT)
    val selectedServer: StateFlow<ServerRegion> = _selectedServer.asStateFlow()

    private val _selectedTab = MutableStateFlow(0)
    val selectedTab: StateFlow<Int> = _selectedTab.asStateFlow()

    private val _showHistorySheet = MutableStateFlow(false)
    val showHistorySheet: StateFlow<Boolean> = _showHistorySheet.asStateFlow()

    private val _showDisclaimerDialog = MutableStateFlow(false)
    val showDisclaimerDialog: StateFlow<Boolean> = _showDisclaimerDialog.asStateFlow()

    val searchHistory: StateFlow<List<SearchHistoryEntity>> = repository.allHistory
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val favoriteHistory: StateFlow<List<SearchHistoryEntity>> = repository.favoriteHistory
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private var searchJob: Job? = null

    fun onUidChange(newUid: String) {
        // Keep only digits and limit to reasonable length
        val filtered = newUid.filter { it.isDigit() }.take(14)
        _inputUid.value = filtered
    }

    fun onServerChange(server: ServerRegion) {
        _selectedServer.value = server
    }

    fun setSelectedTab(index: Int) {
        _selectedTab.value = index
    }

    fun setShowHistorySheet(show: Boolean) {
        _showHistorySheet.value = show
    }

    fun setShowDisclaimerDialog(show: Boolean) {
        _showDisclaimerDialog.value = show
    }

    fun clearInput() {
        _inputUid.value = ""
        _uiState.value = CheckerUiState.Idle
    }

    fun setDemoUid(uid: String, serverCode: String) {
        _inputUid.value = uid
        _selectedServer.value = ServerRegion.fromCode(serverCode)
        searchPlayer(uid, _selectedServer.value)
    }

    fun loadFromHistory(entity: SearchHistoryEntity) {
        _inputUid.value = entity.uid
        _selectedServer.value = ServerRegion.fromCode(entity.server)
        _showHistorySheet.value = false
        searchPlayer(entity.uid, _selectedServer.value)
    }

    fun searchPlayer(
        uidOverride: String? = null,
        serverOverride: ServerRegion? = null
    ) {
        val targetUid = (uidOverride ?: _inputUid.value).trim()
        val targetServer = serverOverride ?: _selectedServer.value

        if (targetUid.isBlank()) {
            _uiState.value = CheckerUiState.Error(
                message = "Please enter a valid numeric Free Fire UID (e.g. 123456789).",
                uid = targetUid,
                server = targetServer
            )
            return
        }

        if (targetUid.length < 6) {
            _uiState.value = CheckerUiState.Error(
                message = "Free Fire UIDs are typically 8 to 11 digits long. Please check your UID.",
                uid = targetUid,
                server = targetServer
            )
            return
        }

        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            _uiState.value = CheckerUiState.Loading(
                uid = targetUid,
                server = targetServer,
                statusMessage = "Connecting to Free Fire API servers..."
            )

            // Dynamic progress messages to indicate responsive background activity
            val progressMessagesJob = launch {
                delay(1200)
                if (_uiState.value is CheckerUiState.Loading) {
                    _uiState.value = CheckerUiState.Loading(
                        uid = targetUid,
                        server = targetServer,
                        statusMessage = "Querying ${targetServer.name} region database..."
                    )
                }
                delay(2200)
                if (_uiState.value is CheckerUiState.Loading) {
                    _uiState.value = CheckerUiState.Loading(
                        uid = targetUid,
                        server = targetServer,
                        statusMessage = "Waking up API worker instance & parsing profile..."
                    )
                }
            }

            val result = repository.fetchPlayerInfo(targetUid, targetServer.code)
            progressMessagesJob.cancel()

            result.fold(
                onSuccess = { player ->
                    _selectedTab.value = 0
                    _uiState.value = CheckerUiState.Success(player)
                },
                onFailure = { error ->
                    val errorMsg = error.message ?: "Failed to get player info."
                    val isNotFound = errorMsg.contains("not found", ignoreCase = true)
                    _uiState.value = CheckerUiState.Error(
                        message = errorMsg,
                        uid = targetUid,
                        server = targetServer,
                        isNotFound = isNotFound
                    )
                }
            )
        }
    }

    fun toggleFavorite(entity: SearchHistoryEntity) {
        viewModelScope.launch {
            repository.toggleFavorite(entity)
        }
    }

    fun deleteHistoryItem(id: Long) {
        viewModelScope.launch {
            repository.deleteHistory(id)
        }
    }

    fun clearAllHistory() {
        viewModelScope.launch {
            repository.clearHistory(onlyNonFavorites = false)
        }
    }
}

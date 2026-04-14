package com.kyrics.demo.presentation.dualsync

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kyrics.demo.data.datasource.DemoLanguage
import com.kyrics.demo.data.datasource.DualSyncDataSource
import com.kyrics.demo.presentation.shared.PlaybackController
import com.kyrics.dualsync.DualSyncController
import com.kyrics.dualsync.model.DualTrackLyrics
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DualSyncViewModel
    @Inject
    constructor(
        private val dataSource: DualSyncDataSource,
    ) : ViewModel() {
        private val playback = PlaybackController(DualSyncDataSource.TOTAL_DURATION_MS, viewModelScope)

        private val _state =
            MutableStateFlow(DualSyncUiState(totalDurationMs = DualSyncDataSource.TOTAL_DURATION_MS))
        val state: StateFlow<DualSyncUiState> = _state.asStateFlow()

        private var syncJob: Job? = null

        init {
            rebuildController()
            observePlayback()
        }

        private fun observePlayback() {
            viewModelScope.launch {
                playback.isPlaying.collect { isPlaying ->
                    _state.update { it.copy(isPlaying = isPlaying) }
                }
            }
            viewModelScope.launch {
                playback.currentTimeMs.collect { timeMs ->
                    _state.update { it.copy(currentTimeMs = timeMs) }
                }
            }
        }

        fun onIntent(intent: DualSyncIntent) {
            when (intent) {
                is DualSyncIntent.TogglePlayPause -> playback.togglePlayPause()
                is DualSyncIntent.Reset -> playback.reset()
                is DualSyncIntent.ToggleSecondary -> toggleSecondary()
                is DualSyncIntent.SetPrimaryLanguage -> setPrimaryLanguage(intent.language)
                is DualSyncIntent.SetSecondaryLanguage -> setSecondaryLanguage(intent.language)
                is DualSyncIntent.SwapLanguages -> swapLanguages()
            }
        }

        private fun rebuildController() {
            syncJob?.cancel()
            val current = _state.value
            val lyrics =
                DualTrackLyrics(
                    primary = dataSource.getTrack(current.primaryLanguage),
                    secondary = dataSource.getTrack(current.secondaryLanguage),
                )
            val controller =
                DualSyncController(
                    lyrics = lyrics,
                    positionMs = playback.currentTimeMs,
                    scope = viewModelScope,
                )
            syncJob =
                viewModelScope.launch {
                    controller.state.collect { syncState ->
                        _state.update { it.copy(syncState = syncState) }
                    }
                }
        }

        private fun setPrimaryLanguage(language: DemoLanguage) {
            if (language == _state.value.primaryLanguage) return
            _state.update { it.copy(primaryLanguage = language) }
            rebuildController()
        }

        private fun setSecondaryLanguage(language: DemoLanguage) {
            if (language == _state.value.secondaryLanguage) return
            _state.update { it.copy(secondaryLanguage = language) }
            rebuildController()
        }

        private fun swapLanguages() {
            val current = _state.value
            _state.update {
                it.copy(
                    primaryLanguage = current.secondaryLanguage,
                    secondaryLanguage = current.primaryLanguage,
                )
            }
            rebuildController()
        }

        private fun toggleSecondary() {
            _state.update { it.copy(showSecondary = !it.showSecondary) }
        }
    }

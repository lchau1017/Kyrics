package com.kyrics.demo.presentation.viewmodel

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kyrics.demo.domain.dispatcher.DispatcherProvider
import com.kyrics.demo.domain.model.DemoSettings
import com.kyrics.demo.domain.model.LyricsSource
import com.kyrics.demo.domain.model.Preset
import com.kyrics.demo.domain.usecase.GetDemoSettingsUseCase
import com.kyrics.demo.domain.usecase.GetLyricsUseCase
import com.kyrics.demo.domain.usecase.UpdateDemoSettingsUseCase
import com.kyrics.demo.presentation.mapper.DemoUiMapper
import com.kyrics.demo.presentation.model.ColorPickerTarget
import com.kyrics.demo.presentation.model.DemoUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DemoViewModel
    @Inject
    constructor(
        private val getDemoSettingsUseCase: GetDemoSettingsUseCase,
        private val updateDemoSettingsUseCase: UpdateDemoSettingsUseCase,
        private val getLyricsUseCase: GetLyricsUseCase,
        private val dispatcherProvider: DispatcherProvider,
        private val uiMapper: DemoUiMapper,
    ) : ViewModel() {
        private val _state = MutableStateFlow(createInitialState())
        val state: StateFlow<DemoUiState> = _state.asStateFlow()

        private val _effect = Channel<DemoEffect>(Channel.BUFFERED)
        val effect = _effect.receiveAsFlow()

        private var playbackJob: Job? = null

        init {
            loadInitialData()
            observeSettings()
        }

        private fun createInitialState(): DemoUiState =
            DemoUiState.Initial.copy(
                viewerTypeOptions = uiMapper.mapViewerTypeOptions(),
            )

        private fun loadInitialData() {
            viewModelScope.launch(dispatcherProvider.io) {
                val lyricsData = getLyricsUseCase(LyricsSource.TTML)
                _state.update { currentState ->
                    uiMapper.mapLyricsToUiState(lyricsData, currentState)
                }
            }
        }

        private fun loadLyrics(source: LyricsSource) {
            viewModelScope.launch(dispatcherProvider.io) {
                val lyricsData = getLyricsUseCase(source)
                _state.update { currentState ->
                    uiMapper.mapLyricsToUiState(lyricsData, currentState).copy(
                        lyricsSource = source,
                        currentTimeMs = 0L,
                        selectedLineIndex = 0,
                    )
                }
            }
        }

        private fun observeSettings() {
            viewModelScope.launch(dispatcherProvider.main) {
                getDemoSettingsUseCase().collect { settings ->
                    _state.update { currentState ->
                        uiMapper.mapSettingsToUiState(settings, currentState)
                    }
                }
            }
        }

        fun onIntent(intent: DemoIntent) {
            when (intent) {
                is DemoIntent.Playback -> handlePlayback(intent)
                is DemoIntent.Selection -> handleSelection(intent)
                is DemoIntent.ColorPicker -> handleColorPicker(intent)
                is DemoIntent.Font -> handleFont(intent)
                is DemoIntent.Layout -> handleLayout(intent)
                is DemoIntent.VisualEffect -> handleVisualEffect(intent)
                is DemoIntent.Animation -> handleAnimation(intent)
                is DemoIntent.LoadPreset -> loadPreset(intent.preset)
                is DemoIntent.SelectLyricsSource -> selectLyricsSource(intent.source)
            }
        }

        private fun selectLyricsSource(source: LyricsSource) {
            stopPlaybackTimer()
            _state.update { it.copy(isPlaying = false) }
            loadLyrics(source)
        }

        // ==================== Intent Handlers ====================

        private fun handlePlayback(intent: DemoIntent.Playback) {
            when (intent) {
                is DemoIntent.Playback.TogglePlayPause -> togglePlayPause()
                is DemoIntent.Playback.Reset -> reset()
                is DemoIntent.Playback.Seek -> seek(intent.timeMs)
                is DemoIntent.Playback.UpdateTime -> updateTime(intent.timeMs)
            }
        }

        private fun handleSelection(intent: DemoIntent.Selection) {
            when (intent) {
                is DemoIntent.Selection.SelectLine -> selectLine(intent.index)
                is DemoIntent.Selection.SelectViewerType -> updateViewerType(intent.index)
            }
        }

        private fun handleColorPicker(intent: DemoIntent.ColorPicker) {
            when (intent) {
                is DemoIntent.ColorPicker.Show -> showColorPicker(intent.target)
                is DemoIntent.ColorPicker.Dismiss -> dismissColorPicker()
                is DemoIntent.ColorPicker.UpdateColor -> updateColor(intent.target, intent.color)
            }
        }

        private fun handleFont(intent: DemoIntent.Font) {
            when (intent) {
                is DemoIntent.Font.UpdateSize -> updateFontSize(intent.size)
                is DemoIntent.Font.UpdateWeight -> updateFontWeight(intent.weight)
                is DemoIntent.Font.UpdateFamily -> updateFontFamily(intent.family)
                is DemoIntent.Font.UpdateAlign -> updateTextAlign(intent.align)
            }
        }

        private fun handleLayout(intent: DemoIntent.Layout) {
            when (intent) {
                is DemoIntent.Layout.UpdateLineSpacing -> updateLineSpacing(intent.spacing)
            }
        }

        private fun handleVisualEffect(intent: DemoIntent.VisualEffect) {
            when (intent) {
                is DemoIntent.VisualEffect.ToggleGradient -> toggleGradient(intent.enabled)
                is DemoIntent.VisualEffect.UpdateGradientAngle -> updateGradientAngle(intent.angle)
                is DemoIntent.VisualEffect.ToggleBlur -> toggleBlur(intent.enabled)
                is DemoIntent.VisualEffect.UpdateBlurIntensity -> updateBlurIntensity(intent.intensity)
            }
        }

        private fun handleAnimation(intent: DemoIntent.Animation) {
            when (intent) {
                is DemoIntent.Animation.ToggleCharAnimation -> toggleCharAnimation(intent.enabled)
                is DemoIntent.Animation.UpdateCharMaxScale -> updateCharMaxScale(intent.scale)
                is DemoIntent.Animation.UpdateCharFloatOffset -> updateCharFloatOffset(intent.offset)
                is DemoIntent.Animation.UpdateCharRotation -> updateCharRotation(intent.degrees)
                is DemoIntent.Animation.ToggleLineAnimation -> toggleLineAnimation(intent.enabled)
                is DemoIntent.Animation.UpdateLineScaleOnPlay -> updateLineScaleOnPlay(intent.scale)
                is DemoIntent.Animation.TogglePulse -> togglePulse(intent.enabled)
                is DemoIntent.Animation.UpdatePulseMinScale -> updatePulseMinScale(intent.scale)
                is DemoIntent.Animation.UpdatePulseMaxScale -> updatePulseMaxScale(intent.scale)
            }
        }

        // ==================== Playback Logic ====================

        private fun togglePlayPause() {
            val newIsPlaying = !_state.value.isPlaying
            _state.update { it.copy(isPlaying = newIsPlaying) }

            if (newIsPlaying) {
                startPlaybackTimer()
            } else {
                stopPlaybackTimer()
            }
        }

        private fun startPlaybackTimer() {
            playbackJob?.cancel()
            playbackJob =
                viewModelScope.launch(dispatcherProvider.main) {
                    val totalDuration = _state.value.totalDurationMs
                    while (_state.value.isPlaying) {
                        delay(100)
                        val currentTime = _state.value.currentTimeMs
                        val newTime =
                            if (currentTime + 100 > totalDuration) {
                                0L
                            } else {
                                currentTime + 100
                            }
                        _state.update { it.copy(currentTimeMs = newTime) }
                        if (newTime == 0L) {
                            _state.update { it.copy(selectedLineIndex = 0) }
                        } else {
                            updateSelectedLineFromTime(newTime)
                        }
                    }
                }
        }

        private fun stopPlaybackTimer() {
            playbackJob?.cancel()
            playbackJob = null
        }

        private fun reset() {
            stopPlaybackTimer()
            _state.update {
                it.copy(
                    isPlaying = false,
                    currentTimeMs = 0L,
                    selectedLineIndex = 0,
                )
            }
        }

        private fun seek(timeMs: Long) {
            _state.update { it.copy(currentTimeMs = timeMs) }
            updateSelectedLineFromTime(timeMs)
        }

        private fun updateTime(timeMs: Long) {
            val totalDuration = _state.value.totalDurationMs
            val newTime = if (timeMs > totalDuration) 0L else timeMs
            _state.update { it.copy(currentTimeMs = newTime) }
            if (newTime == 0L) {
                _state.update { it.copy(selectedLineIndex = 0) }
            } else {
                updateSelectedLineFromTime(newTime)
            }
        }

        private fun updateSelectedLineFromTime(timeMs: Long) {
            val currentState = _state.value
            val currentLineIndex =
                currentState.demoLines.indexOfFirst { line ->
                    timeMs >= line.start && timeMs <= line.end
                }
            if (currentLineIndex >= 0) {
                _state.update { it.copy(selectedLineIndex = currentLineIndex) }
            }
        }

        private fun selectLine(index: Int) {
            _state.update { it.copy(selectedLineIndex = index) }
        }

        // ==================== Settings Updates ====================

        private fun updateSettings(transform: (DemoSettings) -> DemoSettings) {
            viewModelScope.launch(dispatcherProvider.main) {
                val currentSettings = uiMapper.mapUiStateToSettings(_state.value)
                val newSettings = transform(currentSettings)
                updateDemoSettingsUseCase(newSettings)
            }
        }

        private fun updateViewerType(index: Int) {
            updateSettings { it.copy(viewerTypeIndex = index) }
        }

        // Color picker
        private fun showColorPicker(target: ColorPickerTarget) {
            _state.update { it.copy(showColorPicker = target) }
        }

        private fun dismissColorPicker() {
            _state.update { it.copy(showColorPicker = null) }
        }

        private fun updateColor(
            target: ColorPickerTarget,
            color: Color,
        ) {
            updateSettings { settings ->
                when (target) {
                    ColorPickerTarget.SUNG_COLOR -> settings.copy(sungColor = color)
                    ColorPickerTarget.UNSUNG_COLOR -> settings.copy(unsungColor = color)
                    ColorPickerTarget.ACTIVE_COLOR -> settings.copy(activeColor = color)
                    ColorPickerTarget.BACKGROUND_COLOR -> settings.copy(backgroundColor = color)
                }
            }
            _state.update { it.copy(showColorPicker = null) }
        }

        // Font settings
        private fun updateFontSize(size: Float) {
            updateSettings { it.copy(fontSize = size) }
        }

        private fun updateFontWeight(weight: FontWeight) {
            updateSettings { it.copy(fontWeight = weight) }
        }

        private fun updateFontFamily(family: FontFamily) {
            updateSettings { it.copy(fontFamily = family) }
        }

        private fun updateTextAlign(align: TextAlign) {
            updateSettings { it.copy(textAlign = align) }
        }

        // Layout
        private fun updateLineSpacing(spacing: Float) {
            updateSettings { it.copy(lineSpacing = spacing) }
        }

        // Visual effects
        private fun toggleGradient(enabled: Boolean) {
            updateSettings { it.copy(gradientEnabled = enabled) }
        }

        private fun updateGradientAngle(angle: Float) {
            updateSettings { it.copy(gradientAngle = angle) }
        }

        private fun toggleBlur(enabled: Boolean) {
            updateSettings { it.copy(blurEnabled = enabled) }
        }

        private fun updateBlurIntensity(intensity: Float) {
            updateSettings { it.copy(blurIntensity = intensity) }
        }

        // Character animations
        private fun toggleCharAnimation(enabled: Boolean) {
            updateSettings { it.copy(charAnimEnabled = enabled) }
        }

        private fun updateCharMaxScale(scale: Float) {
            updateSettings { it.copy(charMaxScale = scale) }
        }

        private fun updateCharFloatOffset(offset: Float) {
            updateSettings { it.copy(charFloatOffset = offset) }
        }

        private fun updateCharRotation(degrees: Float) {
            updateSettings { it.copy(charRotationDegrees = degrees) }
        }

        // Line animations
        private fun toggleLineAnimation(enabled: Boolean) {
            updateSettings { it.copy(lineAnimEnabled = enabled) }
        }

        private fun updateLineScaleOnPlay(scale: Float) {
            updateSettings { it.copy(lineScaleOnPlay = scale) }
        }

        // Pulse
        private fun togglePulse(enabled: Boolean) {
            updateSettings { it.copy(pulseEnabled = enabled) }
        }

        private fun updatePulseMinScale(scale: Float) {
            updateSettings { it.copy(pulseMinScale = scale) }
        }

        private fun updatePulseMaxScale(scale: Float) {
            updateSettings { it.copy(pulseMaxScale = scale) }
        }

        // Presets
        private fun loadPreset(preset: Preset) {
            viewModelScope.launch(dispatcherProvider.main) {
                val config = preset.config
                val currentSettings = uiMapper.mapUiStateToSettings(_state.value)
                val newSettings =
                    currentSettings.copy(
                        fontSize = config.visual.fontSize.value,
                        fontWeight = config.visual.fontWeight,
                        sungColor = config.visual.playedTextColor,
                        unsungColor = config.visual.upcomingTextColor,
                        activeColor = config.visual.playingTextColor,
                        backgroundColor = config.visual.backgroundColor,
                        charAnimEnabled = config.animation.enableCharacterAnimations,
                        lineAnimEnabled = config.animation.enableLineAnimations,
                        pulseEnabled = config.animation.enablePulse,
                        pulseMinScale = config.animation.pulseMinScale,
                        pulseMaxScale = config.animation.pulseMaxScale,
                        gradientEnabled = config.visual.gradientEnabled,
                        blurEnabled = config.effects.enableBlur,
                        blurIntensity = config.effects.blurIntensity,
                    )
                updateDemoSettingsUseCase(newSettings)
                _effect.send(DemoEffect.PresetLoaded)
            }
        }
    }

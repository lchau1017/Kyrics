# Kyrics

A Jetpack Compose library for displaying synchronized karaoke-style lyrics with customizable animations and visual effects.

## Features

- **Synchronized Lyrics Display** - Character-by-character and syllable-by-syllable highlighting
- **Multiple Viewer Types** - 12 viewer styles including Smooth Scroll, Carousel 3D, Wave Flow, Spiral, and more
- **Rich Animations** - Character pop, float, rotation, and pulse effects
- **Customizable Gradients** - Progress-based, multi-color, and preset gradient options
- **Visual Effects** - Blur, shadows, and opacity transitions
- **Full DSL Support** - Type-safe Kotlin DSL for configuration, lyrics creation, and inline configuration
- **Extension Functions** - Rich collection utilities for working with synced lines
- **Compose-First** - Built entirely with Jetpack Compose

## Installation

### Option 1: JitPack (Recommended)

Add JitPack repository to your root `settings.gradle.kts`:

```kotlin
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}
```

Add the dependency to your module's `build.gradle.kts`:

```kotlin
dependencies {
    implementation("com.github.lchau1017:Kyrics:v1.0.0")
}
```

For the latest development version:
```kotlin
dependencies {
    implementation("com.github.lchau1017:Kyrics:main-SNAPSHOT")
}
```

### Option 2: Local Project Dependency

Clone the repository and include it in your project:

1. Clone the repo alongside your project:
```bash
git clone https://github.com/lchau1017/Kyrics.git
```

2. Add to your `settings.gradle.kts`:
```kotlin
includeBuild("../Kyrics") {
    dependencySubstitution {
        substitute(module("com.kyrics:kyrics")).using(project(":kyrics"))
    }
}
```

3. Add the dependency:
```kotlin
dependencies {
    implementation("com.kyrics:kyrics")
}
```

### Option 3: Copy Module

Copy the `kyrics` module directly into your project and add to `settings.gradle.kts`:

```kotlin
include(":kyrics")
```

Then add the dependency:
```kotlin
dependencies {
    implementation(project(":kyrics"))
}
```

## Quick Start

### Basic Usage

```kotlin
@Composable
fun KaraokeScreen(lyrics: List<SyncedLine>, currentTimeMs: Long) {
    KyricsViewer(
        lines = lyrics,
        currentTimeMs = currentTimeMs.toInt()
    )
}
```

### With Inline DSL Configuration

```kotlin
@Composable
fun KaraokeScreen(lyrics: List<SyncedLine>, currentTimeMs: Long) {
    KyricsViewer(
        lines = lyrics,
        currentTimeMs = currentTimeMs.toInt()
    ) {
        colors {
            playing = Color.Yellow
            sung = Color.Green
            unsung = Color.White
        }
        animations {
            characterAnimations = true
            characterScale = 1.2f
        }
        viewer {
            type = ViewerType.CAROUSEL_3D
        }
    }
}
```

### Creating Lyrics with DSL

```kotlin
val lyrics = kyricsLyrics {
    line(start = 0, end = 2000) {
        syllable("When ", start = 0, end = 200)
        syllable("the ", start = 200, end = 400)
        syllable("sun ", start = 400, end = 800)
        syllable("goes ", start = 800, end = 1200)
        syllable("down", start = 1200, end = 2000)
    }
    line(start = 2000, end = 4000) {
        syllable("And ", start = 2000, end = 2200)
        syllable("the ", start = 2200, end = 2400)
        syllable("stars ", start = 2400, end = 2800)
        syllable("come ", start = 2800, end = 3200)
        syllable("out", start = 3200, end = 4000)
    }
    // Accompaniment/background vocals
    accompaniment(start = 4000, end = 5000) {
        syllable("(ooh)", duration = 1000)
    }
}
```

### With Duration-Based Syllables

```kotlin
val lyrics = kyricsLyrics {
    line(start = 0, end = 2000) {
        syllable("Hel", duration = 200)
        syllable("lo ", duration = 300)
        syllable("World", duration = 500)
    }
}
```

### Factory Functions for Quick Creation

```kotlin
// Simple line from text (single syllable)
val line1 = kyricsLineFromText("Hello World", start = 0, end = 1000)

// Line from words (auto-split on whitespace)
val line2 = kyricsLineFromWords("Hello World", start = 0, end = 1000)

// Accompaniment line
val line3 = kyricsAccompaniment("(Background)", start = 0, end = 1000)
```

## Configuration

Kyrics uses a type-safe DSL for configuration:

```kotlin
val config = kyricsConfig {
    // Color settings
    colors {
        playing = Color.Yellow
        played = Color.Green
        upcoming = Color.White
        background = Color.Black
    }

    // Typography settings
    typography {
        fontSize = 28.sp
        fontWeight = FontWeight.Bold
        textAlign = TextAlign.Center
    }

    // Animation settings
    animations {
        characterAnimations = true
        characterDuration = 800f
        characterScale = 1.15f
        characterFloat = 6f

        lineAnimations = true
        lineScale = 1.05f

        pulse = true
        pulseMin = 0.98f
        pulseMax = 1.02f
    }

    // Visual effects
    effects {
        blur = true
        blurIntensity = 1.0f
    }

    // Gradient settings
    gradient {
        enabled = true
        angle = 45f
    }

    // Viewer type
    viewer {
        type = ViewerType.SMOOTH_SCROLL
    }

    // Layout settings
    layout {
        lineSpacing = 16.dp
    }
}
```

## Viewer Types

Kyrics includes 12 different viewer types:

| Viewer | Description |
|--------|-------------|
| `CENTER_FOCUSED` | Shows only the active line centered |
| `SMOOTH_SCROLL` | Standard vertical scrolling with smooth animations |
| `STACKED` | Z-layer overlapping effect with active line on top |
| `HORIZONTAL_PAGED` | Horizontal swipe transitions between lines |
| `WAVE_FLOW` | Sinusoidal motion pattern with wave-like effects |
| `SPIRAL` | Lines arranged in a spiral pattern |
| `CAROUSEL_3D` | 3D cylindrical carousel arrangement |
| `SPLIT_DUAL` | Shows current and next line simultaneously |
| `ELASTIC_BOUNCE` | Physics-based spring animations |
| `FADE_THROUGH` | Pure opacity transitions |
| `RADIAL_BURST` | Lines emerge from center in burst pattern |
| `FLIP_CARD` | 3D card flip transitions |

## Extension Functions

Rich collection utilities for working with synced lines:

```kotlin
// Find line at a specific time
val currentLine = lyrics.findLineAtTime(timeMs)
val currentIndex = lyrics.findLineIndexAtTime(timeMs)

// Navigation
val nextLine = lyrics.findNextLine(timeMs)
val prevLine = lyrics.findPreviousLine(timeMs)

// Progress calculations
val progress = line.progressAt(timeMs)  // 0.0 to 1.0
val overallProgress = lyrics.calculateOverallProgress(timeMs)

// Time utilities
val duration = line.duration
val totalDuration = lyrics.getTotalDuration()
val (start, end) = lyrics.getTimeRange()

// State checks
val isPlaying = line.containsTime(timeMs)
val hasPlayed = line.hasPlayedAt(timeMs)
val isUpcoming = line.isUpcomingAt(timeMs)

// Filtering
val playedLines = lyrics.getPlayedLines(timeMs)
val upcomingLines = lyrics.getUpcomingLines(timeMs)
val nearbyLines = lyrics.getLinesInRange(currentIndex, range = 3)

// KyricsLine specific
val mainVocals = kyricsLines.filterMainVocals()
val accompaniment = kyricsLines.filterAccompaniment()
val syllableCount = kyricsLines.getTotalSyllableCount()
```

## Data Models

### SyncedLine

Interface for basic synced lyrics:

```kotlin
interface SyncedLine {
    val start: Int  // Start time in milliseconds
    val end: Int    // End time in milliseconds
    fun getContent(): String
}
```

### KyricsLine

Full-featured line with syllable timing:

```kotlin
data class KyricsLine(
    val syllables: List<KyricsSyllable>,
    override val start: Int,
    override val end: Int,
    val metadata: Map<String, String> = emptyMap()
) : SyncedLine
```

### KyricsSyllable

Individual syllable with timing:

```kotlin
data class KyricsSyllable(
    val content: String,
    val start: Int,
    val end: Int
)
```

## State Management

Use `rememberKyricsStateHolder` for advanced state management:

```kotlin
// Basic usage
val stateHolder = rememberKyricsStateHolder(config)

// With initial lines
val stateHolder = rememberKyricsStateHolder(lyrics, config)

// With inline DSL
val stateHolder = rememberKyricsStateHolder {
    colors { playing = Color.Yellow }
    animations { characterScale = 1.2f }
}

// With lines and inline DSL
val stateHolder = rememberKyricsStateHolder(lyrics) {
    colors { playing = Color.Yellow }
}
```

## Architecture

The library follows clean architecture principles:

```
kyrics/
├── components/          # Composable UI components
│   └── viewers/         # Different viewer implementations
├── config/              # Configuration classes and DSL
├── models/              # Data models (SyncedLine, KyricsLine, etc.)
├── rendering/           # Text rendering and effects
│   ├── character/       # Character-level rendering
│   ├── layout/          # Text layout calculations
│   └── syllable/        # Syllable-level rendering
└── state/               # State management
```

## Demo App

The `kyrics-demo` module provides a fully-featured demo application showcasing all library capabilities. It follows clean architecture with MVI pattern:

- **Data Layer** - `DemoLyricsDataSource` using DSL for lyrics
- **Domain Layer** - Use cases and domain models
- **Presentation Layer** - MVI with `DemoViewModel`, `DemoIntent`, and `DemoUiState`

To run the demo:

```bash
./gradlew :kyrics-demo:installDebug
```

## Requirements

- **Min SDK**: 31 (Android 12)
- **Target SDK**: 35
- **Kotlin**: 2.1.0+
- **Jetpack Compose**: 2024.12.01 BOM

## License

```
Copyright 2024 Lung Chau

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## Code Quality

This project enforces code quality with:
- **ktlint** - Kotlin code style
- **detekt** - Static code analysis
- **Paparazzi** - Screenshot testing

Run checks with:
```bash
./gradlew ktlintCheck detekt test
```

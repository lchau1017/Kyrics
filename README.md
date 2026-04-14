# Kyrics

[![JitPack](https://jitpack.io/v/lchau1017/Kyrics.svg)](https://jitpack.io/#lchau1017/Kyrics)
[![API](https://img.shields.io/badge/API-31%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=31)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

A Jetpack Compose library for displaying synchronized karaoke-style lyrics with customizable visual styles.

<p align="center">
  <a href="#demo-app"><img src="media/demo_screenshot.png" alt="Kyrics Demo" width="250"/></a>
  &nbsp;
  <a href="#dualsync---dual-language-lyrics"><img src="media/dualsync_screenshot.png" alt="DualSync Demo" width="250"/></a>
  &nbsp;
  <a href="#word-tap---vocabulary-knowledge"><img src="media/wordtap_screenshot.png" alt="Word Tap Demo" width="250"/></a>
</p>
<p align="center">
  <a href="#demo-app"><b>Kyrics Demo</b></a>
  &nbsp;&nbsp;&nbsp;&nbsp;
  <a href="#dualsync---dual-language-lyrics"><b>DualSync Demo</b></a>
  &nbsp;&nbsp;&nbsp;&nbsp;
  <a href="#word-tap---vocabulary-knowledge"><b>Word Tap Demo</b></a>
</p>

---

## Features

- **TTML Lyrics Parser** - Parse TTML lyrics with syllable-level timing and automatic format detection
- **Synchronized Lyrics Display** - Character-by-character and syllable-by-syllable highlighting
- **DualSync** - Dual-language synchronized highlighting with independent timed tracks
- **Word Tap** - Tap vocabulary words in lyrics to see definitions, phonetics, and seek to context
- **Multi-Language Support** - 9 languages: English, Traditional Chinese, Japanese, Korean, French, Spanish, German, Portuguese, Italian
- **Syllable-Level Click Detection** - Canvas hit-testing for word-level tap interactions
- **2 Viewer Types** - Smooth Scroll and Fade Through
- **Customizable Gradients** - Progress-based, multi-color gradient options
- **Blur Effects** - Optional blur on non-playing lines for focus effect
- **Type-Safe DSL** - Kotlin DSL for configuration and lyrics creation
- **Single Import** - All APIs available from `com.kyrics` package
- **Compose-First** - Built entirely with Jetpack Compose

---

## Table of Contents

- [Installation](#installation)
- [Quick Start](#quick-start)
- [DualSync - Dual-Language Lyrics](#dualsync---dual-language-lyrics)
- [Word Tap - Vocabulary Knowledge](#word-tap---vocabulary-knowledge)
- [Parsing Lyrics](#parsing-lyrics)
- [Creating Lyrics](#creating-lyrics)
- [Configuration](#configuration)
- [Viewer Types](#viewer-types)
- [Data Models](#data-models)
- [State Management](#state-management)
- [Demo App](#demo-app)
- [Requirements](#requirements)
- [License](#license)

---

## Installation

### JitPack (Recommended)

**Step 1.** Add JitPack repository to your root `settings.gradle.kts`:

```kotlin
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}
```

**Step 2.** Add the dependency to your module's `build.gradle.kts`:

```kotlin
dependencies {
    implementation("com.github.lchau1017:Kyrics:v1.4.0")
}
```

### Alternative Installation Options

<details>
<summary>Local Project Dependency</summary>

Clone and include as a composite build:

```bash
git clone https://github.com/lchau1017/Kyrics.git
```

Add to `settings.gradle.kts`:

```kotlin
includeBuild("../Kyrics") {
    dependencySubstitution {
        substitute(module("com.kyrics:kyrics")).using(project(":kyrics"))
    }
}
```

Then add:

```kotlin
dependencies {
    implementation("com.kyrics:kyrics")
}
```

</details>

<details>
<summary>Copy Module</summary>

Copy the `kyrics` module into your project, add to `settings.gradle.kts`:

```kotlin
include(":kyrics")
```

Then add:

```kotlin
dependencies {
    implementation(project(":kyrics"))
}
```

</details>

---

## Quick Start

All Kyrics APIs are available from a single import:

```kotlin
import com.kyrics.*
```

### Basic Usage

```kotlin
@Composable
fun LyricsScreen(lyrics: List<KyricsLine>, currentTimeMs: Int) {
    KyricsViewer(
        lines = lyrics,
        currentTimeMs = currentTimeMs
    )
}
```

### With Configuration DSL

```kotlin
@Composable
fun LyricsScreen(lyrics: List<KyricsLine>, currentTimeMs: Int) {
    KyricsViewer(
        lines = lyrics,
        currentTimeMs = currentTimeMs
    ) {
        colors {
            playing = Color.Yellow
            sung = Color.Green
            unsung = Color.White
        }
        viewer {
            type = ViewerType.SMOOTH_SCROLL
        }
    }
}
```

---

## DualSync - Dual-Language Lyrics

Display two independently timed transcripts synchronized to the same audio. Both tracks use the full character-level canvas renderer for rich highlighting animations.

<p align="center">
  <img src="media/dualsync_screenshot.png" alt="DualSync Demo" width="300"/>
</p>

### Basic Usage

```kotlin
import com.kyrics.dualsync.*
import com.kyrics.dualsync.model.*

// Two tracks with independent timing, same audio clock
val lyrics = DualTrackLyrics(
    primary = englishLines,   // List<KyricsLine>
    secondary = chineseLines, // List<KyricsLine>
)

// Controller drives both tracks from a single position flow
val controller = rememberDualSyncController(
    lyrics = lyrics,
    positionMs = audioPositionFlow, // Flow<Long> of playback position in ms
)
val state by controller.state.collectAsState()

// Render both tracks
DualSyncLyricsView(
    state = state,
    showSecondary = true,
)
```

### Supported Languages

The demo app includes transcripts for 9 languages:

| Language | Script | Timing |
|----------|--------|--------|
| English | Latin | Word-level |
| Traditional Chinese | CJK | Line-level |
| Japanese | CJK + Kana | Line-level |
| Korean | Hangul | Line-level |
| French | Latin | Word-level |
| Spanish | Latin | Word-level |
| German | Latin | Word-level |
| Portuguese | Latin | Word-level |
| Italian | Latin | Word-level |

The library itself is language-agnostic. Any language that renders left-to-right works with the canvas renderer. See the demo app for a live example with switchable primary/secondary language pairs.

---

## Word Tap - Vocabulary Knowledge

Tap highlighted vocabulary words in lyrics to see their definition, phonetics, and context. Builds on the syllable-level click detection in `KyricsSingleLine`.

<p align="center">
  <img src="media/wordtap_screenshot.png" alt="Word Tap Demo" width="300"/>
</p>

### How It Works

The library provides syllable-level tap detection via `onSyllableClick` on `KyricsSingleLine` and `LyricsCanvas`. When a user taps a word, the canvas hit-tests the tap coordinates against syllable layout positions and returns the tapped `KyricsSyllable`.

```kotlin
import com.kyrics.components.KyricsSingleLine

KyricsSingleLine(
    line = line,
    lineUiState = lineState,
    currentTimeMs = currentTimeMs,
    config = config,
    onSyllableClick = { syllable, parentLine ->
        // syllable.content = the tapped word text
        // syllable.start / syllable.end = timestamps in ms
        // parentLine = the full KyricsLine for context
        showDefinition(syllable)
    },
)
```

The demo app uses this to show a bottom sheet with word definitions, and a vocabulary chip bar for seeking to specific words in the lyrics.

---

## Parsing Lyrics

Kyrics includes a TTML lyrics parser with syllable-level timing support.

### Usage

```kotlin
import com.kyrics.*

val content = loadLyricsFile() // Your TTML file content as String

when (val result = parseLyrics(content)) {
    is ParseResult.Success -> {
        val lines = result.lines        // List<KyricsLine>
        val duration = result.durationMs // Total duration (nullable)

        KyricsViewer(
            lines = lines,
            currentTimeMs = currentTime
        )
    }
    is ParseResult.Failure -> {
        Log.e("Lyrics", "Parse error: ${result.error}")
    }
}
```

### ParseResult

```kotlin
sealed class ParseResult {
    data class Success(
        val lines: List<KyricsLine>,
        val durationMs: Long? = null
    ) : ParseResult()

    data class Failure(
        val error: String,
        val lineNumber: Int? = null
    ) : ParseResult()
}
```

---

## Creating Lyrics

### Using DSL Builder

```kotlin
import com.kyrics.*

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
    // Background vocals
    accompaniment(start = 4000, end = 5000) {
        syllable("(ooh)", duration = 1000)
    }
}
```

### Duration-Based Syllables

```kotlin
val lyrics = kyricsLyrics {
    line(start = 0, end = 2000) {
        syllable("Hel", duration = 200)
        syllable("lo ", duration = 300)
        syllable("World", duration = 500)
    }
}
```

### Factory Functions

```kotlin
import com.kyrics.*

// Simple line (single syllable)
val line1 = kyricsLineFromText("Hello World", start = 0, end = 1000)

// Auto-split on whitespace
val line2 = kyricsLineFromWords("Hello World", start = 0, end = 1000)

// Accompaniment line
val line3 = kyricsAccompaniment("(Background)", start = 0, end = 1000)
```

---

## Configuration

### Full Configuration Example

```kotlin
import com.kyrics.*

val config = kyricsConfig {
    // Colors
    colors {
        playing = Color.Yellow
        played = Color.Green
        upcoming = Color.White
        background = Color.Black
    }

    // Typography
    typography {
        fontSize = 28.sp
        fontWeight = FontWeight.Bold
        textAlign = TextAlign.Center
    }

    // Gradient
    gradient {
        enabled = true
        angle = 45f
    }

    // Blur (non-playing lines)
    blur {
        enabled = true
        playedLineBlur = 2.dp
        upcomingLineBlur = 3.dp
        distantLineBlur = 5.dp
    }

    // Viewer type
    viewer {
        type = ViewerType.SMOOTH_SCROLL
    }

    // Layout
    layout {
        lineSpacing = 16.dp
    }
}
```

### Presets

```kotlin
// Use a preset configuration
KyricsViewer(
    lines = lyrics,
    currentTimeMs = currentTime,
    config = KyricsPresets.Classic
)

// Available presets
KyricsPresets.Classic  // Yellow/Green on black, bold
KyricsPresets.Neon     // Cyan/Magenta with gradient
```

---

## Viewer Types

| Viewer | Description |
|--------|-------------|
| `SMOOTH_SCROLL` | Standard vertical scrolling with smooth animations |
| `FADE_THROUGH` | Pure opacity transitions between lines |

---

## Data Models

All types are available from `com.kyrics.*`:

### KyricsLine

```kotlin
data class KyricsLine(
    val syllables: List<KyricsSyllable>,
    val start: Int,
    val end: Int,
    val isAccompaniment: Boolean = false
)
```

### KyricsSyllable

```kotlin
data class KyricsSyllable(
    val content: String,
    val start: Int,
    val end: Int
)
```

---

## State Management

```kotlin
import com.kyrics.*

// Basic
val stateHolder = rememberKyricsStateHolder(config)

// With initial lines
val stateHolder = rememberKyricsStateHolder(lyrics, config)

// With inline DSL
val stateHolder = rememberKyricsStateHolder {
    colors { playing = Color.Yellow }
    typography { fontSize = 28.sp }
}

// With lines and inline DSL
val stateHolder = rememberKyricsStateHolder(lyrics) {
    colors { playing = Color.Yellow }
}
```

---

## Demo App

The `kyrics-demo` module provides a complete demo showcasing library features with clean architecture (MVI pattern).

<p align="center">
  <img src="media/demo_screenshot.png" alt="Kyrics Demo" width="250"/>
  &nbsp;
  <img src="media/dualsync_screenshot.png" alt="DualSync Demo" width="250"/>
  &nbsp;
  <img src="media/wordtap_screenshot.png" alt="Word Tap Demo" width="250"/>
</p>

**Kyrics Demo:**
- Playback controls (play/pause, seek)
- Both viewer types
- Preset themes (Classic, Neon)
- Font size, weight, and family customization
- Gradient and blur effect toggles
- Real-time configuration changes

**DualSync Demo:**
- Dual-language synchronized lyrics with 9 languages
- Independent primary/secondary language selection
- Swap button to exchange languages
- Toggle to show/hide secondary track
- Fake audio playback for standalone testing

**Word Tap Demo:**
- Vocabulary words highlighted in green within lyrics
- Tap any green word to see definition, phonetics, and example
- Vocabulary chip bar for seeking to specific words
- Bottom sheet with word knowledge card
- Auto-pause on word tap

**Run the demo:**

```bash
./gradlew :kyrics-demo:installDebug
```

---

## Requirements

| Requirement | Version |
|-------------|---------|
| Min SDK | 31 (Android 12) |
| Target SDK | 35 |
| Kotlin | 2.1.0+ |
| Compose BOM | 2024.12.01 |

---

## Code Quality

```bash
./gradlew ktlintCheck detekt test
```

---

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

---

## License

```
Copyright 2026 Lung Chau

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

# Kyrics

A Jetpack Compose library for displaying synchronized karaoke-style lyrics with customizable animations and visual effects.

## Features

- **Synchronized Lyrics Display** - Character-by-character and syllable-by-syllable highlighting
- **Multiple Viewer Types** - Scrolling, Teleprompter, Spiral, Wave Flow, and Radial Burst viewers
- **Rich Animations** - Character pop, float, rotation, and pulse effects
- **Customizable Gradients** - Progress-based, multi-color, and preset gradient options
- **Visual Effects** - Blur, shadows, and opacity transitions
- **Type-Safe DSL** - Kotlin DSL for easy configuration
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
fun KaraokeScreen(lyrics: List<ISyncedLine>, currentTimeMs: Long) {
    val config = kyricsConfig {
        visual {
            fontSize = 24.sp
            sungColor = Color(0xFFFFD700)
            unsungColor = Color.White
        }
    }

    KyricsViewer(
        lines = lyrics,
        currentTimeMs = currentTimeMs.toInt(),
        config = config
    )
}
```

### With Syllable-Level Timing

```kotlin
val lyrics = listOf(
    KyricsLine(
        syllables = listOf(
            KyricsSyllable("Hel", start = 0, end = 300),
            KyricsSyllable("lo ", start = 300, end = 600),
            KyricsSyllable("World", start = 600, end = 1000)
        ),
        start = 0,
        end = 1000
    )
)

KyricsViewer(
    lines = lyrics,
    currentTimeMs = playerPosition,
    config = config
)
```

## Configuration

Kyrics uses a type-safe DSL for configuration:

```kotlin
val config = kyricsConfig {
    // Visual settings
    visual {
        fontSize = 28.sp
        fontWeight = FontWeight.Bold
        textAlign = TextAlign.Center

        // Colors
        sungColor = Color(0xFFFFD700)
        unsungColor = Color.White
        activeColor = Color(0xFFFFA500)

        // Gradients
        gradientType = GradientType.PRESET
        gradientPreset = GradientPreset.SUNSET
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

        shadows = true
        shadowColor = Color.Black.copy(alpha = 0.3f)

        playedOpacity = 0.25f
        upcomingOpacity = 0.6f
        visibleRange = 3
    }

    // Viewer type
    viewer {
        type = ViewerType.SCROLLING
        scrollBehavior = ScrollBehavior.SMOOTH
    }
}
```

## Viewer Types

### Scrolling (Default)
Standard vertical scrolling with the current line centered.

```kotlin
viewer {
    type = ViewerType.SCROLLING
    scrollBehavior = ScrollBehavior.SMOOTH
}
```

### Teleprompter
Lines flow from bottom to top, ideal for professional displays.

```kotlin
viewer {
    type = ViewerType.TELEPROMPTER
}
```

### Spiral
Lines arranged in a spiral pattern around the active line.

```kotlin
viewer {
    type = ViewerType.SPIRAL
}
```

### Wave Flow
Sinusoidal motion pattern with wave-like effects.

```kotlin
viewer {
    type = ViewerType.WAVE_FLOW
}
```

### Radial Burst
Lines emerge from the center in a burst pattern.

```kotlin
viewer {
    type = ViewerType.RADIAL_BURST
}
```

## Gradient Presets

Built-in gradient presets for quick styling:

- `GradientPreset.RAINBOW` - Full spectrum colors
- `GradientPreset.SUNSET` - Warm orange and pink tones
- `GradientPreset.OCEAN` - Cool blue tones
- `GradientPreset.FIRE` - Red to yellow gradient
- `GradientPreset.NEON` - Vibrant cyan, magenta, yellow

## Data Models

### ISyncedLine

Interface for basic synced lyrics:

```kotlin
interface ISyncedLine {
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
    val metadata: Map<String, Any> = emptyMap()
) : ISyncedLine
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

## Architecture

The library follows clean architecture principles:

```
kyrics/
├── components/          # Composable UI components
│   └── viewers/         # Different viewer implementations
├── config/              # Configuration classes and DSL
├── models/              # Data models (ISyncedLine, KyricsLine, etc.)
├── rendering/           # Text rendering and effects
│   ├── character/       # Character-level rendering
│   ├── layout/          # Text layout calculations
│   └── syllable/        # Syllable-level rendering
└── state/               # State management
```

## Demo App

The `kyrics-demo` module provides a fully-featured demo application showcasing all library capabilities with an MVI architecture pattern.

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

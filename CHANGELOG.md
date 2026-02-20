# Changelog

All notable changes to Kyrics will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.3.0] - 2026-02-20

### Changed
- **Simplified Config** - Removed `AnimationConfig` and `EffectsConfig`; hardcoded sensible defaults
  - `KyricsConfig` now contains only `VisualConfig` and `LayoutConfig`
  - Removed `animations {}` and `effects {}` from DSL builder
- **Simplified Viewer Types** - Reduced from 12 to 2 (`SMOOTH_SCROLL`, `FADE_THROUGH`)
  - Deleted 10 viewer implementations (Stacked, HorizontalPaged, WaveFlow, Spiral, Carousel3D, SplitDual, ElasticBounce, RadialBurst, FlipCard, CenterFocused)
- **Simplified Presets** - Reduced from 10 to 2 (`Classic`, `Neon`)
- **Renamed internal classes** - Dropped redundant prefixes on internal types
  - `KaraokeMath` -> `RenderMath`, `KaraokeDrawing` -> `TextDrawing`
  - `KaraokeLayout` -> `TextLayout`, `KaraokeCanvas` -> `LyricsCanvas`
  - `KyricsStateCalculator` -> `StateCalculator` (extracted to own file)
- **Removed unused code** - Deleted dead code across library and demo app
  - Removed `DemoEffect` side-effects system, unused mapper methods, unused config fields
  - Removed `GradientPreset` enum and `PRESET` gradient type

### Added
- **Blur Effects** - Opt-in blur on non-playing lines for focus effect
  - `blur {}` DSL block with `enabled`, `playedLineBlur`, `upcomingLineBlur`, `distantLineBlur`
  - Animated transitions via `animateFloatAsState`
  - Disabled by default in library, enabled by default in demo app
- **Demo app blur toggle** - Blur switch in Visual Effects settings panel

### Improved
- Consistent naming: public API uses `Kyrics` prefix, internals use descriptive names without prefix
- Extracted `StateCalculator` into separate file from `KyricsStateHolder`
- Demo app defaults: smaller font size (20sp) and zero line spacing for better multi-line visibility

## [1.2.0] - 2025-01-20

### Added
- **TTML Lyrics Parser** - Parse TTML lyrics with automatic format detection
  - Syllable-level timing with `<span>` elements
  - Word-level timing with iTunes-style `itunes:timing="Word"` attribute
  - Accompaniment/background vocal detection via `ttm:role="x-bg"`
  - `ParseResult` sealed class with `Success` and `Failure` states

### Fixed
- **Color Conversion Bug** - Fixed dark colors when applying presets
  - Changed `Color.value.toLong()` to `Color.toArgb().toLong()` for proper ARGB conversion

### Changed
- **Demo App Architecture** - Refactored to strict Clean Architecture with MVI pattern
  - Domain layer now uses pure Kotlin types (no Compose dependencies)
  - `DemoSettings` uses primitive types (Long for colors, Int for font weight, String for font family)
  - `DemoUiMapper` handles all Compose type conversions
  - Added `LyricsRepository` interface with `LyricsRepositoryImpl`

### Improved
- Demo app now loads lyrics from asset files using the parser
- Added comprehensive unit tests for parser components

## [1.1.1] - 2025-01-19

### Fixed
- **Exposed DSL Public API** - All DSL functions and types are now accessible from the `com.kyrics` package
  - Added type aliases for `SyncedLine`, `KyricsLine`, `KyricsSyllable`, `KyricsLineBuilder`, `KyricsLyricsBuilder`, `KyricsLineFactory`, `KyricsConfig`, `KyricsConfigBuilder`, `ViewerType`
  - Re-exported DSL functions: `kyricsConfig()`, `kyricsLine()`, `kyricsLyrics()`, `kyricsLineFromText()`, `kyricsLineFromWords()`, `kyricsAccompaniment()`
  - Client apps can now import everything from `com.kyrics` package without needing to import from `com.kyrics.models` or `com.kyrics.config`

## [1.1.0] - 2025-01-19

### Added
- **Full DSL Support for Lyrics Creation**
  - `kyricsLyrics {}` builder for creating lists of lyrics lines
  - `kyricsLine {}` builder for creating individual lines with syllables
  - Duration-based syllable timing (`syllable("text", duration = 200)`)
  - Explicit timing syllables (`syllable("text", start = 0, end = 200)`)
  - Accompaniment line support (`accompaniment {}` block)

- **Factory Functions for Quick Line Creation**
  - `kyricsLineFromText()` - Create single-syllable line from text
  - `kyricsLineFromWords()` - Create line with auto-split words
  - `kyricsAccompaniment()` - Create accompaniment/background vocal line
  - `KyricsLineFactory` object with equivalent methods

- **Inline DSL Configuration for KyricsViewer**
  - Trailing lambda support for `KyricsViewer` composable
  - Inline configuration without pre-creating config object
  - DSL overloads for `rememberKyricsStateHolder`

- **SyncedLine Extension Functions**
  - Time utilities: `containsTime()`, `duration`, `progressAt()`
  - State checks: `hasPlayedAt()`, `isUpcomingAt()`
  - Collection utilities: `findLineAtTime()`, `findNextLine()`, `findPreviousLine()`
  - Progress: `calculateOverallProgress()`, `getTotalDuration()`, `getTimeRange()`
  - Filtering: `getPlayedLines()`, `getUpcomingLines()`, `getLinesInRange()`
  - KyricsLine-specific: `filterAccompaniment()`, `filterMainVocals()`, `getTotalSyllableCount()`

### Changed
- **Renamed `ISyncedLine` to `SyncedLine`** - Follows idiomatic Kotlin naming conventions (no `I` prefix for interfaces)
- **Demo app refactored to Clean Architecture with MVI pattern**

## [1.0.0] - 2025-01-18

### Added
- Initial release of Kyrics library
- `KyricsViewer` composable for synchronized lyrics display
- Character-by-character and syllable-by-syllable highlighting
- Viewer types: Smooth Scroll and Fade Through
- Type-safe Kotlin DSL for configuration (`kyricsConfig {}`)
- 2 preset configurations (Classic, Neon)
- Gradient effects
- `SyncedLine` interface for custom implementations
- `KyricsLine` and `KyricsSyllable` data classes
- `KyricsStateHolder` for advanced state management
- Comprehensive test suite with Paparazzi screenshot tests
- Demo application

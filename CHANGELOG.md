# Changelog

All notable changes to Kyrics will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.2.0] - 2025-01-20

### Added
- **Multi-Format Lyrics Parser** - Parse lyrics from multiple file formats with automatic format detection
  - **TTML Parser** - Full support for Timed Text Markup Language (W3C standard)
    - Syllable-level timing with `<span>` elements
    - Word-level timing with iTunes-style `itunes:timing="Word"` attribute
    - Accompaniment/background vocal detection via `ttm:role="x-bg"`
    - Metadata extraction (title, artist, album, duration)
  - **LRC Parser** - Standard LRC lyrics format
    - Line-level timestamp parsing `[mm:ss.xx]`
    - Metadata tags support (`[ti:`, `[ar:`, `[al:`, etc.)
  - **Enhanced LRC Parser** - Extended LRC with word-level timing
    - Inline word timestamps `<mm:ss.xx>`
    - Compatible with karaoke software formats
  - **Automatic Format Detection** - `parseLyrics()` auto-detects format from content
  - **Factory Pattern** - `LyricsParserFactory` for explicit format selection
  - **ParseResult** - Sealed class with `Success` and `Failure` states

- **Preview Composables** - Android Studio preview support for visual testing
  - `PresetPreviews.kt` - Individual previews for all 10 presets with names
  - `ViewerTypePreviews.kt` - Individual previews for all 12 viewer types with names
  - Grid previews for easy comparison

### Fixed
- **Color Conversion Bug** - Fixed dark colors when applying presets
  - Changed `Color.value.toLong()` to `Color.toArgb().toLong()` for proper ARGB conversion
  - Affects `DemoViewModel`, `DemoUiMapper`, and related tests

### Changed
- **Demo App Architecture** - Refactored to strict Clean Architecture with MVI pattern
  - Domain layer now uses pure Kotlin types (no Compose dependencies)
  - `Preset` sealed class replaced with `PresetType` enum
  - `ViewerTypeOption` replaced with `ViewerTypeId` enum
  - `DemoSettings` uses primitive types (Long for colors, Int for font weight, String for font family)
  - `DemoUiMapper` handles all Compose type conversions
  - Added `LyricsRepository` interface with `LyricsRepositoryImpl`
  - Supports multiple lyrics sources (TTML, LRC, Enhanced LRC)

### Improved
- Demo app now loads lyrics from asset files using the new parser
- Added comprehensive unit tests for parser components
- Better separation of concerns in demo app

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
  - Metadata support for lines (alignment, custom metadata)

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
  - Data layer with `DemoLyricsDataSource` using DSL
  - Domain layer with use cases and domain models
  - Presentation layer with `DemoViewModel`, `DemoIntent`, `DemoEffect`, and `DemoUiState`
  - `DemoUiMapper` for domain-to-presentation mapping
  - Proper separation of concerns following Android best practices

### Improved
- Demo app now showcases DSL usage for lyrics creation
- Better code organization with clear layer separation
- Comprehensive unit tests for demo app components

## [1.0.0] - 2025-01-18

### Added
- Initial release of Kyrics library
- `KyricsViewer` composable for synchronized lyrics display
- Character-by-character and syllable-by-syllable highlighting
- Multiple viewer types:
  - Smooth Scroll (default)
  - Stacked
  - Carousel 3D
  - Wave Flow
  - Radial Burst
  - Fade Through
  - Split Dual
  - Center Focused
  - Horizontal Paged
  - Elastic Bounce
  - Flip Card
  - Spiral
- Type-safe Kotlin DSL for configuration (`kyricsConfig {}`)
- 10 preset configurations (Classic, Neon, Rainbow, Fire, Ocean, Retro, Minimal, Elegant, Party, Matrix)
- Gradient presets (Rainbow, Sunset, Ocean, Fire, Neon)
- Animation effects (character pop, float, rotation, pulse)
- Visual effects (blur, shadows, opacity transitions)
- `SyncedLine` interface for custom implementations
- `KyricsLine` and `KyricsSyllable` data classes
- `KyricsStateHolder` for advanced state management
- Comprehensive test suite with Paparazzi screenshot tests
- Demo application

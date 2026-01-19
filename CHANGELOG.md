# Changelog

All notable changes to Kyrics will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

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

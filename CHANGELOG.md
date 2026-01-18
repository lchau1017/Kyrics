# Changelog

All notable changes to Kyrics will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

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
- Type-safe Kotlin DSL for configuration
- 10 preset configurations (Classic, Neon, Rainbow, Fire, Ocean, Retro, Minimal, Elegant, Party, Matrix)
- Gradient presets (Rainbow, Sunset, Ocean, Fire, Neon)
- Animation effects (character pop, float, rotation, pulse)
- Visual effects (blur, shadows, opacity transitions)
- `ISyncedLine` interface for custom implementations
- `KyricsLine` and `KyricsSyllable` data classes
- Comprehensive test suite with Paparazzi screenshot tests
- Demo application

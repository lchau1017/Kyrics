# Contributing to Kyrics

Thank you for your interest in contributing to Kyrics! This document provides guidelines for contributing.

## Getting Started

1. Fork the repository
2. Clone your fork: `git clone https://github.com/YOUR_USERNAME/Kyrics.git`
3. Create a branch: `git checkout -b feature/your-feature-name`

## Development Setup

### Requirements
- Android Studio Ladybug or later
- JDK 17
- Android SDK 35

### Building
```bash
./gradlew build
```

### Running Tests
```bash
./gradlew test
```

### Code Quality Checks
```bash
./gradlew ktlintCheck detekt
```

## Code Style

This project uses:
- **ktlint** for Kotlin code formatting
- **detekt** for static analysis

Run `./gradlew ktlintFormat` to auto-fix formatting issues.

## Pull Request Process

1. Ensure all tests pass: `./gradlew test`
2. Ensure code quality checks pass: `./gradlew ktlintCheck detekt`
3. Update documentation if needed
4. Submit your PR with a clear description of changes

## Reporting Issues

When reporting issues, please include:
- Android version
- Device/emulator details
- Steps to reproduce
- Expected vs actual behavior
- Code snippets if applicable

## License

By contributing, you agree that your contributions will be licensed under the Apache License 2.0.

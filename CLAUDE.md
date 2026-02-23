# CLAUDE.md - DivKit Repository Guide

## Project Overview

DivKit is an open-source Server-Driven UI (SDUI) framework by Yandex. It renders platform-native views (Android, iOS, Web, Flutter) from JSON markup sent by a backend. The current version is defined in the root `version` file (currently 32.38.0).

**Repository:** Originally mirrored from Yandex's internal Arcadia monorepo (see `.piglet-meta.json`).

## Repository Structure

```
divkit/
├── client/                  # Platform client libraries
│   ├── android/             # Android (Kotlin, Gradle)
│   ├── ios/                 # iOS (Swift, SPM/CocoaPods)
│   ├── web/                 # Web (TypeScript/Svelte, Vite)
│   │   ├── divkit/          # Core web framework
│   │   ├── divkit-react/    # React wrapper
│   │   └── divkit-examples/ # 14 example projects
│   ├── flutter/             # Flutter (Dart)
│   │   ├── divkit/          # Core Flutter package
│   │   └── div_expressions_resolver/  # Native expression resolver plugin
│   └── multiplatform/       # Kotlin Multiplatform (KMP)
├── schema/                  # JSON Schema definitions (~187 files, JSON Draft-07)
├── api_generator/           # Python code generator (schema -> platform code)
├── json-builder/            # Server-side JSON building libraries
│   ├── typescript/          # TypeScript (@divkitframework/jsonbuilder)
│   ├── kotlin/              # Kotlin DSL (Gradle)
│   └── python/              # Python (pydivkit)
├── expression-api/          # Expression function/method signatures
├── test_data/               # Cross-platform test data (13 subdirectories)
├── tools/                   # Developer tools (hot reload, test runner)
├── site/                    # divkit.tech playground (Svelte + Express)
├── visual-editor/           # WYSIWYG layout editor (Svelte + Vite)
├── figma-plugin/            # Figma-to-DivKit converter
├── compat_data/             # Platform compatibility matrices
├── shared_data/             # Shared data (phone masks, etc.)
├── samples/                 # Cloud function examples
├── lessons_resources/       # Learning modules with JSON examples
└── version                  # Single source of version truth
```

## DivKit JSON Structure

A valid DivKit JSON has this structure:

```json
{
  "templates": { },
  "card": {
    "log_id": "card_id",
    "states": [
      {
        "state_id": 0,
        "div": { "type": "container", "items": [...] }
      }
    ]
  }
}
```

**Always verify element properties against the schema files in `schema/`.** Schema defines required properties, correct types, allowed enum values, and default values. Nested objects must conform to their respective schemas at every level.

## Schema

Location: `schema/` (~187 JSON files using JSON Schema Draft-07)

The schema is the single source of truth for the DivKit data format. All platform implementations generate code from it via the API generator. Key schema files:
- `div.json` - Base div element type definitions
- `div-data.json` - Top-level card data structure
- `div-base.json` - Common base properties shared across elements
- `div-container.json`, `div-text.json`, `div-image.json`, etc. - Element-specific schemas
- `div-action-*.json` - 30+ typed action schemas
- `common.json` - Shared type definitions
- `expressions.json` - Expression language schema

## API Generator

Location: `api_generator/` (Python)

Generates type-safe platform code from JSON schemas. Supports these target languages:
- **Swift** (iOS)
- **Kotlin** (Android)
- **Kotlin DSL** (json-builder)
- **TypeScript** (Web)
- **Python** (json-builder)
- **Dart** (Flutter)
- **Divan** (internal format)
- **Documentation**

Generator configs are `generator_config.json` files found in each platform's source directory. The generator uses hash checking for incremental builds.

Generated source files are typically placed in `generated_sources/` directories and should not be edited manually. On iOS, generated sources are excluded from SwiftFormat/SwiftLint.

## Platform Clients

### Android

**Location:** `client/android/`
**Language:** Kotlin
**Build System:** Gradle 9.1.0 with Groovy DSL
**Min SDK:** 21 | **Compile/Target SDK:** 35
**Kotlin:** Language version 1.8, JVM target 1.8

**Key modules** (34+ modules):
| Module | Purpose |
|--------|---------|
| `div` | Core DivKit rendering |
| `div-core` | Core abstractions and interfaces |
| `div-data` | Data models (generated from schema) |
| `div-evaluable` | Expression evaluation engine |
| `div-json` | JSON parsing |
| `div-states` | State management |
| `div-gesture` | Gesture handling |
| `div-video` / `div-video-m3` | Video playback |
| `div-lottie` / `div-rive` | Animation extensions |
| `div-markdown` / `div-svg` | Content extensions |
| `div-shimmer` / `div-shine` | Visual effects |
| `div-pinch-to-zoom` | Zoom interactions |
| `div-size-provider` | Size measurement |
| `div-storage` | Persistence |
| `div-network` | Network layer |
| `div-histogram` | Performance metrics |
| `divkit-demo-app` | Demo application |
| `sample` | Sample integration |
| `assertion` | Test assertion library |
| `lint-rules` | Custom lint rules |

**Build commands:**
```bash
cd client/android
./gradlew unitTests              # Run all unit tests
./gradlew :div:testDebugUnitTest # Run tests for a specific module
./gradlew assembleDemoDebug      # Build demo app (debug)
./gradlew assembleDemoRelease    # Build demo app (release)
./gradlew lint                   # Run lint checks
```

**Testing:** JUnit + Mockito + Robolectric. Test config in `div-tests.gradle`. Tests run with `maxParallelForks = 1` (Robolectric constraint), `maxHeapSize = 4g`. JaCoCo coverage enabled.

**API validation:** Uses `kotlinx.binary-compatibility-validator` for public API tracking. API dumps are in `api/` directories within each module.

**Shared Gradle scripts:**
- `div-common.gradle` - Common Android config
- `div-library.gradle` - Library module config
- `div-application.gradle` - Application module config
- `div-tests.gradle` - Test config (JUnit, Mockito, Robolectric)
- `div-tests-coverage.gradle` - JaCoCo coverage
- `publish-android.gradle` / `publish-java.gradle` / `publish-common.gradle` - Maven publishing
- `version.gradle` - Reads version from root `version` file

**Custom Gradle plugins:**
- `api-generator-plugin` - Integrates the Python API generator into Gradle builds
- `screenshot-test-plugin` - Screenshot/snapshot testing support

### iOS

**Location:** `client/ios/`
**Language:** Swift 5.9
**Build System:** Swift Package Manager (Package.swift) + Xcode
**Minimum Deployment:** iOS 13, macOS 10.15
**Compiler flags:** `-warnings-as-errors` (all warnings are errors)

**Key frameworks/targets:**
| Target | Purpose |
|--------|---------|
| `DivKit` | Core rendering framework |
| `DivKitExtensions` | Additional component extensions |
| `DivKitMarkdownExtension` | Markdown rendering support |
| `DivKitSVG` | SVG image support |
| `LayoutKit` | Layout engine |
| `LayoutKitInterface` | Layout engine protocol definitions |
| `Serialization` | JSON serialization |

**Dependencies:**
- `vgsl` (VGSL) >= 7.18.0 - Yandex's graphics/serialization library
- `swift-markdown` == 0.6.0 - Apple's Markdown library

**Testing:**
- `DivKitTests/` - Unit tests
- `DivKitSnapshotTests/` - Snapshot/visual regression tests
- `DivKitUITests/` - UI tests

**Code style:**
- **SwiftFormat** (`.swiftformat`): 2-space indent, max line width 100, `--swiftversion 5.9`
- **SwiftLint** (`.swiftlint.yml`): Whitelist-based rules, custom rules for safety checks
- Both exclude `generated_sources/` and `shared_data_generated_sources/`

**Playground apps:**
- `DivKitPlayground/` - Interactive DivKit testing app
- `LayoutKitPlayground/` - Layout engine testing app

### Web

**Location:** `client/web/divkit/`
**Language:** TypeScript + Svelte
**Build System:** Vite 5.4.21
**Version:** 32.38.0 (matches root)

**Key characteristics:**
- Zero runtime dependencies
- Multiple output formats: CJS, ESM, IIFE
- Browser targets: Chrome 67+, Safari 14+, Firefox 68+
- SSR target: Node.js 10+
- Expression parser built with Peggy

**Build commands:**
```bash
cd client/web/divkit
npm install
npm run build:prod        # Full production build (all formats)
npm run build:watch       # Development watch mode
npm run build:peggy       # Build expression parser
```

**Testing:**
```bash
npm run test:unit                # Vitest unit tests
npm run test:testplane           # Visual regression tests (Testplane/Hermione)
npm run test:testplane:update    # Update visual reference images
npm run perf:expressions         # Expression parser benchmark
```

**Linting:**
- ESLint with TypeScript + Svelte plugins
- 4-space indent, single quotes, semicolons, max line length 120
- No Prettier - formatting enforced by ESLint only
- Config: `.eslintrc.js` extends `.eslintrc.base.js`

**React wrapper** (`client/web/divkit-react/`): Lightweight React bindings using Webpack. React >= 16 as peer dependency.

### Flutter

**Location:** `client/flutter/divkit/`
**Language:** Dart
**Build System:** Flutter/pub
**Version:** 0.6.1-rc.1
**SDK constraints:** Dart >= 2.17.0, Flutter >= 3.0.1

**Key dependencies:** rxdart, equatable, petitparser, cached_network_image, flutter_svg

**Testing:** flutter_test + golden_toolkit for visual tests

**Expression resolver plugin** (`client/flutter/div_expressions_resolver/`): Platform-specific native plugin (Kotlin for Android, Swift for iOS) for expression evaluation.

### Kotlin Multiplatform (KMP)

**Location:** `client/multiplatform/`
**Language:** Kotlin
**Build System:** Gradle with Kotlin DSL
**Targets:** Android + iOS (via CocoaPods)

Shared code in `src/commonMain/`, platform-specific in `src/androidMain/` and `src/iosMain/`.

## JSON Builder Libraries

Server-side libraries for constructing DivKit JSON:

### TypeScript
**Location:** `json-builder/typescript/`
**Package:** `@divkitframework/jsonbuilder`

### Kotlin DSL
**Location:** `json-builder/kotlin/`
**Modules:** `core/`, `divan-dsl/`, `expression-dsl/`, `expression-dsl-generator/`, `legacy-json-builder/`, `lint-rules/`
**Build:** Gradle

### Python (PyDivKit)
**Location:** `json-builder/python/`
**Package:** `pydivkit`
**Build:** pyproject.toml, managed via `uv`

## Test Data

**Location:** `test_data/` - Cross-platform test fixtures shared by all client implementations.

| Directory | Purpose |
|-----------|---------|
| `expression_test_data/` | Expression evaluation tests |
| `integration_test_data/` | End-to-end tests |
| `interactive_snapshot_test_data/` | Interactive element snapshots |
| `parsing_test_data/` | JSON parsing validation |
| `perf_test_data/` | Performance benchmarks |
| `regression_test_data/` | Regression test cases |
| `snapshot_test_data/` | Visual regression data |
| `template_test_data/` | Template processing tests |
| `ui_test_data/` | UI rendering tests |
| `unit_test_data/` | Unit test fixtures |
| `samples/` | Sample layouts for 15+ component types |
| `test_schema/` | Schema test fixtures |
| `tutorials/` | Tutorial content |

## Development Conventions

### General
- The `version` file at the repository root is the single source of truth for version numbers. Android reads it via `version.gradle`, other platforms have their own version files.
- Generated code lives in `generated_sources/` directories - never edit these files manually.
- The `.mapping.json` file (7.8 MB) maps between internal Arcadia paths and open-source paths.

### Schema-First Development
When adding or modifying DivKit elements:
1. Update the JSON schema in `schema/`
2. Run the API generator to regenerate platform code
3. Implement rendering logic in each platform client
4. Add test data to `test_data/` for cross-platform validation

### DivKit JSON Conventions
- Don't wrap single elements in containers unnecessarily.
- Containers with `wrap_content` must not contain elements with `match_parent` along the main axis.
- Use unique identifiers for elements and actions.
- Use `actions` (array) instead of deprecated `action` (singular).
- Use templates for repeating elements.
- Use variables for dynamic values.

### Actions
Two styles for actions:
1. **Typed actions** (preferred): Use `"typed": { "$ref": "div-action-typed.json" }`
2. **URL actions**: Use `"url": "div-action://action_name?param=value"`

For state switching: `div-action://set_state?state_id=0/nested_state/selected`

### Code Style by Platform

| Platform | Indent | Max Line | Notes |
|----------|--------|----------|-------|
| Android (Kotlin) | 4 spaces | - | Kotlin 1.8 language level, allOpen for `@Mockable` |
| iOS (Swift) | 2 spaces | 100 | SwiftFormat + SwiftLint, warnings-as-errors |
| Web (TypeScript) | 4 spaces | 120 | ESLint only (no Prettier), single quotes |
| Flutter (Dart) | 2 spaces | - | flutter_lints |

### Testing Strategy
- **Unit tests**: Each platform has its own unit test framework
- **Snapshot/visual tests**: Screenshot comparison tests on Android, iOS, and Web
- **Cross-platform test data**: Shared JSON test fixtures in `test_data/` ensure consistent behavior
- **Expression tests**: Expression evaluation tested across all platforms with shared test data
- **API compatibility**: Android uses `binary-compatibility-validator` for public API stability

## Contribution Guidelines

External contributions require adopting the Yandex CLA (see `CONTRIBUTING.md`). Pull requests should include: `I hereby agree to the terms of the CLA available at: https://yandex.ru/legal/cla/?lang=en`

## Key Files Reference

| File | Purpose |
|------|---------|
| `version` | Project version (32.38.0) |
| `schema/*.json` | DivKit JSON Schema definitions |
| `AGENTS.md` | AI agent instructions for JSON authoring |
| `CONTRIBUTING.md` | Contribution guidelines and CLA |
| `CHANGELOG.md` | Detailed release history |
| `.piglet-meta.json` | Internal repo metadata |
| `.mapping.json` | Arcadia path mapping |
| `expression-api/translations.json` | Localized function documentation |

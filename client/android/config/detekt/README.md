# File length

`./gradlew detekt` limits production Kotlin files in Android libraries to 800
physical lines. Blank lines, comments and imports count; a final newline does
not add an extra line. Tests, generated sources, demo applications and build/test
tools are excluded from this rule. Other detekt rules retain their existing
scope and baselines.

The rule is built from the `detektRules` source set in the existing `build-logic`
build and loaded through `detektPlugins`. It reads only the current source files;
version-control tools and repository history are not required. Every file,
including new and renamed files, has the same limit.

The limit and path exclusions are configured in `file-length.yml`. Generated
source headers are also recognized by the rule. `JsonFieldResolver.kt` is an
explicit exception recorded in `parser-json/detekt-baseline.xml`: its uniform
field-resolution overloads stay together. This baseline also suppresses future
file-length violations for this file. Other files retain the 800-line limit.

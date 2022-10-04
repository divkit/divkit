## 10.0.0
## Android Client:
Additions and changes:
* Implemented alignments in `WrapLayout`
* Implemented radial gradient
* Implemented warnings on slider ticks overlap each other
* Implemented visibility transition support
* Allow patch multiple view with same id

## iOS Client:
Fixes:
* Fixed Lottie extension params parsing

## 9.0.0 (September 27, 2022)
## JSON Schema:
* Clarification of wrap container documentation
* Added radial gradient schema

## Android Client:
Additions and changes:
* Implemented `WrapLayout` - layout with transfer of elements to the next line if they don't fit in the previous one
* Implemented showing of rendering time in demo activity
* Improved snapshot tests
Fixes:
* Fixed concurrent modification of variables
* Fixed `tint_color` observing on element's rebind
* Fixed lottie resources providing for tests

## iOS Client:
* Added Swift Package Manager support
* Fixed concurrency issues in `DivStateManager` and `DivVariablesStorage`
* Improved `DivContainer` height calculation
* Improved snapshot tests

## Web Client:
Added:
* `radial_gradient` support


## 8.0.0 (September 20, 2022)
### Android Client:
Additions and changes:
* Added image change subscription
* Moved `observeTintColor` into `bind` method
* Refactored `DivBorderDrawer`
* Parsing patch from JSON
* Changed host tag
* Disabled bind on attach
* Implemented replacing of link or json when paste from buffer in demo-app
* Supported `true`/`false` literals into variables
* Bind `input` type for accessibility
Fixes:
* Fixed regression screen at release builds of demo-app
* Fixed image blinking on rebind
* Fixed text alignment in `input` when rtl enabled
* Fixed extensions reuse

### iOS Client:
Additions and changes:
* Added `true`/`false` values support in `set_variable` actions
* Added `wrap` mode in DivContainer
* Added interactive snapshot tests
* Improved public API
* Improved dark mode in DivKit Playground
* Updated bool values parsing in set_variable actions
Fixes:
* Fixed transition animations
* Fixed DivContainer error messages

### Web Client:
Additions and changes:
* BooleanInt props now accepts booleans too
* Implemented boolean values for boolean variables
* Reworked `container` layout
* Added package tests
* Downgraded `babel-preset-jest`, so it is possible to run divkit tests with an older version of the node.js
Fixes:
* Fixed zero-values in `action_animation`
* Fixed `grid` recalculation
* Fixed layout of `pager` child elements when their size is too small

### Kotlin JSON builder
Addition:
* Implemented flags for hash files


## 7.0.0 (September 13, 2022)
### Android Client:
Additions and changes:
* improve actions binding
* cover generator with tests
Fixes:
* use public gradle distribution url

### iOS Client:
* Added test data into DivKit Playground
* Added color themes support in DivKit Playground
* Added DivInput tests
* Improved public API
* Fixed DivInput keyboard behavior
* Fixed boolean values serialization

### Web Client:
Addition:
* `transform` is now supported for the `base` component


## 6.0.0 (September 6, 2022)
### Android Client:
Additions and changes:
* Supported rotation transformation
* Switched to new API Generator
Fixes:
* Fixed build configuration

### iOS Client:
* Added LottieExceptionHandler
* Added focus support in DivInput
* Renamed DivKit Demo to DivKit Playground
* Fixed DivData states transition
* Fixed visibility actions for transitioning blocks
* Fixed text alignment in DivInput
* Fixed camera initialization in DivKit Playground
* Improved parseDivData methods in DivKitComponents
* Improved Sample app

### Web Client:
* Updated `input` to sync with the schema, also fixed `text_color` and height
* Fixed the behavior of `transition_in` / `transition_out` (when to start and when not to start animation)
* Fixed the default value of `variable_trigger.mode`


## 5.0.0 (September 5, 2022)

### JSON Schema
Fixes:
* Minor fixes in documentation

### Android client
Changes and additions:
* Added code samples to sample app
* Added README
* Minor UI tweaks in Playground app

### iOS client
Changes and additions:
* Added sample app
* Supported `alignment`, `max_visible_lines` and `select_all_on_focus` for `div-input`
* Minor UI tweaks in Demo app
* Text selection in `div-input` can be cleared by tap to outside area

Fixes:
* Fixed scale animation with zero factor
* Fixed text selection in multiline inputs


### Web client
Changes and additions:
* Messages of expression parsing errors made more informative


## 4.0.0 (August 29, 2022)

### JSON Schema
Changes and additions:
* Added translations for property descriptions
* `max_lines` renamed to `max_visible_lines`

### Android client
Changes and additions:
* Redesigned demo activity in Playground app
* Removed unnecessary permissions for Playground app
* Removed theme setting from Playground app
* Updated Playground app icon
* Added sample app

Fixes:
* Fixed flickering during video loading

### iOS client
Changes and additions:
* `text_variable`, `highlight_color` and `keyboard_type` support in DivInput
* Demo app refactoring and redesign

### Web client
Fixes:
* Fixed `action_animation` on iOS

### Kotlin JSON builder
Fixes:
* Fixed serialization of overloaded template properties

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

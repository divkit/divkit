## 19.0.0

# Android Client:
* Added backgrounds for ranges of text
* Added cache for background spans
* Added implementation of wrapping list control to single item for accessibility
* Removed redundant layout steps in gallery

# iOS Client:
* Added custom for rive animations
* Added memoization for image placeholders

# Web Client:
Added:
* `mix` property for a custom class name

Fixed:
* Incorrect `container` spacing with `separators`


## 18.0.0

# JSON Schema:
* Added Min/max sizes in schema
* Added type=list to accessiblity
* Changed command format for timers

# Android Client:
* Added border support to div-customs
* Added accessibility hint and mode subscriptions
* Fixed state patches
* Fixed exta actions bind
* Fixed linear layout params
* Removed module com.yandex.div:div-core-views. Classes moved to com.yandex.div:div
* Removed Div2Context#div2Component. In order to update global variables use Div2Context#globalVariableController
* Log and Assert is internal now. Use DivKit class methods in oder to toggle logging and assertions

# iOS Client:
* Added backgrounds for ranges of text
* Optimize escaping in expression resolving
* Fixed getting preview from url starts with data:image/png;base64

# Web Client:
Added:
* `background` and `border` for the `text` ranges
* `baseline` vertical alignment

Fixed:
* Background redraw on variables change (with additional conditions)
* CSS names conflict on production build, now css classes are longer
* Artifacts near conrers, if both radius and stroke are used
* Updated `image` preview / placeholder logic
* Fixed `tint_mode` = `multiply`to better match Android logic


## 17.0.0

# JSON Schema:
* Added indicator items stretching mode

# Android Client:
* Added baseline alignment in wrap container
* Fixed patch applying
* Fixed crash on invalid patch field
* Fixed blur when radius is 0

# iOS Client:
* Fixed redrawing after blur radius changed
* Fixed slider failure.
* Fixed height for linear container
* Fixed line height in wrap container


## 16.0.0

# Android Client:
* Added baseline alignment in linear container

# iOS Client:
* Added blur and tint mode support
* Added baseline alignment in DivContainer
* Improved DivErrorsStorage


## 15.0.0

# JSON Schema:
* Added `div-timer`
* Added zero blur radius support
* Added `border` and `background` for text range

# Android Client:
* Align elements in wrap container rows by default
* Added generated method to apply patch
* Fixed expressions serialization
* Fixed shadows in DivGallery

# Web Client:
* Added separators support in DivContainer


## 14.0.0
# JSON Schema:
* Added `div-blur` and `div-blur-background`
* Added `baseline_alignment` to `div-container`

# Android Client:
* Added blur implementation
* Added extensions unbind for DivCustom
* Added static method for version name
* Added hyphenation support for DivText with custom ellipsis
* Fixed DivCustom behaviour in regression tests
* Fixed overlap DivContainer children size measure
* Fixed applying paths in Playground
* Fixed missing ellipses
* Open url if DemoActivity opened from intent

# iOS Client:
* Added image blur implementation
* Added separators support in DivContainer
* Fixed DivPager last page layout
* Fixed overlap DivContainer children size measure
* Fixed memory leak in PinchToZoomExtensionHandler
* Fixed screen capture for web preview

# Web Client:

Additions:
* Added `setTheme` instance method
* `filters` (only `blur` is supported so far) implemented for the `image` and image background

Fixed:
* `Enter` handling in multiline input

# TypeScript JSON Builder:
* Synchronized version number with DivKit version


## 13.0.0
## JSON Schema:
* Added `tint_mode` for images in `div-text`

## Android Client:
* Added circle shape
* Implemented Nine-patch background
* Implement separators in wrap container
* Implemented tint_mode for images in div-text
* Fixed applying patches in demo app
* Fixed escaping symbols processing in expressions 
* Fixed expression result convertion for string properties

## iOS Client:
* Updated Lottie dependency to 3.5.0
* Added Lottie prefetching
* Combined video and images prefetching
* Supported nested ternary operator
* Added rendering time in Playground
* Fixed pager state change behavior
* Fixed Lottie animation strarting
* Fixed escaping symbols processing in expressions
* Fixed animations for div-text

## Web Client:
* Added `circle` shape support
* Supported `no_animation` and `native` values for the `action_anmation`
* Supported `tint_mode`

## 12.0.0
## JSON Schema:
* Added Nine-patch background

## Android Client:
* Added separators in linear layout
* Fixed text style change after slider position change
* Fixed invalid view visibility on change state
* Fixed gallery pages position
* Fixed attaching pager indicator

## iOS Client:
* Added text gradient support
* Implemented Nine-patch background
* Fixed animation traits

## Web Client:
* Implemented Nine-patch background
* Supported for the `selected_actions` in `pager`
* Fixed color values convertion

## 11.0.0
## JSON Schema:
* Added `cross_spacing` support for gallery
* Added `tint_mode` for `div-image`
* Added separator in DivContainer
* Added circle shape to schema
* Updated description for `longtap_actions` and `doubletap_actions`

## Android Client:
Additions and changes:
* Implemented support external theme in DivContext
* Implemented `tint_mode` for DivImage
* Implemented `cross_spacing` for Gallery
* Implemented `wrap_content_constrained`
* Added divider between elements in container to schema
* Added avoid creating `DivBorderDrawer` if it will not used
* Added show warning on `wrap_content` container with `match_parent` child size
Fixes:
* Fixed list of expressions generating

## iOS Client:
* Added `cross_spacing` support in DivGallery.
* Added radial gradient support.
* Improved `match_parent` items behaviour in DivContainer and DivGallery.
* Fixed invisible items size.
* Fixed actions handling in Sample app.

## Web Client:
Additions:
* Support for the `wrap` layout mode in a `container`
* Support for the `cross_spacing` property in `gallery`
* `doubletap_actions` and `longtap_actions` are now supoorted in actionable components
* Support for the `appearance_animation` in `image`
* Support for the `restrict_parent_scroll` property in `tabs`, `gallery` and `pager`
* Root states are now properly supported
Changes and fixes:
* Updated the `wrap_content` logic, this would lead to a different layout in some corner-cases
* Boolean values from exressions are now correctly converted to the string `false` / `true` in the `text` block
* The layout of the `slider` has been changed to sync with other platforms, now the text offset will not change the size of the component. Also now there may be more than 20 ticks
* Updated color functions according to the latest changes. Now several of them would return the type `color` instead of `string` (rgb, rgba, setColorRed and others)
* Components will now correctly unregister when destroyed, so recreated elements will work properly (for example, actions to change the current element in the gallery, nested state elements and others)
* Fixed logic of expression processing Previously, if a string was considered an "expression", it was processed as it, otherwise it would be used as a simple string. But sometimes this check worked incorrectly and processed expressions as a string. This is now fixed. It also means that some strings will now require proper escaping, so be sure to check this in your project (previously,these strings "just worked" without escaping because they were not treated as expressions)
* Now elements with custom actions can be focused, as it was with simple URLs. This would result to better accessibility in such cases (these elements also accept the keyboard input!)
* Fixed the visibility of the `gallery` arrows on desktops in several cases
* Fixed a case where`transition_in`, `transition_out` and `transition_change` incorrectly discarded the `alpha` component property
* Component actions will now wait for the result of each of them. This means that you can create an array using 2 actions: one to change the state, and the second to change the created component inside the state

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

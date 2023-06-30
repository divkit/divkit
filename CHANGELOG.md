## 25.7.0

# Android Client:
* `compileSdkVersion` updated to 34
* Supported margins for linear container separators.

# iOS Client:
* Added Xcode 14.3 & Xcode 15 support.
* Fixed player pausing in VideoBlock.

# Web Client:
* Fixed update of the`gallery` with the `scroll_mode` = `paging` and `default_item` in rare cases

## 25.6.0

# JSON Schema:
* Fixed accessibility for `DivSelect`

# Android Client:
* Fixed `min_width` for horizontal container
* Fixed accessibility for `DivSelect`

# iOS Client:
* Fixed borders with rounded corners.
* Fixed `DivGallery` with `match_parent` items layout.

## 25.5.0

# Android Client:

* ExoPlayer version updated to `2.18.4`
* Supported preview image in DivVideo
* Fixed trigger rebind for DivData with the same DivDataTag

# iOS Client:

* Fixed disappear actions timer invalidation

## 25.4.1

# Android Client:

* Fixed video player engine disconnecting from view when any activity stopped

## 25.4.0

# Android Client:

* Added regex validator for `div-input`
* Added support of disappear actions
* Added support of text alignment to `div-input`
* `DivStorage` forwards IllegalStateExceptions to `LoadDataResult` now

# iOS Client:

* Supported resume, pause, fatal actions in `DivVideo`
* Supported disappearing actions

# Web Client:

Added:
* Supported `dict` variable type

## 25.3.0

# JSON Schema:

Additions:
* Added `text_alignment_` properties for `div-input`

# Android Client:

Additions:
* Added `disappear_actions`
* Added `DivCustomContainerViewAdapter` with `DivStatePath` propagation to implementations

Fixes:
* Fixed `DivSelect` line height
* Fixed permanent destroying video manager on view detach
* Fixed big corner radius drawing

# iOS Client:

Additions:
* Added `disappear_actions`
* Added `fixed_length` mask for `DivInput`
* Added `margins` for separators in `DivContainer`

# Web Client:

Added:
* New content alignments in the `container`: `space-between`, `space-around`, `space-evenly`
* `font_family` property is now supported in the different components. Keep in mind, for this property to work, you need to provide a `typefaceProvider`
* `margins` for a `container` separators are now supported

# Kotlin JSON Builder:

* Supported properties from scheme up to 25.2.0

## 25.2.0

# JSON Schema:

Additions:
* Added `disappear_actions`
* Added new variable type: `dict`
* Added new types of content-alignment: `space-between`, `space-around`, `space-evenly`
* Added new property of container separators: `margins`

# Android Client:

Additions:
* Supported multiple `RenderScript` contexts

Fixes:
* Fixed patch applying to pager
* Fixed animation of disappearing views
* Fixed black screen and state saving after detach in video-custom
* Fixed black screen at the end of playback after onPause
* Fixed `VariableController` potential memory leaks

# iOS Client:

Fixes:
* Fixed cursor offset in `DivInput`
* Deserialization optimization

# Web Client:

Additions:
* `disappear_actions` is now supported
* Added a new `video` component

## 25.1.0

# JSON Schema:

Additions:
* Added `pause_actions` and `fatal_actions` to `div-video`

Changes:
* Combined `stream` and `video` source types in `div-video`

# Android Client:

Additions:
* Supported `DivVideo`

Fixes & Changes:
* Fixed center alignment of elements with margins in containers
* Rolled back of `DivCustomContainerViewAdapter`
* Fixed double closing of `DivStorage` database on cards removing
* Extended API of `DivPlayer.Observer`
* Fixed array validation at JSON parsing

# iOS Client:

Additions:
* Supported `DivVideo`

# TypeScript JSON Builder:

Updates:
* Optimized `rewriteTemplateVersions`

## 25.0.0

# JSON Schema:

* Added `input` `valdator`
* Moved `input` mask's `raw_text_variable` into the base `mask`

# Android Client:

Fixes:

* Fixed crash at hardware accelerated bitmap animation
* Fixed binding indicator with pager within gallery items
* Fixed various DivStorage concurrency issues
* Fixed various currency input mask issues

# iOS Client:

* Removed metal, selecting feature flags
* Supported getValue functions
* Supported stretch parameter for DivImage

# Web Client:

Additions:
* Supported expressions in `tabs` title
* Added `currency` mask for the `input`

Fixes:
* Fixed `select` sizes, paddings and other size-related properties
* Fixed `select` `hint` show logic to match other platforms
* Fixed several cases with incorrect json processing (color is not a string, etc)
* Changed `actions` without `url` processing logic to match other platforms (previusly they was ignored as incorrect)
* Fixed `input` cursor logic for the Safari (for the iOS in partucular)

## 24.4.0

# Android Client:

Additions:
* Added overload for variable set functions
* Added `DivVideo` implementation
* Added `DivSelect` implementation
* Added currency mask
* Added `DivCustomContainerViewAdapter` with `DivStatePath` propagation to implementations
* Support `raw_text_variable` for masks

Fixes:
* Fixed generator creating optional fields with validators when not needed
* Fix cards feed size in demo-app
* Fixed image loading race condition

# iOS Client:

Additions:
* Added video block implementation

## 24.3.0

# JSON Schema:

Additions:
* Added `div-video.json` to schema
* Actualized supported features on Android

# Android Client:

Additions:
* Added the ability to stretch background images without saving aspect ratio
* Added `z-order` modes for `video-custom`

Fixes:
* Fixed redundant re-measurement in an overlapping container
* Fixed incorrect name of the `Linear Layout` class in accessibility mode
* Fixed deletion and insertion bugs in `fixed_length` input masks
* Added workaround for bug in `View.isLaidOut`

# iOS Client:

Additions:
* Implemented `select` component

# Web Client:

Additions:
* `extensions` support
* SizeProvider extension
* Lottie extension
* Implemented `select` component
* Implemented an `input` `mask` for a text with a fixed number of characters

Fixes:
* Fixed markup update after `url` change in the `action`
* Fixed size-related properties of `input` (`paddings`, `border_radius`, `letter_spacing` and others), previously they incorrectly depended on the `font_size` property

## 24.2.0

# JSON Schema:

Additions:
* Added stretch image scale type
* Added div-select.json to schema
* Added currency mask schema

Changes:
* Moved raw_text_variable to input mask
* Updated `platforms` field in schema

# Android Client:

Additions:
* Implemented wrap_content constrained size for gallery items
* Implemented stretch image scale type
* Implemented aspect-based height for vertical and horizontal containers

Fixes & Changes:
* Fixed base sizes for wrap_content constrained in linear container
* Skip paddings when content does not fit into container
* Allowed DivCustom childView creation from any thread
* Disabled incorrect child height warning when aspect-based size is defined
* Improved DivImage preview performance
* Fixed error counter breaking overlap container with match_parent height
* Fixed expression evaluation cache
* Fixed linear container accessibility

# iOS Client:
* Removed TemplateSupport module.

# Web Client:

Additions:
* `stretch` image scale type
* Ability to build custom DivKIt lib with a limited set of components and disabled expressions

Fixes:
* `no_scale` image scale type should now work properly

# Kotlin JSON Builder:

Changes:
* Changed Int to Long


## 24.1.0

# JSON Schema:

Fixes & Changes:
* Removed selection input schema
* Updated `platforms` field in schema

# Android Client:

Additions:
* Implemented aspect height in wrap container

Fixes & Changes:
* Made gallery scroll slower
* Removed observers in clearBinding method
* Fixed visibility animations on disappear
* Fixed triggering changes after variable declaration

# iOS Client:

Additions:
* Added aspect support for vertical wrap container

Fixes & Changes:
* Made solid and gradient background behaviors the same
* Made initializer for gradient failable
* Passed elements errors in DeserializationResult for templates
* Replaced fatalError with assertionFailure where possible
* Removed selection input implementation

# Web Client:

Fixes:
* The `aspect` property in the `container` should work better in combination with the `height` property

## 24.0.0

# JSON Schema:
* Added raw_text_variable for div_input
* Added new keyboard type

# Android Client:

Breaking changes:
* Changed Int to Long
* Inherit Div2View from FrameContainerLayout

Additions:
* Added new keyboard type
* Added getValue functions
* Added size_provider extension

Fixes & Changes:
* Replaced DivSnappyRecyclerView with DivRecyclerView + PagerSnapHelper
* Optimization AND expression
* Opt-in primary constructors of div models
* Fixed crash on set_state in trigger actions

# iOS Client:

Additions:
* Added new keyboard type

Fixes & Changes:
* Increased support version for iOS to 11
* Increased size of Integer
* Removed selection input method into DivInput
* Renamed base modules
* Fixed string arrays parsing
* Fixed DivGallery actions handling
* Fixed CFString parsing
* Fixed shimmer-view block reuse
* Fixed constrained logic in wrap container

# Web Client:

Breaking changes:
* The `setVariable` / `GetVariable` methods have been removed (these methods were deprecated long time ago before opensource, in web version 1.11.0)
* DivKit now uses `BigInt` internally if it is supported by the current platform. This also means that variable methods such as `subscribe` can now return `bigint` values instead of `number` (and accept such values in json). `integer` values in expressions are now limited to 64 bits instead of 32, and the minimum/maximum value for `integer` variables has been changed. Note that outside of expressions, DivKit restricts values to 32-bit integers, as was the case in previous versions. Also note that the js builtin methods, such as `JSON.parse`, return a simple `number`, so these values may lose accuracy outside the logic of DivKit. Make sure that your values are in the range `-2^53 — 2^53` or a custom json parsing function is used

# Kotlin JSON Builder:
* Added new keyboard type


## 23.7.0

# Android Client:
Additions:
* Added cache for expressions

Fixes:
* Fixed concurrent modification exception in div storage
* Fixed IndexOutOfBoundsException in DivGallery
* Fixed declaration of new local variables
* Fixed scroll view for demo-app

# iOS Client:
* Added overflow parameter support in actions for DivTabs
* Allowed zero duration

# Web Client:
* Removed missing template field warning

## 23.6.0

# Android Client:
Additions:
* Added format date function
* Added trigger logging

Fixes:
* Fixed image preview
* Fixed ripple animation duplication
* Fixed alignment in `DivGallery`

# iOS Client:
* Added `overflow` parameter support in `div-action` for `DivPager` navigation.
* Fixed constrained element size calculation inside `DivContainer`.
* Fixed `MetalImageView` images rendering.

# Web Client:
Additions:
* `toUrl` / `toColor` functions


## 23.5.0

# Android Client:

Additions:
* Sampling for div-blur
* Masks for div-input
* Cache for evaluated expressions

Fixes and changes:
* fixed image binding perfomance

# iOS Client:
* added `selection` input method into `DivInput`.
* added error level into `externalLogger`.
* fixed `DivImage` layout trait check.

# Web Client:
Additions:
* Functions for getting `datetime ` components
* Functions for getting value of a variable, if it exists. `getStringValue`, `getIntegerValue`, etc

Fixes and changes:
* `timers` ticks should now be more accurate and reliable. Time intervals would correct time error, there is also an extra tick before end the duration if the ticks doesn't trigger in time
* `visibility_action` actions will now evaluate expressions on the trigger, and not when the creating the component


## 23.4.0

# Android Client:
Added:
* Added `shimmer` extension
* Supported `aspect ratio` in `overlap container`
* Supported min/max sizes for containers with `wrap_content constrained` size

Fixes:
* Fixed `timers` in Div2View inside RecyclerView
* Fixed `ellipsize` containing image span
* Fixed `focus` when `accessibility` is turned on
* Fixed `transition animations` in `state layout`

# iOS Client:
Added:
* added `images` support for `ellipsis` in DivText

Fixes:
* fixed alignment inside wrap DivContainer
* fixed views reuse in DivContainer
* fixed concurrency issues in DivTriggersStorage
* fixed transition animations inside DivGallery/DivPager

# Web Client:
Added:
* `aspect` support for `container` (`aspect-ratio` css property support is required!)

Fixes:
* Updated missing function error text, to better match other platforms
* Fixed error in `gallery` after destroy
* Fixed element `wrap_content` sizes with `constrained` in `grid`

## 23.3.0

# Android Client:
* Fixed algorithm of `indicator` attach to specified `pager`
* Fixed accessibility type binding for `gallery`
* Fixed NPE in `DecodeBase64ImageTask`

# iOS Client:
* Fixed corners in `DivIndicator`
* Fixed wrapped `DivContainer` layout


## 23.2.0

# Android Client:
* Added support of `max_size` property
* Added support of `wrap_content` size with constraints
* Added border to indicator
* Added sharing non-local variables between contexts
* Fixed filters not being applied to image preview
* Fixed visibility change of gallery items

# iOS Client:
* Added SizeProviderExtensionHandler
* Added plus operator support for strings
* Fixed last tick action in DivTimer
* Fixed text selection in DivInput
* Fixed multithreading in DivVariableStorage

## 23.1.0

# Android Client:
* Added time interval functions
* Added support of shape properties to slider

# iOS Client:
* Added functions for DateTime
* Fixed `padStart`/`padEnd` functions

# Web Client:
Added:
* `indicator` can now be rendered in two ways, `placement` field is now supported
* `indicator` can now have a border inside every point, shape objects have been updated to support `background_color` and `border` properties. Several fields in `indicator` are now deprecated, including `space_between_centers`, `inactive_item_color`, `active_item_color`, `shape` and `active_item_size`. Shape objects support in `slider` was also updated

Fixed:
* `padStart` / `padEnd` now will work properly with an empty `pad_str` argument
* Default `width` `match_parent` should work more predictable in rare cases
* `separator` and `line_separator`  in `container` now will not block clicks and other events on component and it's descendants
* `constrained` size logic in rare cases


## 23.0.0

# JSON Schema:
* Added `div-shape-base` to `div-circle-shape`

# Android Client:
* Added support of timers
* Added `div-indicator` style based on `div-rounded-rectangle-shape`
* Added subscription to image preview changes
* Fixed border clipping in transition animations
* Fixed patch application to child of container with id

# iOS Client:
* Added `ShimmerImagePreviewExtension`
* Added `aspect` size support in `DivContainer`
* Added batch actions handling, `DivActionURLHandler.UpdateCardAction` refactoring

# Web Client:
Fixed:
* `image` preview size and filters support
* `gallery` arrows on the desktop should now appear faster

# TypeScript JSON Builder:
* Speed up object walking in several operations


## 22.0.0

# JSON Schema:
* Added `aspect` in div-container
* Added `background_color` and `stroke` in div-rounded-rectangle-shape

# Android Client:
* Added extension for Rive animations
* Added function to get time component from datetime
* Fixed layout of linear container children with match parent size
* Fixed background blur radius updating by variable

# iOS Client:
* Added border in DivIndicator
* Added minSize/maxSize for constrained elements
* Added CustomImagePreviewExtensionHandler
* Added errors view in DivKit Playground

# Web Client:
Fixes:
* Fixed `tint_color` update for Safari



## 21.1.0

# JSON Schema:
* updated translations

# Android Client:
* Fixed baseline alignment in linear container
* Fixed minor layout issues
* Fixed rare crash when animating not laid out view
* Clear warnings after setting new data to `Div2View`

# iOS Client:
* Added functions for time intervals
* Added `padStart`, `padEnd` functions
* Added stretching `DivIndicator` support
* Fixed temporary param in `set-state` action
* Fixed `DivSlider` thumbs
* Fixed `nowLocal` function

# Web Client:
Added:
* `min_size` / `max_size` for the `wrap_content` size
* Functions `padStart` / `padEnd`
* Functions for time intervals

Changed:
* Allow `wrap_content` size for the `galleries` on the main axis
* Fixed `timers` start with incorrect values



## 21.0.0

# JSON Schema:
* Added `timers` support

# Android Client:
* Removed warning about element's wrong size in case when it doesn't affect its parent size

# iOS Client:
* Added `timers` support

# Web Client:
* Added `timers` support
* Added customization of the link protocols that should be rendered as native link (`builtinProtocols`)


## 20.0.0

# Android Client:
* Updated gradle wrapper to version `7.6`
* Added tapjacking protection
* Added separation of `wrap_content` and `wrap_content constrained` logic

# Kotlin JSON Builder:
* Updated gradle wrapper to version `7.6`


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
* `customization` render property for a desktop `gallery`

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

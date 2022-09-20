## 8.0.0 (September 20, 2022)

Changes and additions:
* BooleanInt props now accepts booleans too
* Implemented boolean values for boolean variables
* Reworked `container` layout
* Added package tests
* Downgraded `babel-preset-jest`, so it is possible to run divkit tests with an older version of the node.js

Fixes:
* Fixed zero-values in `action_animation`
* Fixed `grid` recalculation
* Fixed layout of `pager` child elements when their size is too small


## 7.0.0 (September 13, 2022)

Addition:
* `transform` is now supported for the `base` component


## 6.0.0 (September 6, 2022)
Changes:
* Updated `input` to sync with the schema, also fixed `text_color` and height
* Fixed the behavior of `transition_in` / `transition_out` (when to start and when not to start animation)
* Fixed the default value of `variable_trigger.mode`


## 5.0.0 (September 5, 2022)

Changes and additions:
* Messages of expression parsing errors made more informative


## 4.0.0 (August 29, 2022)

* Fixed `action_animation` on iOS


## 3.0.0 (August 18, 2022)

Version now in sync with the Android and iOS.

Changes and additions:
* Changed `gallery` items alignment
* Changed variables' types processing in expressions
* Added additional functions for `Color` and `Url`
* Changed `wrap_content` / `match_parent` in several cases to better match the Android layout. Better `match_parent` inside `wrap_content` handling. Better `match_parent` inside `container` with `overlap`. Container now can have multiple `constrained` children.

Fixes:
* Fixed `gallery` inside other `gallery`
* `width` and `height` can now be zero
* Fixed extra unused css variables in html
* Fixed the case with string interpolation without closing bracket

## 1.12.0 (August 10, 2022)

Additions:

* Added support for animations on `visibility` change
* Added `action_animation` support
* Added `transition_change` support

Fixes and changes:

* Layout fixes for `state`, `gallery` and `container`
* Layout fixes for `image` with `wrap_content`
* Fixed an issue with `text` when correct `text` value changed to incorrect


## 1.11.1 (August 10, 2022)

* Fixed `theme` prop in `render`


## 1.11.0 (July 29, 2022)

Variables:

* New API: now variables are objects with methods to `subscribe`, `getValue`, `setValue`, etc
* Added `GlobalVariablesController`. One can now use same variables across multiple instances of DivKit, that keep their values in sync
* DEPRECATION: Old methods `getVariable` / `setVariable` is deprecated

Added:
* `input` component
* Experimental `palette` support


## 1.10.1 (July 14, 2022)

* Fixed `onCustomAction` types


## 1.10.0 (July 14, 2022)

* Added `slider` block
* Added support for `expressions` and `variable_triggers`
* Improved a11y for `indicator` and `pager`


## 1.9.0 (June 27, 2022)

* Added `onCustomAction` callback support
* Added `default_item` support for gallery
* Added `indicator` block
* Added `id` support for state (`div_id` was deprecated)
* Added `set_current_item`, `set_previous_item`, `set_next_item` actions support for pager
* Added `div-action://` and `custom action` support in visibility actions


## 1.8.0 (June 16, 2022)

New features:
* Added interpolation for `variables` in strings
* Added `visibility` support for base block (w/o animation)
* Added `selectable` property support for `text` block (and now text by default cannot be selected, to match Android / iOS behaviour)

Fixes:
* Fixes for `grid`'s children sizing
* Recursive templates now correctly produces error instead of infinity loop


## 1.7.0 (June 6, 2022)

* Added `variables` support


## 1.6.0 (June 6, 2022)

* Added `pager` block
* Added support for `switch_tabs_by_content_swipe_enabled` in tabs
* Added support for `tint_color` in image, gif-image, text


## 1.5.0 (May 24, 2022)

* Added support for `set_current_item`, `set_previous_item`, `set_next_item` actions in tabs and gallery


## 1.4.0 (May 16, 2022)

* Added support for `shadow` in base block
* Fixed build for older browsers


## 1.3.0 (May 5, 2022)

* Added support for `active_font_weight` / `inactive_font_weight` in tabs' style


## 1.2.0 (April 29, 2022)

* Added `state` block


## 1.1.0 (April 4, 2022)

* Added support for `corner_radius` / `corners_radius` in tabs' style


## 1.0.0 (March 22, 2022)

* Initial release

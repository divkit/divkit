## 28.11.0 (ADD DATE HERE)

* Fixed an issue with the `focus` property support in `slider` component.
* Added `focus_element` typed action support.


## 28.10.0 (November 13, 2023)

* Added `copy_to_clipboard` action support.
* Fixed cross-axis container items with the `match_parent` size logic in combination with the `wrap_content` size of the container itself.
* Fixed `gallery` scrolling with `wrap_content` and `max_size` property.


## 28.9.0 (November 7, 2023)

* Fixed the first/last separator of the `container` with `space-evenly` and `space-around` alignment.
* Added an additional api with `props` and `variables` for `custom` components.
* Added `array_insert_value` and `array_remove_value` typed actions.
* Added support for expressions in the `execAction` method.


## 28.8.0 (October 30, 2023)

Additions:
* Added `aspect` support for the `video` component.
* Added `text_shadow` support for the `text` component.
* Supported `set_variable` typed action.

Fixes:
* Fixed text cropping in several cases, especially with the `max_lines` property.
* Fixed an issue with the `input` server-side rendering (SSR).


## 28.7.0 (October 23, 2023)

* Added `custom` component support.


## 28.6.0 (October 16, 2023)

Additions:
* Added `array` variable type, alongside with the couple of functions `getStringFromArray`, `getOptDictFromArray` and others.
* Added aliases for the existing `dict` fucntions: `getStringFromDict` (alias for the `getDictString`) with many others.
* `dict` variables are now shown in markup as json instead of old `<dict>` (new `array`s works the same).
* Added `scale` property support in the `video` component.

Fixes:
* Fixed zero size support in `shape`s.


## 28.5.0 (October 9, 2023)

Deprecation:
* `palette` and `theme` marked as deprecated

Fixes:
* Fixed `input` multikey mask with the single pattern element


## 28.4.0 (October 2, 2023)

Fixes:
* Fixed custom `text` color with focus (without explicit `focused_text_color` property)


## 28.3.0 (September 25, 2023)

Additions:
* Added `state_id_variable` support in the `state` component


## 27.4.0 (August 28, 2023)

Additions:
* Added `tooltips` support

Fixes:
* Fixed an issue with the big amount of the unused event listeners on the DOM nodes


## 27.3.0 (August 21, 2023)

Fixes:
* Fixed an issue with a `gallery` component with the `default_item` outside of the items range


## 27.2.0 (August 14, 2023)

Additions:
* Added `div-patch`, `download` action and `download_callbacks` support
* Added support for the `focus` parameters (including ``focused_text_color`)


## 27.0.0 (July 31, 2023)

Fixes:
* Fixed an issue with the integer values from the several functions: `getYear`, `getMonth`, `getDay`, `getDayOfWeek`, `getHours`, `getMinutes`, `getSeconds`, `getMillis`, `len`, `index` and `lastIndex`. These integer values previously could produce errors in integer operations


## 26.2.0 (July 24, 2023)

Additions
* Supported `text_alignement_horizontal` and `text_alignement_vertical` properties in `input`


## 26.1.0 (July 17, 2023)

Additions:
* Implemented `testRegex` function

Fixes and changes:
* `content_alignment_` properties along cross-axis in `container` with the `wrap` mode will now align rows / columns of items, instead of children components (to better match Android / iOS behavior)
* Fixed expression processing without variables (constant expressions)


## 26.0.0 (July 10, 2023)

Additions:
* Allowed expressions in the `visibility_percentage`

Fixes:
* Fixed `fatal_actions` in the `video` component logic
* Fixed `video` unmount logic
* Incorrect non-array `background`s will not throw error
* Fixed default `visibility_percentage` value in the `disappear_action`
* Fixed `disappear_action` with the percentage equal to `0`


## 25.7.0 (July 03, 2023)

Fixes:
* Fixed update of the`gallery` with the `scroll_mode` = `paging` and `default_item` in rare cases


## 25.4.0 (June 12, 2023)

Added:
* Supported `dict` variable type


## 25.3.0 (June 5, 2023)

Added:
* New content alignments in the `container`: `space-between`, `space-around`, `space-evenly`
* `font_family` property is now supported in the different components. Keep in mind, for this property to work, you need to provide a `typefaceProvider`
* `margins` for a `container` separators are now supported


## 25.2.0 (May 29, 2023)

Additions:
* `disappear_actions` is now supported
* Added a new `video` component


## 25.0.0 (May 11, 2023)

Additions:
* Supported expressions in `tabs` title
* Added `currency` mask for the `input`

Fixes:
* Fixed `select` sizes, paddings and other size-related properties
* Fixed `select` `hint` show logic to match other platforms
* Fixed several cases with incorrect json processing (color is not a string, etc)
* Changed `actions` without `url` processing logic to match other platforms (previusly they was ignored as incorrect)
* Fixed `input` cursor logic with `mask` for the Safari (for the iOS in partucular)


## 24.3.0 (April 24, 2023)

Additions:
* `extensions` support
* SizeProvider extension
* Lottie extension
* Implemented `select` component
* Implemented an `input` `mask` for a text with a fixed number of characters

Fixes:
* Fixed markup update after `url` change in the `action`
* Fixed size-related properties of `input` (`paddings`, `border_radius`, `letter_spacing` and others), previously they incorrectly depended on the `font_size` property

## 24.2.0 (April 17, 2023)

Additions:
* `stretch` image scale type
* Ability to build custom DivKIt lib with a limited set of components and disabled expressions

Fixes:
* `no_scale` image scale type should now work properly


## 24.1.0 (April 10, 2023)

Fixes:
* The `aspect` property in the `container` should work better in combination with the `height` property


## 24.0.0 (April 3, 2023)

Breaking changes:
* The `setVariable` / `GetVariable` methods have been removed (these methods were deprecated long time ago before opensource, in web version 1.11.0)
* DivKit now uses `BigInt` internally if it is supported by the current platform. This also means that variable methods such as `subscribe` can now return `bigint` values instead of `number` (and accept such values in json). `integer` values in expressions are now limited to 64 bits instead of 32, and the minimum/maximum value for `integer` variables has been changed. Note that outside of expressions, DivKit restricts values to 32-bit integers, as was the case in previous versions. Also note that the js builtin methods, such as `JSON.parse`, return a simple `number`, so these values may lose accuracy outside the logic of DivKit. Make sure that your values are in the range `-2^53 — 2^53` or a custom json parsing function is used


## 23.7.0 (March 27, 2023)

Fixes and changes:
* Removed missing template field warning


## 23.6.0 (March 20, 2023)

Additions:
* `toUrl` / `toColor` functions


## 23.5.0 (March 13, 2023)

Additions:
* Functions for getting `datetime ` components
* Functions for getting value of a variable, if it exists. `getStringValue`, `getIntegerValue`, etc

Fixes and changes:
* `timers` ticks should now be more accurate and reliable. Time intervals would correct time error, there is also an extra tick before end the duration if the ticks doesn't trigger in time
* `visibility_action` actions will now evaluate expressions on the trigger, and not when the creating the component


## 23.4.0 (March 1, 2023)

Added:
* `aspect` support for `container` (`aspect-ratio` css property support is required!)

Fixes:
* Updated missing function error text, to better match other platforms
* Fixed error in `gallery` after destroy
* Fixed element `wrap_content` sizes with `constrained` in `grid`


## 23.1.0 (February 1, 2023)

Added:
* `indicator` can now be rendered in two ways, `placement` field is now supported
* `indicator` can now have a border inside every point, shape objects have been updated to support `background_color` and `border` properties. Several fields in `indicator` are now deprecated, including `space_between_centers`, `inactive_item_color`, `active_item_color`, `shape` and `active_item_size`. Shape objects support in `slider` was also updated

Fixed:
* `padStart` / `padEnd` now will work properly with an empty `pad_str` argument
* Default `width` `match_parent` should work more predictable in rare cases
* `separator` and `line_separator`  in `container` now will not block clicks and other events on component and it's descendants
* `constrained` size logic in rare cases


## 23.0.0 (January 25, 2023)

Fixed:
* `image` preview size and filters support
* `gallery` arrows on the desktop should now appear faster


## 22.0.0 (January 18, 2023)

Fixes:
* Fixed `tint_color` update for Safari


## 21.1.0 (January 11, 2023)

Additions:
* `min_size` / `max_size` for the `wrap_content` size
* Functions `padStart` / `padEnd`
* Functions for time intervals

Changes:
* Allow `wrap_content` size for the `galleries` on the main axis
* Fixed `timers` start with incorrect values


## 21.0.0 (December 26, 2022)

Additions:
* `timers` support
* Customization of the link protocols that should be rendered as native link (`builtinProtocols`)


## 19.0.0 (December 7, 2022)

Additions:
* `mix` property for a custom class name
* `customization` render property for a desktop `gallery`

Fixed:
* Incorrect `container` spacing with `separators`


## 18.0.0 (November 30, 2022)

Additions:
* `background` and `border` for the `text` ranges
* `baseline` vertical alignment

Fixed:
* Background redraw on variables change (with additional conditions)
* CSS names conflict on production build, now css classes are longer
* Artifacts near conrers, if both radius and stroke are used
* Updated `image` preview / placeholder logic
* Fixed `tint_mode` = `multiply`to better match Android logic


## 15.0.0 (November 14, 2022)

* Added separators support in DivContainer


## 14.0.0 (November 7, 2022)

Additions:
* Added `setTheme` instance method
* `filters` (only `blur` is supported so far) implemented for the `image` and image background

Fixed:
* `Enter` handling in multiline input


## 13.0.0 (October 30, 2022)

Additions:
* Added `circle` shape support
* Supported `no_animation` and `native` values for the `action_anmation`
* Supported `tint_mode`


## 12.0.0 (October 18, 2022)

Additions:
* Implemented Nine-patch background
* Supported for the `selected_actions` in `pager`

Changes and fixes:
* Fixed color values convertion


## 11.0.0 (October 12, 2022)

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


## 9.0.0 (September 27, 2022)

Added:
* `radial_gradient` support


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

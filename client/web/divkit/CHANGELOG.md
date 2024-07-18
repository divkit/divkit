## 30.9.0 (July 15, 2024)

* Performance optimizations.


## 30.8.0 (July 8, 2024)

* Fixed several issues with `variable_triggers` evaluation.


## 30.7.0 (July 1, 2024)

* Fixed the `top_offset` mutation for incoming data in the `text` component.


## 30.6.0 (June 24, 2024)

* Added support for the property `font_weight_value` in `text`, `input`, `slider` and `select`.
* Introduced `index` variable for prototyped elements.


## 30.5.0 (June 17, 2024)

* Added `array_set_value` action.


## 30.4.0 (June 10, 2024)

* Added `dict_set_value` action.
* Added `containsKey` method for dictionaries.


## 30.3.0 (June 3, 2024)

* Fixed an issue with the `text` property `top_offset` and ranges intersection.
* Added new API's for the extensions: `processExpressions` and `execAction`.
* Added `gesture` extension.
* Fixed an issue with calling `disappear_actions` after instance destruction.
* When passing `data` directly inside `item_builder`, its expressions are no longer executed.
* Supported `z`/`Z` patterns in datetime formatting functions.
* Added `max_length` property support for `input`.
* Supported `font_feature_settings` property for `text`.
* Updated error messages for invalid calls inside expressions.


## 30.2.0 (May 27, 2024)

* Calculate `is_enabled` in `actions` before executing the entire list of actions, not in the process.
* Implemented `toString` function and method for arrays and dicts.
* Improved expression error messages.
* Fixed unary minus operator parsing.
* Added functions `parseUnixTimeAsLocal`, `formatDateAsLocal`, `formatDateAsUTC`, `formatDateAsLocalWithLocale`, `formatDateAsUTCWithLocale` and the `weekStartDay` parameter.


## 30.0.0 (May 13, 2024)

* Breaking change: actions from `variable_triggers` lead to execution of a callback `onStat` with type = `trigger`.
* Disallowed variable names with consecutive dots or with trailing dot.


## 29.15.0 (April 22, 2024)

* Persistent variable storage has been implemented, including the action `set_stored_value` along with the function `getStoredStringValue` and others.
* Fixed an issue with calling `disappear_actions` when destroying a component (for example, when hiding a tooltip).
* Supported flag `is_enabled` for `input`.
* Fixed an issue with the patch changes after the tooltip action.
* Added `toString(string)` function.
* Added support for methods in expressions, including `toString`, `getString`, `getBoolean`, `getInteger`, `getNumber`, `getUrl`, `getColor`, `getDict`, `getArray`.
* Added support for the `menu_items` property inside `action`s (with a additional `customization` properties `menuPopupClass` and `menuItemClass`).
* Fixed an issue with the nested `longtap_actions` and simultaneous `actions` and `longtap_actions` processing.


## 29.12.0 (April 1, 2024)

* Fixed an issue with deep `state` changes.
* Implemented new typed action `clear_focus` which removes focus from any focused view.
* Supported `preload_required` property for the `video` component.
* Fix timer stop in rare cases.
* Fixed an issue with the `visibility_actions`, `disappear_actions` and `tooltips` in `patch`es.
* Fixed a bug where several `text` styles were not updated when the expression was changed.
* Fixed an issue with the `tel:` schema in actions.
* Minor performance optimization.
* Fixed parsing of incorrect values of `items` / `states` in `container`, `tabs` and `state`.


## 29.11.0 (March 25, 2024)

* Fixed a rare call to "tick_actions" after "end_actions" in timers.
* Removed validation for rectangular grid layout.


## 29.10.0 (March 18, 2024)

* Fixed several issues with the `tabs` component with the `match_parent` and `wrap_content` heights with the `constrained` flag.
* Fixed an animation issue after swipe in `tabs` component.
* Fixed a small issue with rendering corners with a frame and radius.
* Supported `scroll_to_start`, `scroll_to_end`, `scroll_backward`, `scroll_forward`, `scroll_to_position` in `gallery`. Updated `set_previous_item` and `set_next_item` to support `step` argument.
* Supported `scroll_to_start`, `scroll_to_end` in `tabs` and `pager`. Updated `set_previous_item` and `set_next_item` to support `step` argument.
* Fixed an issue with the `gallery` scroll actions and RTL layout direction.
* Implicit conversion of `integer` -> `number` in expressions is supported.


## 29.9.0 (March 11, 2024)

* Fixed space symbols collapse inside `tabs` title.
* `keyboard_type` = `password` has been supported for `input`.


## 29.8.0 (February 26, 2024)

* The `visibility_duration` and `disappear_duration` properties in `actions` now supports the value 0.


## 29.6.0 (February 12, 2024)

* Added support for the `preload_required` property in `image`, `gif` and `text` components.
* Fixed an issue with the `max_lines` and `auto_ellipsize` on the same `text` component.
* Errors from expressions now also contain the `path` property.


## 29.5.0 (February 5, 2024)

* Fixed several input issues in the `slider` with two thumbs.
* Fixed `gallery` markup in several cases.
* Fixed `input` focus outlines in several cases.
* Fixed a processing error when setting an incorrect value for the `integer` variable.
* Fixed an issue with the `state` patches.


## 29.4.0 (January 29, 2024)

* Added `item_builder` property support in the `container` component.
* Added `getDictFromDict`, `getArrayFromDict`, `getOptArrayFromDict` and `getOptDictFromDict` functions.
* Added support for the `RTL` layout (with the `direction` configuration property).


## 29.3.0 (January 22, 2024)

* `transition_in`, `transition_out`, `transition_change`, `action_animation` and `animation_in` / `animation_out` from the `tooltip` will now use system setting "prefers reduced motion".


## 29.2.0 (January 15, 2024)

* Fixed an issue with the `visibility_action`s restart on variable changes and their cleanup.
* Added support for the `is_enabled` flag inside all types of actions.
* Added `clip_to_bounds` property support for the `container` component.
* Fixed an issue with the `wrap_content` `image`s in Safari.
* Fixed `variable_triggers` logic to match other platforms. Now they are executed, even if not all variables are present.
* Fixed `variable_triggers` to be executed only after components mount.
* Added `auto_ellipsize` property support in `text` component.
* Fixed an issue with the experimental method `setData` and `video` updates.
* Fixed `null` value processing in `array` getter functions.


## 29.1.0 (December 25, 2023)

* Fixed rare cases with the `lottie` animations load errors.


## 29.0.0 (December 18, 2023)

* Breaking change: expressions are now executed without waiting for all variables in them.
* Breaking change. The type `Variable` and other related types (such as `StringVariable`) are now classes, not interfaces. In most cases, all the code will work the same way, but in rare cases it may break something. Also note that it is now possible to check variable type with the `instanceof`, which works as a type-guard in TypeScript (`getValue()` will return a value with the correct type). Previously, this was not possible because only `getType` existed, and TypeScript would not use it as a type-guard.
* Breaking change: private methods in DivKit instance was removed. This methods was not typed and was not documented.
* Breaking change: added arrows for `pager` on the `desktop` platform. There are new customization options for these arrows. Also note that the `platform` can be configured using the `render` method.
* Breaking change: the internal implementation of the `lottie` extension has been changed. This was required to fix the issue with simultaneous display of fallback `gif` with `lottie` itself. If you are providing a similar extension created from scratch, you may need to change it a bit.
* Added try operator `!:` for expressions.
* Undeclared variables would not cause expressions to fail (even without getting their values) (revert change from version 23.4.0). The error would still occur if the expression actually evaluated an undeclared variable.
* Fixed the behavior of `match_parent` inside the `container` with the size of `wrap_content` and `orientation` = `overlap`.
* Added `len` function for array variables.
* Fixed an issue with the expressions support inside `transition_change` property.
* Fixed an SSR error that occured when rendering a card with a list of timers.
* Empty containers (without `items`) is now allowed.
* Added support for patches in `tabs` and `states` (note that patches require exactly one element for each change, unlike other types of components).
* `text` component with an empty `text` property is now allowed.
* Fixed an issue with whitespace near brackets `()` in expressions.
* Added a new experimental method `setData`. This method allows you to quickly redraw the DivKit card using a new json, much more efficiently than directly destroying and recreating it again.
* Fixed possible errors in integer calculation for the functions `getArrayInteger`, `getIntegerFromArray`, `getArrayOptInteger`, `getOptIntegerFromArray`, `getDictInteger`, `getIntegerFromDict`, `getDictOptInteger`, `getOptIntegerFromDict`, `copySign`.
* Fixed an issue with a `separators` in `container` in the `ssr` mode.
* Empty `grid`, `gallery` and `pager` are now allowed.


## 28.12.0 (November 27, 2023)

* Added `scrollbar` property support in `gallery`.
* Fixed the text of the missing `gif_url` error. Also the `gif` component now uses `gif_url` and `image` uses `image_url`, instead of both of them consuming any of these 2 properties.
* Removed empty `padding` attribute from html.
* Fixed empty `image`s (and `gif`s) to load error. Note "empty image" here is an `image` with an `image_url` property equal to `empty://`.


## 28.11.0 (November 20, 2023)

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

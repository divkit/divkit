## 30.21.0

# Android Client:
* Added evaluable functions `getStoredArrayValue(name)`, `getStoredDictValue(name)`.
* Added new typed action `scroll_by` that can be used instead of `div-action://scroll_forward`, `div-action://scroll_backward`, `div-action://set_next_item` and `div-action://set_previous_item`.
* Added new typed action `scroll_to` that can be used instead of `div-action://set_current_item`, `div-action://scroll_to_position`, `div-action://scroll_to_start` and `div-action://scroll_to_end`.
* Added new typed action `set_stored_value` that can be used instead of `div-action://set_stored_value`.

# iOS Client:
* Changed pager's page size evaluation to make the behavior consistent with other platforms.
* Fixed focus on ios. update blocks after changing focus via actions or inside blocks.
* `ImageBlock` `tintMode: TintMode` and `tintColor: Color?` can now be rendered using `RemoteImageView` instead of `MetalImageView`. To do so `imageTintPreferMetal: Bool` must be set to `false` via `DivFlagsInfo`

# Web Client:
* Supported `id` property for the prototypes in the `item_builder`.

# TypeScript JSON Builder:
* Added platforms support in the TSDoc comments.


## 30.20.0

# Android Client:
* Added implementation for local `variable_trigger` field.
* Added method `DivVariableController#replaceAll` which updates all existing variables, declare new ones and removes variables which are not provided in the list of new variables, and `DivVariableController#replaceAll` which removes all provided variables. 
* Added `tighten_width` parameter for `div-text`, that removes unnecessary horizontal paddings in multiline texts.
* Added support of variable animators.
* Added support of `scope_id` property for `set_variable` actions.
* Experimental API: Added code generation of json serializers.
* Changed behavior in case when all children in container with `wrap_content` size along the cross axis for horizontal and vertical orientation (with `layout_mode` `no_wrap`) or any axis for overlap orientation have `match_parent` size. Now container's size becomes equal to the biggest child's size (as earlier) and other children take the same size (instead of their content's size before).
* Fixed `indicator` behavior when connected `pager` has invisible items.
* Fixed `indicator` behavior when infinite scroll is enabled.

# iOS Client:
* Added `tighten_width` parameter for `div-text`, that removes unnecessary horizontal paddings in multiline texts.
* Added new typed action `div-action-timer` that can be used instead of `div-action://timer`.
* Added new typed action `div-action-video` that can be used instead of `div-action://video`.
* Added `InputAccessoryViewExtensionHandler` which supports adding `inputAccessoryView` for `div-input` keyboard.
* Supported `resolveValue` for `RawRepresentable` type.
* Supported `DivFunction` in `div-base`. Functions can be declared in any div element. Access to functions is carried out in the same way as local div-variables.
* Fixed problems with keyboard appearing in multiline text input.

# Web Client:
* Added support for the `on_applied_actions` and `on_failed_actions` inside `patch`.
* Added support for the `autocapitalization` property in the `input` fields.
* Added `encodeRegex` function.
* Added `isEmpty()` method to dict.
* Changed behavior in case when all children in container with `wrap_content` size along the cross axis for horizontal and vertical orientation (with `layout_mode` `no_wrap`) or any axis for overlap orientation have `match_parent` size. Now container's size becomes equal to the biggest child's size (as earlier) and other children take the same size (instead of their content's size before).


## 30.19.0

# Android Client:
* Experimental API: added new parsers for properties and template fields.
* Supported `item-builder` property in `gallery` and `pager`.
* Supported `autocapitalization` in `input`. Allows you to control capital letters in the keyboard
* Added new typed action `div-action-download` that can be used instead of `div-action://download`.
* Added new typed action `div-action-hide-tooltip` that can be used instead of `div-action://hide_tooltip`.
* Added new typed action `div-action-set-state` that can be used instead of action `div-action://set_state`.
* Added `isEmpty()` method to dict.
* Create markwon lazily.
* Fixed patch applying to root div.
* Fixed focus navigation inside `gallery`.
* Fixed keyboard blinking when changing focus between inputs.

# iOS Client:
* Fixed a bug with notification observers while changing parent variable from child scope
* Fixed opacity for elements with solid background.

# Web Client:
* Added local `variables` support.
* Added `layout_provider` property support.
* Fixed the behavior of `toNumber('')` to return an error.
* Fixed the `transition_out` animation when `visibility` became `gone`.
* Fixed an issue with the `max_length` of `input` with the `keyboard_type` equal to `number`.
* Fixed the first call of `variable_triggers` before full initialization of child components.
* Fixed an issue with incorrect `corners_radius`.
* Fixed an issue with incorrect `variable_triggers`.


## 30.18.0

# Android Client:
* Experimental API: added template serialization interfaces.
* Fixed a bug where the input field intercept vertical scrolling.
* Fixed corner clipping while transitions is in progress.
* Support `div-custom` patching.

# iOS Client:
* Experiment added to use different drawing methods for blur/tint effects inside `ImageBlock`. Previously `MetalImageView.swift` was the only option. Now using `DivFlagsInfo` - we can set `imageBlurPreferMetal: Bool` or `imageTintPreferMetal: Bool` to false, and `RemoteImageView.swift` will be used instead (with new way to render). By default behaviour stays the same as before.
* Support resolveValue for RawRepresentable type.
* Supported div-input max_length field for ios


## 30.17.0

# Android Client:
* Bump OkHttp version to 4.12.0
* Implemented support for field `accessibility` for images inside text. (from version 30.14)
* Supported `alignment_vertical` for `ranges` and `images` elements.
* Implemented support for methods for getting values from dict `getBoolean, getDict ...` You can use them in evaluable expressions like that `dict_var.getBoolean(path_to_var)`.
* AGP version updated to 8.5.2 and Gradle version updated to 8.10.
* Added new typed action `div-action-show-tooltip` that can be used instead of `div-actino://show_tooltip`.
* Added new typed action `div-action-timer` that can be used instead of `div-action://timer`.
* Added new typed action `div-action-video` that can be used instead of `div-action://video`.
* Fixed black background behind the corners of shimmer extension on apis lower than Android 26.
* Fix bug when image preview was skipped after bind with same image_url, but different preview.
* Throw AssertationError on try to load patch without providing `DivDownloader` to `DivConfiguration`.

# iOS Client:
* Fixed bug with last small page.
* Fixed bug with pager scrolling.


## 30.16.0

# Android Client:
* Fixed visibility actions within patch layout.
* Fixed a bug when separators inside a `container` ignore margins.

# iOS Client:
* Added test playgrounds with layout of objects from LayoutKit.
* Added support of autocapitalization type in `div-input` text fields.
* Added filtering entered data in `div-input` using regexp or expressions.
* Fixed a bug with scrolling through the last page.


## 30.15.0

# Android Client:
* Fix indicator disappearing after patch.

# iOS Client:
* Added fix of scrolling multiline text input while typing text to avoid active input to be under keyboard.
* Fixed variable trigger invocation after calling appends from the client.


## 30.14.0

# Android Client:
* Added support for local `variables` field.
* Fixed an issue when items built from prototypes couldn't receive variable updates.
* Deprecated method `DivCustomContainerChildFactory#bindChildView`. Use a new method implementation with `childIndex` param instead. Otherwise, local variables inside custom containers wouldn't work.
* Support toString() methods for dicts and arrays.

# iOS Client:
* Moved edits from the patch for the swipe_down fix.
* Fixed a bug when scrolling from page to another page.
* Fixed boolean type parsing in `set_stored_value` action.

# Web Client:
* Added `markdownExtensionBuilder` function, which accepts the markdown processing function and produces a `markdown` extension.
* Added `getComponentProperty` extension API.


## 30.13.0

# Android Client:
* Now `next_focus_ids.forward` supports accessibility order.
* Now `div-gallery` in `paging` scroll mode will be centered like in other platforms.
* Fixed visibility action tracking when `DivData` moved to another `Div2View`.
* Fixed multiple state switching within pager items.

# iOS Client:
* Added variable triggers to div-base. They can be triggered by parent variables.
* Reset local triggers in the elements after applying div-patch.

# Web Client:
* Updated the layout of the `grid`, in which elements are superimposed on each other to match the behavior of Android.
* The logic of `transition_change` has been brought into line with other platforms: with this change, there is no need to contain the `transition_change` property in the final state. In addition, parameters from the previous state will be used, not from the new one.
* Fixed the `auto_ellipsize` property in the presence of `max_lines` for the `text` component.
* Performance optimizations.


## 30.12.0

# Android Client:
* Supported `layout_provider` property.
* Supported `div-patch.on_applied_actions` and `div-patch.on_failed_actions`.
* Support custom corner radius at `shimmer` extension.
* Fixed pages in `pager` not switching in accessibility.

# iOS Client:
* Supported `item_builder` for pager and gallery.
* Added callbacks with div actions in div-patch. Called after applying patches.


## 30.11.0

# Android Client:
* Supported `reuse_id` field. Use `reuse_id` for more efficient reuse of items with same structure in `gallery`/`pager`.
* Fixed gallery not reusing items.
* Fixed custom shadow drawing when view is in transient state.

# iOS Client:
* Supported page transformations in pager.
* Supported reuse_id field for items in pager, gallery, tabs, grid
* Update alpha while changing states with action_animation params.
* Fixed operators in expressions parsing.
* Fixed div-custom configuration
* Added items count info to gallery scroll event


## 30.10.0

# Android Client:
* Make `div-stroke.width` a number.
* Now visual errors dump includes card's json and all global variables states.

# iOS Client:
* Fixed video player initialization in `VideoBlock`.


## 30.9.0

# Android Client:
* Fix `shadow` rotation if `div` uses `transform`.

# iOS Client:
* Fixed element paths for `div-gallery`.
* Added `div-base.layout_provider`.
* Optimized `DivLastVisibleBoundsCache`.

# Web Client:
* Performance optimizations.


## 30.8.0

# Android Client:
* Implemented `DivMarkdownExtensionHandler` to support `markdown` extension for `text`.
* Fixed crash after removing child in `gallery`.
* Fixed drawing shadows when view is invisible or gone.
* Fixed NPE at `shimmer` extension
* Fix issue when Div2View could not access variable via nested DivVariableController.

# iOS Client:
* Added local variables support.
* Fixed infinity pager scroll.
* Opening tooltips in new window.
* Fixed multiple `player.seek` call in `VideoBlock`.

# Web Client:
* Fixed several issues with `variable_triggers` evaluation.


## 30.7.0

# Android Client:
* Add numeric value support for `fontWeightValue`
* Supported `id` property in `div-collection-item-builder.prototype`.
* Fixed div view state change happened before binding.

# iOS Client:
* Optimized `ExpressionResolver`.
* `DivVariableTracker` state made public.

# Web Client:
* Fixed the `top_offset` mutation for incoming data in the `text` component.


## 30.6.0

# Android Client:
* Fixed multiple state switching with different root state.
* **Experimental API:** added new interfaces for data serialization.
* Introduced `index` variable for prototyped elements.
* Fixed grid cell size calculation when margins are set.

# iOS Client:
* Fixed inline image reusing in DivText.
* Added `DivVariableStorage.hasValue` method.
* Added `font_weight_value` support.
* Added `id` property into `div-collection-item-builder.prototype`.
* Fixed memory leak in VisibilityActionPerfromers.
* Added `hitTest` implementation into `StateBlockView` to pass through touches.

# Web Client:
* Added support for the property `font_weight_value` in `text`, `input`, `slider` and `select`.
* Introduced `index` variable for prototyped elements.


## 30.5.0

# Android Client:
* Implemented support for field `page_transformation` in `pager`.
* Old visibility actions are no longer logged after Div2View was rebound by another DivData.
* Fixed image drawing when its padding bigger than size.
* Fixed showing of first and last elements in `pager` with infinite scroll after items changing.
* Fix closing `tooltip` on cleanup.
* Fixed `Div2View` state reset that resulted in broken expression subscriptions.
* Fixed vararg functions matching.
* Detach old indicators from pager on attach new ones.
* `okhttp` library updated to version 4.11.0

# iOS Client:
* Introduced `index` variable for prototyped elements.
* Fixed escaped strings resolving in expressions

# Web Client:
* Added `array_set_value` action.


## 30.4.0

# Android Client:
* Fixed `pager` page size evaluation. Now all the pages are the same size, the same as on other platforms.
* Fixed scroll of `pager` when more then 2 pages is visible.
* Added `array_set_value` action.
* Added `dict_set_value` action.
* Added `containsKey` method for dictionaries.

# iOS Client:
* Fixed `input` paddings.
* Fixed comparing `scrollRange` in `GalleryViewState`.
* Added `array_set_value` action.
* Added `dict_set_value` action.
* Added `containsKey` method for dictionaries.

# Web Client:
* Added `dict_set_value` action.
* Added `containsKey` method for dictionaries.


## 30.3.0

# Android Client:
* Fixed sizes calculation for `gallery` `items` wrapped within `container`.
* Added `tab_title_delimiter` property support for `tabs`.
* Remove focus from focused `input` if some clickable div were clicked.
* Removed `z`/`Z` patterns restriction from datetime formatting functions.
* Added `max_length` property support for `input`.
* Fixed crash by `gallery` when count of `items` was less then `column_count`. 
* Supported `font_feature_settings` property for `div-text`.

# iOS Client:
* Fixed items position in `gallery`.
* Supported `font_feature_settings` property for `div-text`.
* Removed `z`/`Z` patterns restriction from datetime formatting functions.
* Fixed clipping in `container`.

# Web Client:
* Fixed an issue with the `text` property `top_offset` and ranges intersection.
* Added new API's for the extensions: `processExpressions` and `execAction`.
* Added `gesture` extension.
* Fixed an issue with calling `disappear_actions` after instance destruction.
* When passing `data` directly inside `item_builder`, its expressions are no longer executed.
* Supported `z`/`Z` patterns in datetime formatting functions.
* Added `max_length` property support for `input`.
* Supported `font_feature_settings` property for `text`.
* Updated error messages for invalid calls inside expressions.


## 30.2.0

# Android Client:
* Fixed line height adjustment for embedded images and image placeholdes in `div-text`.
* Added padding on draw `default_item` in `gallery`.
* Improved expression error messages.

# iOS Client:
* Fixed disappear actions for gone blocks.
* Improved expression error messages.

# Web Client:
* Calculate `is_enabled` in `actions` before executing the entire list of actions, not in the process.
* Implemented `toString` function and method for arrays and dicts.
* Improved expression error messages.
* Fixed unary minus operator parsing.
* Added functions `parseUnixTimeAsLocal`, `formatDateAsLocal`, `formatDateAsUTC`, `formatDateAsLocalWithLocale`, `formatDateAsUTCWithLocale` and the `weekStartDay` parameter.


## 30.1.0

# Android Client:
* Fixed a bug where updates to global variables are lost if they occurred after the destruction of Div2View, but before the creation of a new one.

# iOS Client:
* Implemented operands lazy evaluation.


## 30.0.0

# Android Client:
* Breaking change: Classes `StandardExoDivPlayerView` and `ZoomExoDivPlayerView` replaced with common `ExoDivPlayerView`
* Breaking change: Classes removed from public API: `ExoDivMediaSourceAbstractFactory`, `ExoDivPlayer` (accessible by interface `DivPlayer`), `ExoDivPlayerView` (accessible by interface `DivPlayerView`)
* Breaking change: Removed method `isCompatibleWithNewParams(scale: DivVideoScale)` from `DivPlayerView`
* Breaking change: Removed method `makePlayerView(context: Context, additional: Map<String, Any>)` from `DivPlayerFactory`
* Breaking change: Use explicitly passed `ExpressionResolver` in `DivActionHandler`, `Div2Logger`, `DivExtensionHandler` and `custom` classes.
* Breaking change: Restricted variable names with consecutive dots and with a trailing dot.
* Added `gesture` extension that allows to perform div-actions when swipe gesture happens at specified div.
* Added `item_builder` property support in the `container` component.
* Added `svg` support to all `DivImageLoaders`
* Added ability to forcibly release video players by `releaseMedia()` method of `Div2View`.
* Added Jetpack Compose interoperability for `Div2View`
* Calculate `is_enabled` in `actions` before executing the entire list of actions, not in the process.
* Do not set new border or background when it has no changes.
* Fixed text height calculation when line height specified explicitly.
* Fixes for complex rebind algorithm
* Old visibility actions are no longer logged after Div2View was rebound.
* Supported methods in evaluable expressions, such as `123.toString()`.
* TextureView is now a default view for DivVideo.


# iOS Client:
* Breaking change: Changed method signature in `ExpressionResolver` from `resolveString(_ expression: String) -> String` to `resolveString(_ expression: String) -> String?`.
* Breaking change: Protocol `DivStateInterceptor` and corresponding implementation `DivStateInterceptorImpl` was removed. Please use `DivExtensionHandler` instead.
* Breaking change: public API for `DivActionURLHandler.updateReason` has been changed.
* Breaking change: Removed `DefaultDivActionLogger` (replaced with `EmptyDivActionLogger`). `DivActionLogger` became deprecated, use `DivReporter` for actions tracking.
* Breaking change: Removed `DefaultShimmerViewFactory`.
* Breaking change: Removed `DivVariablesStorage.ChangeEvent.oldValues` due to performance issues.
* Breaking change: Removed `urlOpener` argument from `DivKitComponents` initializer. Use `urlHandler` instead.
* Breaking change: Removed `UserInterfaceAction.Payload.composite`.
* Breaking change: Removed `UserInterfaceAction.Payload.json`.
* Breaking change: The signatures of the setSource functions have been changed: they are now marked as async to reduce main thread locks.
* The minimum version of the DivKit has been increased to iOS 13.
* Disallowed variable names with consecutive dots or with trailing dot.
* Double quotes are no longer supported in string literals.
* Fixed animation in gallery.
* Fixed numbers to string casting in expressions.
* Fixed position of gallery element after scrolling with action.
* Fixed unary minus operator parsing.
* Fixed validation in expressions.
* Fixed view reuse in `ShimmerImagePreviewExtension`.

# Web Client:
* Breaking change: actions from `variable_triggers` lead to execution of a callback `onStat` with type = `trigger`.
* Disallowed variable names with consecutive dots or with trailing dot.

# Kotlin JSON Builder:
* Changed the way of how primitive array elements are added.

This change supports usage of expressions in array elements. As for now, primitive array elements  (e.g., transition triggers, gradient backgrounds) must be
wrapped with either `divkit.dsl.core.valueArrayElement` or`divkit.dsl.core.expressionArrayElement`.


## 29.15.0

# Android Client:
* Added ability to set a specific logging level using DivKit#setLogLevel.
* Replaced `String` with `CharArray` at expression tokenization process.
* Recreating subscriptions to local variables in case Div2View is recreated after destroying its lifecyclerOwner in order to preserve the state of local variables.
* Fixed that invalid base64 preview in `DivVideo` may cause `IllegalArgumentException`.
* Fixed NoSuchElementException on handling disappear actions.
* Fixed accessibility of gallery children when exclude a11y mode turned on.
* Fixed that `video` screen could show up before `preview` was rendered.

# iOS Client:
* Removed mask from GalleryView to reduce offscreen rendering.
* Added `toString` method/function for arrays and dictionaries.
* Fixed arrays and dictionaries convertion to string.
* Removed unnecessary re-creation of gestureRecognizers.
* Fixed resetting inconsistent gallery state in pager.
* Fixed allocated unfair lock in `DivBlockStateStorage`.
* Added `toString(string)` function.
* Added `scale` support in Lottie animation.
* Added `len` function for arrays.
* Fixed resetting video when `elapsed_time_variable` is not set.
* Fixed pager state after reuse.
* Fixed looping of video player events.

# Web Client:
* Persistent variable storage has been implemented, including the action `set_stored_value` along with the function `getStoredStringValue` and others.
* Fixed an issue with calling `disappear_actions` when destroying a component (for example, when hiding a tooltip).
* Supported flag `is_enabled` for `input`.
* Fixed an issue with the patch changes after the tooltip action.
* Added `toString(string)` function.
* Added support for methods in expressions, including `toString`, `getString`, `getBoolean`, `getInteger`, `getNumber`, `getUrl`, `getColor`, `getDict`, `getArray`.
* Added support for the `menu_items` property inside `action`s (with a additional `customization` properties `menuPopupClass` and `menuItemClass`).
* Fixed an issue with the nested `longtap_actions` and simultaneous `actions` and `longtap_actions` processing.


## 29.14.0

# Android Client:
* Added implicit cast from `integer` to `number` inside evaluable expressions.
* Supported flag `is_enabled` for `input`.
* Fixed `restrict_parent_scroll` to restrict both directions
* Fixed reusing of view id in same Div2Context
* Added support of signed numbers in numeric `div-input`

# iOS Client:
* Added `toString()` method support in expressions.
* Fixed ternary operator parsing.
* Added get methods for dictionaries and arrays in expressions.


## 29.13.0

# Android Client:
* Supported loading images from assets by url `divkit-asset://`
* Fixed that sometimes extensions could be removed on state change.
* Fixed a memory leak occurring when using expressions in the "alignment" fields of the `container` element.

# iOS Client:
* Fixed focus setting when state changes and there are multiple inputs without id.
* Supported `is_enabled` property in `input`.
* Fixed variables usage in tooltips.
* Fixed visibility action of empty container.
* Fixed crash in input with mask.
* Fixed DivVideo view reconfiguration.


## 29.12.0

# Android Client:
* Enable android.nonTransitiveRClass.
* Supported `item-builder` property in `container`.
* Removed excessive list copy creation for thread-safe iteration.

# iOS Client:
* Fixed nested expression parsing.
* Implemented new typed action `clear_focus` which removes focus from any focused view.
* Improved views reuse.
* Removed `id` requirement for `div-state`.

# Web Client:
* Fixed an issue with deep `state` changes.
* Implemented new typed action `clear_focus` which removes focus from any focused view.
* Supported `preload_required` property for the `video` component.
* Fix timer stop in rare cases.
* Fixed an issue with the `visibility_actions`, `disappear_actions` and `tooltips` in `patch`es.
* Fixed a bug where several `text` styles were not updated when the expression was changed.
* Fixed an issue with the `tel:` schema in actions.
* Minor performance optimization.
* Fixed parsing of incorrect values of `items` / `states` in `container`, `tabs` and `state`.

# Kotlin JSON Builder:
* Added ability to create `Patch` declaration.


## 29.11.0

# Android Client:
* Fixed visibility recognition of pages in pager.
* Removed validation for rectangular grid layout.
* Screenshot test plugin improvements.
* Set version without build start time in debug build.
* Fixed `restrict_parent_scroll`.
* Implemented new typed action `clear_focus` which removes focus from any focused view inside Div2View.
* Fixed overriding of `custom` layout params.

# iOS Client:
* Fixed variable names parsing.
* Add default assets image provider.

# Web Client:
* Fixed a rare call to "tick_actions" after "end_actions" in timers.
* Removed validation for rectangular grid layout.


## 29.10.0

# Android Client:
* Migrated to Gradle version catalog.
* Add div data generator plugin.
* Fixed SO at state variable change.

# Web Client:
* Fixed several issues with the `tabs` component with the `match_parent` and `wrap_content` heights with the `constrained` flag.
* Fixed an animation issue after swipe in `tabs` component.
* Fixed a small issue with rendering corners with a frame and radius.
* Supported `scroll_to_start`, `scroll_to_end`, `scroll_backward`, `scroll_forward`, `scroll_to_position` in `gallery`. Updated `set_previous_item` and `set_next_item` to support `step` argument.
* Supported `scroll_to_start`, `scroll_to_end` in `tabs` and `pager`. Updated `set_previous_item` and `set_next_item` to support `step` argument.
* Fixed an issue with the `gallery` scroll actions and RTL layout direction.
* Implicit conversion of `integer` -> `number` in expressions is supported.


## 29.9.0

# Android Client:
* Reduced number of memory allocations at observing of client-side variables.
* Fixed periodic crash in `pager` on view rebind.
* `keyboard_type` = `password` has been supported for `input`.
* Fixed video overlap with scale `fill`.
* Removed redundant memory usage when calling the `putOrUpdate` method on a `DivVariableController` with already known variables.
* `Complex rebind` enabled by default.

# iOS Client:
* Implemented new actions to scroll gallery `scroll_to_start`, `scroll_to_end`, `scroll_backward`, `scroll_forward`, `scroll_to_position`.
* Added implicit cast from integer to number.
* Fixed actions using item_builder.
* Fixed numeric values parsing.
* Fixed skipping pages when scrolling pager by timer.

# Web Client:
* Fixed space symbols collapse inside `tabs` title.
* `keyboard_type` = `password` has been supported for `input`.


## 29.8.0

# Android Client:
* New rebinding algorithm is implemented under `complexRebind` flag (can be enabled using the DivConfiguration builder).
* Implemented new actions to scroll gallery `scroll_to_start`, `scroll_to_end`, `scroll_backward`, `scroll_forward`.
* Fixed autoallipsizing of `div-text` with fixed line height.
* Fixed artifacts on drawing corner_radius with border.

# iOS Client:
* `GestureExtensionHandler` introduced.

# Web Client:
* The `visibility_duration` and `disappear_duration` properties in `actions` now supports the value 0.


## 29.7.0

# Android Client:
* Fixed text line height so that it behaves as on other platforms.
* Fixed baseline alignment for `image`, `gif-image` and `video`.


## 29.6.0

# Android Client:
* Supported page switching inside `pager` using focus.
* `separator` and `image` without any `action` or `accessibility` block are excluded from accessibility.
* Fixed subscription in `video` to fields with `variables`

# iOS Client:
* Added `focus_element` action support.
* Added `auto` `div-accessibility.type`.
* Fixed accessibility for `div-input`.
* Fixed concurrency crash in `DivStateManager`.
* Added more information about associated `action` in URL handling.

# Web Client:
* Added support for the `preload_required` property in `image`, `gif` and `text` components.
* Fixed an issue with the `max_lines` and `auto_ellipsize` on the same `text` component.
* Errors from expressions now also contain the `path` property.


## 29.5.0

# Android Client:
* Supported functions `getArrayFromDict`, `getDictFromDict`, `getOptArrayFromDict`, `getOptDictFromDict`.
* Supported accessibility for `slider`.
* Fixed visibility actions not working for items children inside `div-pager` and `div-tabs`.

# iOS Client:
* Fixed cursor position in the text with emoji.
* Improved `DivData` parsing.

# Web Client:
* Fixed several input issues in the `slider` with two thumbs.
* Fixed `gallery` markup in several cases.
* Fixed `input` focus outlines in several cases.
* Fixed a processing error when setting an incorrect value for the `integer` variable.
* Fixed an issue with the `state` patches.


## 29.4.0

# Android Client:
* Optimized the performance of `div-tabs` when scrolling.
* Fixed a bug when the last element twitched when scrolling `div-pager` with `infinite-scroll`.
* Added ability to scroll `div-gallery` forward/backward by N-items using `div-action`.
* Fixed div models generating without templates.

# iOS Client:
* Added action payload to `DivReporter.reportAction`.
* Add operators error messages.
* Supported datetime format functions.
* Added `list` value support for `div-accessibility.type`.
* Fixed visibility actions for views that can change their size.

# Web Client:
* Added `item_builder` property support in the `container` component.
* Added `getDictFromDict`, `getArrayFromDict`, `getOptArrayFromDict` and `getOptDictFromDict` functions.
* Added support for the `RTL` layout (with the `direction` configuration property).

# Kotlin JSON Builder:
* Completely removed divkit.dsl.Url input value validation since it doesn't comply with RFC 2396.


## 29.3.0

# Android Client:
* Fixed element blinking when rebinding `div-gallery`.
* Implemented `hash()` method for Divs.
* Added a parameter to `DivConfiguration.Builder#divPlayerPreloader`. Also implemented `ExoPlayerVideoPreloader`, which can be used to preload videos if you are using `ExoDivPlayerFactory` as your video player.
* Disabled creation of implicit default values for object properties.
* Fixed a bug that `visibility_action` does not work for children with size more than `div-gallery` size.
* Added parameter describing the reason of `div-action` call to `DivActionHandler` methods.
* Fixed `visibility_change` animation

# iOS Client:
* Updated VGSL version to 3.0.0.
* Fixed `DivView` size calculation.
* Added ability to force action updates scheduled for the next run loop iteration.

# Web Client:
* `transition_in`, `transition_out`, `transition_change`, `action_animation` and `animation_in` / `animation_out` from the `tooltip` will now use system setting "prefers reduced motion".


## 29.2.0

# Android Client:
* Fixed character escaping.
* Fixed `state` does not switch inside `div-custom`.
* Added support `is_enabled` for `action`.
* Migrated from Dagger to Yatagan DI framework
* Added supporting `len(array)` function in evaluable.
* Fixed min size applying in `div-container`.
* Fix wrong gradient color in `div-text`.
* Fixed not sending taps through the `div-gallery`.

# iOS Client:
* Supported `is_enabled` property in `div-action`.
* Optimized accessibility elements creation.
* Changed `DivText.text` property type from `CFString` to `String`.
* Fixed `DivContainer` without items behavior.
* Fixed accessibility params for elements with margins.
* Fixed memory leak in `FunctionsProvider`.
* Fixed Lottie blinking when state changed.

# Web Client:
* Fixed an issue with the `visibility_action`s restart on variable changes and their cleanup.
* Added support for the `is_enabled` flag inside all types of actions.
* Added `clip_to_bounds` property support for the `container` component.
* Fixed an issue with the `wrap_content` `image`s in Safari.
* Fixed `variable_triggers` logic to match other platforms. Now they are executed, even if not all variables are present.
* Fixed `variable_triggers` to be executed only after components mount.
* Added `auto_ellipsize` property support in `text` component.
* Fixed an issue with the experimental method `setData` and `video` updates.
* Fixed `null` value processing in `array` getter functions.

# TypeScript JSON Builder:
* Added expressions support in `fixed` function.


## 29.1.0

# Android Client:
* Supported `clip_to_bounds` property in `div-container`.
* Optimized binding process of base div properties.
* Optimized binding process of `div-text`.
* Optimized binding process of `div-separator`.
* Optimized binding process of `div-container`.
* Fixed calling of disappear actions for tooltips.
* Fixed parent's vertical scroll starting at `div-slider`.
* Fixed state switching of `div-gallery` items.
* Fixed incorrect height of `div-input`.
* Fixed text color applying for ranges in `div-text`.

# iOS Client:
* Fixed `DivVariableStorage` change events.
* Optimized `DivData` parsing.
* Improved `DivBlockModelingContext` cloning.

# Web Client:
* Fixed rare cases with the `lottie` animations load errors.


## 29.0.0

# JSON Schema:
* Removed `non_empty_string` type.
* Removed some `minItems` restirctions.
* `items` property in `container`, `gallery`, `pager` and `grid` became not required.

# Android Client:
* Fixed timers cleared after patch.
* Added Complex Rebind flag.
* Added ability to reset expression runtimes, error collectors and selected states for given data tags.
* Added the ability to change ViewPreCreationProfile in Div2Context.
* Added supporting 'try' operator.
* Fixed a case when the wrong DivActionHandler was used.
* Improved performance by making functions stateless.
* Fixed a bug that visibility_action does not work for children of the gallery element.
* Added the ability to enable/disable the scrollbar in the gallery.
* Added Coil image loader.

# iOS Client:
* Supported `item_builder` property in `DivContainer`.
* Supported `scrollbar` property in `DivGallery`.
* Supported `clipToBounds` parameter in `DivContainer`.

# Web Client:
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


## 28.13.0

# Android Client:
* Fixed using incorrect uri scheme in Beacon.
* Fixed broken fade animation.
* Added the ability to set the lineHeight to be smaller than the font size.

# iOS Client:
* Supported `!:` operator.


## 28.12.0

# Android Client:
* Added `getStoredUrlValue` functions.
* Removed dependency on androidx.fragment.
* Fixed the hyphenation of images in the text.
* Fixed baseline alignment in `DivCustom`.
* Fixed bind on enabled bind_on_attach.
* Fixed alignment of the DivStateLayout.

# iOS Client:
* Change the unit of video duration to ms
* Optimized actions resolving.
* Removed `UserInterfaceAction.Payload.json` usage.
* Fixed putting text to pasteboard when cutting in the input with a mask.
* Fixed `DivPager` state updating.

# Web Client:
* Added `scrollbar` property support in `gallery`.
* Fixed the text of the missing `gif_url` error. Also the `gif` component now uses `gif_url` and `image` uses `image_url`, instead of both of them consuming any of these 2 properties.
* Removed empty `padding` attribute from html.
* Fixed empty `image`s (and `gif`s) to load error. Note "empty image" here is an `image` with an `image_url` property equal to `empty://`.


## 28.11.0

# Android Client:
* Added functions to get URL value: `getUrlValue`, `getUrlFromArray`, `getUrlFromDict`.
* Added functions to get Color from Dict: `getOptColorFromDict`, `getDictOptColor`.
* Fixed incorrect height of select.

# iOS Client:
* Supported infinite scrolling in the pager.
* Fixed `change_bounds` animations content scale.

# Web Client:
* Fixed an issue with the `focus` property support in `slider` component.
* Added `focus_element` typed action support.


## 28.10.0

# Android Client:
* Added new evaluable type - EvaluableType.URL
* Added functions `toString(url)`, `toUrl(str)` and `toColor(str)`.
* Added `scale` property support in the `video` component.
* Fixed crash when `DivState` has empty `states` array.
* Fixed crash when clicking "Next" on keyboard in `DivInput` inside `DivGallery`.
* Fixed `DivInput` not working in `DivPager`.

# iOS Client:
* Fixed clipping to bounds of view when rotating.

# Web Client:
* Added `copy_to_clipboard` action support.
* Fixed cross-axis container items with the `match_parent` size logic in combination with the `wrap_content` size of the container itself.
* Fixed `gallery` scrolling with `wrap_content` and `max_size` property.


## 28.9.0

# Android Client:
* Added infinite scrolling property to the pager.
* Added common interface for all div views.
* Added a workaround for a bug in R8 that sometimes causes a `ClassCastException` when using `DivGallery` or `DivGridLayout`.
* Fixed `DivCustom` wrong ids for `DivGallery` items.

# iOS Client:
* Added `VideoDurationExtensionHandler`.

# Web Client:
* Fixed the first/last separator of the `container` with `space-evenly` and `space-around` alignment.
* Added an additional api with `props` and `variables` for `custom` components.
* Added `array_insert_value` and `array_remove_value` typed actions.
* Added support for expressions in the `execAction` method.


## 28.8.0

# Android Client:
* Added optional param `databasePrefix` to `DivStorageComponent#create` method.
* Fixed crash in `DivGallery` with `scroll_mode` set to `paging`.
* Fixed unnecessary bindings after triggers changing DivGallery state before its elements are bound.
* Fixed `DivDataRepositoryImpl.getAll()` card duplicates.
* Fixed variable triggers cleared after patch.

# iOS Client:
* Added `DivImageHolderFactory` protocol.

# Web Client:
* Added `aspect` support for the `video` component.
* Added `text_shadow` support for the `text` component.
* Supported `set_variable` typed action.
* Fixed text cropping in several cases, especially with the `max_lines` property.
* Fixed an issue with the `input` server-side rendering (SSR).


## 28.7.0

# iOS Client:
* Added `DivVariableStorage`.
* Added `scale` property support in the `video` component.

# Web Client:
* Added `custom` component support.


## 28.6.0

# Android Client:

* Fixed width of drawables stroke.
* Fixed runtime warnings and errors clearing when reusing `Div2View` after calling `cleanup`.

# iOS Client:

* Replaced `DivActionSource.custom` with `.timer` and `.trigger`.

# Web Client:

* Added `scale` property support in the `video` component.
* Added `array` variable type, alongside with the couple of functions `getStringFromArray`, `getOptDictFromArray` and others.
* Added aliases for the existing `dict` fucntions: `getStringFromDict` (alias for the `getDictString`) with many others.
* `dict` variables are now shown in markup as json instead of old `<dict>` (new `array`s works the same).
* Fixed zero size support in `shape`s.


## 28.5.1

# iOS Client:

* Changed `variablesStorage` visibility to public.


## 28.5.0

# Android Client:

Additions:
* Updated divkit-demo-app and divkit-lottie okhttp and okio dependencies due to CVE.
* Updated Kotlin to 1.8.22.
* Updated Gradle to 8.3.
* Updated AGP to 8.1.1.
* Implemented `DivVariableController` and since now it should be used instead of `GlobalVariableController`. `GlobalVariableController` was deprecated.

Fixes:
* Fixed `div-input` multikey mask with single pattern element.
* Fixed applying `maxWidth` and `maxHeight` to gallery children.
* Fixed incorrect background color on `div-input` rebind.
* Fixed snapping in `div-gallery` with `paging` `scroll_mode`.
* Fixed `div-pager` visibility action with 100% visibility percentage not called for last item.

# iOS Client:

Additions:
* Added `copy_to_clipboard` action handler.
* Added `array_insert_value` and `array_remove_value` action handlers.
* Added `getArray`/`getDict` functions.

Fixes:
* Fixed clickability of a margin area.

# Web Client:

Deprecation:
* `palette` and `theme` marked as deprecated.

Fixes:
* Fixed `input` multikey mask with the single pattern element.

## 28.4.0

# JSON Schema:
* Added support of ranges in `DivSlider` for Android and iOS

# Android Client:
* Added automatic `ViewPool` optimization flag
* Fixed crash on empty `DivInput` placeholder
* `DivImageLoader` methods should be called on main thread now

# iOS Client:
* Added support of ranges in `DivSlider`
* Fix cursor position in phone input mask
* Supported variable in `DivState`

# Web Client:
* Fixed custom `text` color with focus (without explicit `focused_text_color` property)


## 28.3.0

# Android Client:
Additions:
* Added RTL-mirror filter for `DivImage`
* Added support of ranges in `DivSlider`.
* Added `copy_to_clipboard` typed action.
* Added accessibility support for text ranges
* Added android implementation of `DivImageLoader`: `PicassoDivImageLoader`/`GlideDivImageLoader` using Picasso and Glide accordingly

Fixes:
* Fixed drawing corners during transition
* Fixed slider sticking in the beginning of moving.

# iOS Client:
Additions:
* Implemented `space-between`, `space-around`, `space-evenly` alignments
* Added `DivReporter` protocol.
* Added `set_variable` action handler.

# Web Client:
Additions:
* Added `state_id_variable` support in the `state` component


## 28.2.0

# Android Client:
* Typed actions moved to singleton scope.
* Removed asserts in `DivDataRepository`.
* Locally collect `ViewPool` statistic for optimization.
* Implemented `StoredValuesController`.
* Added ability to patch inside gallery/pager items.
* Fixed shadows overlap container's children.
* Added ability to request focus action.
* Support for gif images in `playground` and `sample-app`.
* `text_shadow` implementation.
* Support alignments with spaces.
* Array variable mutations.
* Fixed the inability to patch an item inside the gallery that inside the other gallery.

# iOS Client:
* Improved `DivSlider` performance.
* Fixed memory leaks in `ExpressionResolver`.
* Fixed `DivContainer` with constrained children height.
* Fixed phone mask in `DivInput`.

# Kotlin JSON Builder:
* Fixed missing * symbol in Url validating regex.


## 28.1.0

# Android Client:
* Support RTL for `DivPager`.
* Added `get<Type>FromDict/get<Type>FromArray` functions for arrays.

# iOS Client:
* Fixed `DivInput` mask.
* Fixed alignment in overlapped `DivContainer`.


## 28.0.0

# JSON Schema:
* Added `text_shadow` property
* Added `ranges` to slider
* Added `array` variable type

# Android Client:

Breaking changes:
* Added ArrayVariable case into Variable class.

Added:
* RTL for div-select
* Support non-linear font scaling
* Phone masks
* Array get functions

# iOS Client:

Braking changes:
* Added array variables support. Added `array` case into `DivVariableValue` enum.
* Improved error types: reduced visibility of `DivBlockModelingError`, `DivBlockModelingWarning` types, `DivError` typealias replaced with protocol.
* Reduced visibility of types and methods not intended to be used outside the framework: `DivLastVisibleBoundsCache`, `DivVisibilityCounter`, `EmptyDivCustomBlockFactory`,

Other changes:
* Added some array functions
* Added phone masks support in `DivInput`
* Added `DivViewPreloader` for `DivView`
* Added `match_parent` size support for tooltips


## 27.4.0

# Android Client:
* Deprecate constructors without LifecycleOwner
* Added aspect ratio for video
* Fixed doubletaps
* Improved visibility action binding performance

# iOS Client:
* Added center alignment for tooltips.
* Fixed Swift 5.7 compatibility.
* Fixed DivInput line height.

# Web Client:
* Added `tooltips` support
* Fixed an issue with the big amount of the unused event listeners on the DOM nodes


## 27.3.0

# JSON Schema:
* Added phone input mask schema
* Added `state_id_variable` to `DivState`

# Android Client:
* Enabled accessibility flag by default
* Fixed state update on rebind
* Fixed showing `transition` from `visible` to `invisible`/`gone` on first draw
* Fixed `SQLiteFullException` in `DivStorage` causing app crash

# iOS Client:
* Added RTL support for `DivImage`, `DivInput`, `DivSlider`.
* Fixed `updateCardAction` calls for elements with variables binding.
* Fixed visibility counters.
* Fixed Lottie and Rive blocks comparison.

# Web Client:
Fixes:
* Fixed an issue with a `gallery` component with the `default_item` outside of the items range


## 27.2.0

# JSON Schema:
* Replaced `left` with `start` alignment by default
* Added `center` `DivTooltip` position
* Added `aspect` to `DivVideo`

# Android Client:
* Fixed `DivTooltip` with `match_parent` size
* Fixed crash caused by `DivTimer` actions execution after `DivView` detach
* Observe `DivVideo` `muted` property
* Updated video player manager
* Improved binding performance

# iOS Client:
* Added RTL-layout support for `DivTabs`.
* Fixed crash in `DivInput` mask validator.

# Web Client:
Additions:
* Added `div-patch`, `download` action and `download_callbacks` support
* Added support for the `focus` parameters (including ``focused_text_color`)


## 27.1.0

# Android Client:
* Lottie version updated to 6.1.0
* Added configuration paramater for scroll interception angle.
* Method DivConfiguration.Builder#enableAccessibility is deprecated now.
* Fixed layout of children with match_parent size inside container with constrained wrap_content size.
* Fixed input layout with constrained wrap_content height.
* Fixed image layout with constrained wrap_content size.
* Fixed visibility tracking of views that has been removed after patch.

# iOS Client:
* Added `DivView`.
* Added RTL support for `DivText`, `DivGallery`, `DivPager` and `DivIndicator`.
* Fixed state management for `DivGallery`.


## 27.0.0

# Android Client:
* Added RTL support for container, tabs, slider and indicator
* Fixed multiple find lifecycleOwner call
* Fixed multiple out animations ignoring or view doubling

# iOS Client:
* Added `getStoredValue` functions support.
* Added RTL layout support in `DivContainer`.
* Added `updateCardSignal` to `DivKitComponents`.
* Replaced `UrlOpener` with `DivUrlHandler` protocol. Changed `DivActionHandler` API.

# Web Client:
Fixes:
* Fixed an issue with the integer values from the several functions: `getYear`, `getMonth`, `getDay`, `getDayOfWeek`, `getHours`, `getMinutes`, `getSeconds`, `getMillis`, `len`, `index` and `lastIndex`. These integer values previously could produce errors in integer operations


## 26.2.0

# JSON Schema:
* Supported `text_alignement_horizontal` and `text_alignement_vertical` properties in `div-input`

# Android Client:
* Added `start` and `end` values to horizontal alignment
* Added `start` and `end` values to paddings and margins
* Supported right-to-left layout direction for `wrap` container
* Supported dictionary functions
* Fixed `visibility_actions` in DivStates

# iOS Client:
* Added `testRegex` function.
* Fixed visibility action for `DivStates`.

# Web Client:
* Supported `text_alignement_horizontal` and `text_alignement_vertical` properties in `input`

## 26.1.0

# JSON Schema:
* Added `start` and `end` to horizontal alignment.

# Android Client:
* Added `testRegex` function.
* Supported `constrained` property in `wrap_content` size for pagers.
* Ignore constrained along wrap-container's cross axis.
* Fixed gallery `item_space` in RTL layout direction.

# iOS Client:
* Ignore constrained along wrap-container's cross axis.
* Supported `start` and `end` parameters in margins, padding.

# Web Client:
* Implemented `testRegex` function
* `content_alignment_` properties along cross-axis in `container` with the `wrap` mode will now align rows / columns of items, instead of children components (to better match Android / iOS behavior).
* Fixed expression processing without variables (constant expressions).

# TypeScript JSON Builder:
* `boolean_int` properties will now accept `true` and `false` values in addition to `1` / `0`. These boolean values will be automatically converted to `1` / `0`, but only if the `divCard` function is used. Beware of old clients with the old DivKit SDK, they may not accept boolean `true` / `false` values.

## 26.0.0

# JSON Schema:
* Added `start` and `end` properties to edge insets.
* Supported user-defined font families.

# Android Client:
* Added profile to configure number of precreated views.
* Supported margins for wrap container separators.
* Supported user-defined font families.
* Supported beacons in swipe_out_actions
* Fixed indicators alignment when they cannot fit to container
* Fixed `release` method not called on destroy
* Fixed sequential patch applyment

# iOS Client:
* Supported user-defined font families.
* Added async image previews loading.
* Fixed expressions support in `DivState.default_state_id`.
* Fixed recurring visibility actions.

# Web Client:
* Fixed `fatal_actions` in the `video` component logic
* Fixed `video` unmount logic
* Incorrect non-array `background`s will not throw error
* Fixed default `visibility_percentage` value in the `disappear_action`
* Fixed `disappear_action` with the percentage equal to `0`
* Allowed expressions in the `visibility_percentage`

# Kotlin JSON Builder:
* Added extension div data creation.

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

## 0.6.0-rc.1

* Update generated schema
* Support nullable end in div-ranges and cloud background fallback
* Clean dependencies
* Add a synchronous method to resolve expression
* Auto generate resolve DTO mechanism
* Rewrite context initialization mechanism to synchronous
* Rewrite conversion mechanism, simplify and standardize
* Rewrite expression analyzer to synchronous

## Migration 0.5.0 → 0.6.0
* Remove DivKitView property `cacheManager` as unsupported to the proper extent
* Remove DivKitView property `loadingBuilder` as unused
* Replace DivKitView props `viewScale` and `textScale` to `scale`
* Remove DivData `preload` and `parse` methods for simplification
* DTO models use the generated resolve instead of extension
* Change prop.resolve(context: context) to prop.resolve(context)
* Now are required to perform the conversion DTO to DivModel
* Due to architecture changes and conversion mechanism changes, you need to migrate your codebase

## 0.5.0

* Add full implementation on Dart of DivKit specification for calculating expressions: Runtime
* Completely remove use of the div_expressions_resolver plugin
* Start to remove additional code intended for using plugin
* Previously organize caching of the execution tree within life cycle of expression
* Fix patches are not applied to items of custom
* Add custom font family provider
* Provide documentation for generated schema

## Migration 0.4 → 0.5
* The DivExpressionResolver method `clearVariables` was removed as unused
* To ensure stability and a large change in the main calculation mechanism,
  you need to check the your application when updating

## 0.4.0

* Use low-level divkit layout implementation: DivLayout
* Optimize pre-calculation of states when switching
* Fix sticky switching of states
* Fix inner objects in templates breaks rendering
* Add support div-text features: font_family, letter_spacing font_weight_value, text_shadow
* Add scenario list to testing page
* Add handling url in playground editor
* Added feature logging management


## Migration 0.3 → 0.4

* No changes in the public API!
* Due to the change in the layout system, we cannot guarantee full compliance with the rendering
  of the previous version, now we use a lower-level, rather than a composition of standard components.

## 0.3.0

* Provide instant rendering of DivKitView, asynchronous parsing and preloading
* Change the layout system, refuse ParentData
* Add call tracking tools
* Fix object parsing in DTO
* Add div-ranges in div-text
* Open access to the classes DivAction, DivTimer, DivVariable, DivPatch, DivDownloadCallbacks and
  converters in the public api
* Add visibility triggers feature
* Add Full support div-background
* Isolate problematic divs with parsing errors
* Expansion of logged incidents
* Public BuildContext in DivContext
* Add div-pager

## Migration 0.2 → 0.3

* Now DTO has changed the folder from generated_sources to schema
* Now DefaultDivKitData needs to be built and preloaded for instant rendering
* Now we have fixed the error and extract the value of dict type values when parsing
  This change affected the following structures: dict_value, dict_variable, div_action, div_custom,
  div_disappear_action, div_extension, div_video, div_visibility_action:
  ```diff
  - action.payload['payload']
  + action.payload
  ```
* The naming of the classes of the public api was changed so as not to conflict with the dto
  classes:
  ```diff
  - DivAction, DivTimer, DivVariable, DivPatch, DivDownloadCallbacks
  + DivActionModel, DivTimerModel, DivVariableModel, DivPatchModel, DivDownloadCallbacksModel
  ```
  
## 0.2.0

* Correct interpretation of dict in variables context
* Add support no_animation type of animation
* Add support of longtap actions
* Remove dispose from public div-context
* Provide DivContext in custom elements
* Add enumeration indexed switching
* Add div-patch feature
* Add copyWith to DTO entities
* Fix testing page in Playground
* Fix error handling in div-action

## Migration 0.1 → 0.2

* Now DivContext specified in div-customs protocol, you need to change signature of function if it
  was used.
  ```diff
  - Widget createCustom(DivCustom div);
  + Widget createCustom(DivCustom div, DivContext context)
  ```
  
## 0.1.5

* Templater support: templates can be inherited, composited, links and transitive links to fields.
* Raised div_expressions_resolver to 0.4.2
* Add scaling of views and texts
* Fix impossibility of multiple inheritance of variable storage
* Fix DTO entities creation from json

## 0.1.4

* Correct README and add documentation.

## 0.1.3

* Make a wide range of versions for flutter_svg
* Try passing through external logging
* Improve playground regression test page

## 0.1.2

* Use more reliable implementation of expressions resolver on codecs
* Use Color type in variables
* Support array and dict collections (most fully)
* Fix state lock when update DivKitView

## 0.1.1

* Use stable expressions resolver
* Add regression test page to Playground

## 0.1.0

* Generate DTO entities from scheme
* Prepare the basic structure and public API of the project
* Prepare project health and stability check
* Implement basic div widgets: container, gallery, image (raster + svg), input, state, text, base
* Implement fundamental features: variables, states, triggers, timers, actions, customs, templating
* Prepare base Playground
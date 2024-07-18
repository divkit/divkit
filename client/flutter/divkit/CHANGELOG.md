## 0.3.0-pre+2
* Add visibility triggers feature

## 0.3.0-pre+1

* Fix object parsing in DTO
* Add ranges in div-text
* Open access to the classes DivAction, DivTimer, DivVariable, DivPatch, DivDownloadCallbacks and
  converters in the public api
* Add visibility triggers feature

## Migration 0.2 → 0.3.0-pre+1

* Now we have fixed the error and extract the value of dict type values when parsing.
  This change affected the following structures: dict_value, dict_variable, div_action, div_custom,
  div_disappear_action, div_extension, div_video, div_visibility_action.
  `-action.payload['payload']`
  `+action.payload`
* The naming of the classes of the public api was changed so as not to conflict with the dto
  classes:
  `-DivAction, DivTimer, DivVariable, DivPatch, DivDownloadCallbacks`
  `+DivActionModel, DivTimerModel, DivVariableModel, DivPatchModel, DivDownloadCallbacksModel`

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
  `-Widget createCustom(DivCustom div);`
  `+Widget createCustom(DivCustom div, DivContext context)`

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
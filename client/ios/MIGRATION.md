# DivKit for iOS migration guide

## Migrating from 32.x to 33.0.0

### Migrate `DivData` resolution

`RawDivData.resolve(flagsInfo:)` and the public `DivDataTemplate` type were removed.

Before:

```swift
let rawData = try RawDivData(dictionary: json)
let result = rawData.resolve(flagsInfo: flagsInfo)
```

After:

```swift
let rawData = try RawDivData(dictionary: json)
let result = DivData.resolve(
  card: rawData.card,
  templates: rawData.templates,
  flagsInfo: flagsInfo
)
```

If code called `DivTemplates.parseValue(type: DivDataTemplate.self, from:)` directly, use the
`DivData.resolve(card:templates:flagsInfo:)` overload that accepts a prebuilt `DivTemplates`.

### Replace removed `DivKitComponents` callbacks

#### Tooltips

The `DivActionHandler.ShowTooltipAction` type and the `showTooltip` parameter of
`DivKitComponents` were removed. Implement `TooltipManager` and pass it through the
`tooltipManager` parameter:

```swift
let components = DivKitComponents(
  tooltipManager: appTooltipManager
)
```

If the integration implements the asynchronous `TooltipActionPerformer` method, rename it and add
main-actor isolation.

Before:

```swift
func showTooltipAsync(info: TooltipInfo) async -> Bool
```

After:

```swift
@MainActor
func showTooltip(info: TooltipInfo) async -> Bool
```

The synchronous `showTooltip(info:)` method is unchanged.

#### Visibility and disappear action reporting

The `DivActionHandler.TrackVisibility` type and the `trackVisibility` and `trackDisappear`
parameters of `DivKitComponents` were removed. Pass a `DivReporter` through the `reporter`
parameter and distinguish events using `DivActionInfo.source`:

```swift
final class AppDivReporter: DivReporter {
  func reportError(cardId: DivCardID, error: DivError) {
    // Forward the error to the application logger.
  }

  func reportAction(context: DivActionHandlingContext) {
    switch context.info.source {
    case .visibility:
      break // Handle visibility.
    case .disappear:
      break // Handle disappear.
    default:
      break
    }
  }
}

let components = DivKitComponents(reporter: AppDivReporter())
```

The event and card identifiers are available as `context.info.logId` and `context.cardId`.

### Replace removed public types

| Removed type | Replacement in 33.0.0 |
|---|---|
| `DivActionURLHandler.UpdateReason` | `DivCardUpdateReason` |
| `DivTimerAction` | `DivActionTimer.Action` |
| `DivVideoAction` | `DivActionVideo.Action` |
| `DivStoredValueScope` | `DivActionSetStoredValue.Scope` |

When replacing `DivVideoAction`, use `.start` instead of `.play`. The `.pause` case is unchanged.
The enum cases of the other types are unchanged.

### Use `UIElementPath` for block state

`DivBlockStateStorage.setState(id:cardId:state:)`, `getState(_:cardId:)`, and
`getStateUntyped(_:cardId:)` were removed. Use their `UIElementPath`-based counterparts.

Before:

```swift
storage.setState(id: elementID, cardId: cardID, state: state)
let savedState: ElementStateType? = storage.getState(elementID, cardId: cardID)
```

After:

```swift
let path = modelingContext.path
storage.setState(path: path, state: state)
let savedState: ElementStateType? = storage.getState(path)
```

Use the actual path from `DivBlockModelingContext.path`, `DivActionInfo.path`, or another available
context. Do not reconstruct a path from `id` alone: elements with the same `id` at different paths
now have independent state.

### Remove direct calls to `extractDivVariableValues`

`Collection<DivVariable>.extractDivVariableValues(_:)` was moved to the `Legacy` SPI.

If it was used to initialize DivKit card variables, remove the manual extraction and pass the card
data to `DivKitComponents`:

```swift
components.setCardData(divData: divData, cardId: cardID)
```

If application code needs the extracted dictionary for non-DivKit logic, there is no stable public
replacement in 33.0.0. The old method remains temporarily available through SPI:

```swift
@_spi(Legacy) import DivKit
```

### Handle generated model type changes

These changes require action only from code that accesses the generated Swift models directly:

| 32.x property or method | Change in 33.0.0 | Required migration |
|---|---|---|
| `DivAction.logId`, `DivVisibilityAction.logId`, `DivDisappearAction.logId` | `Expression<String>` became `Expression<String>?` | Handle a missing value. |
| `DivImage.imageUrl`, `DivGifImage.gifUrl` | `Expression<URL>` became `Expression<URL>?` | Handle a missing URL. |
| `DivShapeDrawable.color` | `Expression<Color>` became `Expression<Color>?` | Handle a missing color. |
| `DivInput.hideSuggestionsBar`, `DivInputTemplate.hideSuggestionsBar`, `resolveHideSuggestionsBar(_:)` | Replaced by `allowSuggestionsBar` and `resolveAllowSuggestionsBar(_:)` | Rename usages and invert the value. |

The first three rows do not require changes to existing JSON: fields that were previously required
are still accepted by DivKit 33.0.0. `hide_suggestions_bar` was not supported on iOS in 32.x, so
replacing it with `allow_suggestions_bar` in raw JSON enables a new feature rather than completing
a required migration. Only code and JSON builders that referenced the old generated property must
rename it and invert the value.

### Check templates that contain a literal and a `$` reference

Value precedence changed when one template field contains both a literal and a `$` reference.
DivKit 33.0.0 uses this order:

1. A value supplied at the template usage site.
2. The value obtained through the `$` reference.
3. The literal as a fallback.

In 32.x, iOS could select the literal before the referenced value. If a template depends on the
old behavior, remove the `$` reference to always use the literal. Remove the literal when it must
not be used as a fallback.

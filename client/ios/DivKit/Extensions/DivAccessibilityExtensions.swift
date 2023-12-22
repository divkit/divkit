import BaseUIPublic
import CommonCorePublic

extension DivAccessibility {
  func resolve(
    _ expressionResolver: ExpressionResolver,
    id: String?,
    customDescriptionProvider: (() -> String?)?
  ) -> AccessibilityElement {
    if resolveMode(expressionResolver) == .exclude {
      return AccessibilityElement(
        traits: .none,
        strings: AccessibilityElement.Strings(label: nil),
        hideElementWithChildren: true
      )
    }

    let label: String?
    if let customDescriptionProvider {
      label = customDescriptionProvider()
    } else if let description = resolveDescription(expressionResolver) {
      label = description
    } else {
      label = type == nil ? nil : ""
    }

    return AccessibilityElement(
      traits: traits,
      strings: AccessibilityElement.Strings(
        label: label,
        hint: resolveHint(expressionResolver),
        value: resolveStateDescription(expressionResolver),
        identifier: id
      ),
      startsMediaSession: resolveMuteAfterAction(expressionResolver)
    )
  }

  private var traits: AccessibilityElement.Traits {
    switch type {
    case .button:
      return .button
    case .header:
      return .header
    case .image:
      return .image
    case .text:
      return .staticText
    case .editText:
      return .searchField
    case .tabBar:
      return .tabBar
    case .list, .select:
      DivKitLogger.warning("Unsupported accessibility type")
      return .none
    case .none?, nil:
      return .none
    }
  }
}

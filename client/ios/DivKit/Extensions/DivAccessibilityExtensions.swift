import BaseUIPublic
import CommonCorePublic

extension DivAccessibility {
  func accessibilityElement(
    divId: String? = nil,
    expressionResolver: ExpressionResolver,
    childrenA11yDescription: String?
  ) -> AccessibilityElement {
    let mode = resolveMode(expressionResolver)
    return AccessibilityElement(
      traits: type?.cast() ?? .none,
      strings: AccessibilityElement.Strings(
        label: makeLabel(
          with: expressionResolver,
          childrenA11yDescription: childrenA11yDescription,
          mode: mode
        ),
        hint: resolveHint(expressionResolver),
        value: resolveStateDescription(expressionResolver),
        identifier: divId
      ),
      startsMediaSession: resolveMuteAfterAction(expressionResolver),
      hideElementWithChildren: mode.isExclude
    )
  }

  private func makeLabel(
    with expressionResolver: ExpressionResolver,
    childrenA11yDescription: String?,
    mode: Mode
  ) -> String? {
    let label = resolveDescription(expressionResolver)
    if mode == .merge, label == nil {
      return childrenA11yDescription
    } else if description == nil, type != nil {
      return ""
    }
    return label
  }
}

extension DivAccessibility.Kind {
  func cast() -> AccessibilityElement.Traits {
    switch self {
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
    case .none:
      return .none
    case .list:
      DivKitLogger.warning("Unsupported accessibility type")
      return .none
    }
  }
}

extension DivAccessibility.Mode {
  var isExclude: Bool {
    switch self {
    case .exclude:
      return true
    case .default, .merge:
      return false
    }
  }
}

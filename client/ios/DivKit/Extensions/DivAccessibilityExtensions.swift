import BaseUIPublic
import CommonCorePublic

extension DivAccessibility {
  func resolve(
    _ context: DivBlockModelingContext,
    id: String?
  ) -> AccessibilityElement {
    let expressionResolver = context.expressionResolver
    let mode = resolveMode(expressionResolver)
    return AccessibilityElement(
      traits: type?.cast() ?? .none,
      strings: AccessibilityElement.Strings(
        label: resolveDescription(context, mode: mode),
        hint: resolveHint(expressionResolver),
        value: resolveStateDescription(expressionResolver),
        identifier: id
      ),
      startsMediaSession: resolveMuteAfterAction(expressionResolver),
      hideElementWithChildren: mode.isExclude
    )
  }

  private func resolveDescription(
    _ context: DivBlockModelingContext,
    mode: Mode
  ) -> String? {
    if let description = resolveDescription(context.expressionResolver) {
      return description
    }
    if mode == .merge {
      return context.childrenA11yDescription
    }
    if type != nil {
      return ""
    }
    return nil
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
    case .list, .select:
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

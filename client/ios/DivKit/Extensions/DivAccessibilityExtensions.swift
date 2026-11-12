import LayoutKit
import VGSL

extension DivAccessibility {
  func resolveMenu(context: DivBlockModelingContext) -> [AccessibilityCustomAction] {
    let expressionResolver = context.expressionResolver
    guard let menu, resolveMode(expressionResolver) != .exclude else { return [] }

    return menu.compactMap { item in
      guard let actions = item.actions.tapUIActions(context: context) else { return nil }
      return AccessibilityCustomAction(
        name: item.resolveLabel(expressionResolver) ?? "",
        actions: actions
      )
    }
  }

  func resolve(
    _ expressionResolver: ExpressionResolver,
    id: String?,
    block: AccessibilityContaining,
    customParams: CustomAccessibilityParams
  ) -> AccessibilityElement {
    let mode = resolveMode(expressionResolver)
    if mode == .exclude {
      return AccessibilityElement(
        traits: .none,
        strings: AccessibilityElement.Strings(label: nil),
        hideElementWithChildren: true
      )
    }

    var label: String?
    if let customDescriptionProvider = customParams.descriptionProvider {
      label = customDescriptionProvider()
    } else if let description = resolveDescription(expressionResolver) {
      label = description
    }
    if mode == .merge, (label ?? "").isEmpty,
       let merged = block.mergedAccessibilityLabel {
      label = merged
    }
    if label == nil, type != .auto, !customParams.allowsNilLabel {
      label = ""
    }

    return AccessibilityElement(
      traits: traits ?? customParams.defaultTraits,
      strings: AccessibilityElement.Strings(
        label: label,
        hint: resolveHint(expressionResolver),
        value: resolveValue(expressionResolver),
        identifier: id
      ),
      startsMediaSession: resolveMuteAfterAction(expressionResolver),
      isContainer: type == .list
    )
  }

  private var traits: AccessibilityElement.Traits? {
    switch type {
    case .button:
      return .button
    case .header:
      return .header
    case .image:
      return .image
    case .text:
      return .staticText
    case .checkbox:
      return .switchButton
    case .editText, .radio, .select, .tabBar:
      DivKitLogger.warning("Unsupported accessibility type")
      return AccessibilityElement.Traits.none
    case .none, .list:
      return AccessibilityElement.Traits.none
    case .auto:
      return nil
    }
  }

  private func resolveValue(_ resolver: ExpressionResolver) -> String? {
    switch type {
    case .checkbox:
      // voice over sounds this as "checked" / "unchecked" for switchButton trait
      resolveIsChecked(resolver) == true ? "1" : "0"
    default:
      resolveStateDescription(resolver)
    }
  }
}

struct CustomAccessibilityParams {
  static let `default` = CustomAccessibilityParams()

  let defaultTraits: AccessibilityElement.Traits
  /// Keeps a `nil` label for any accessibility type, so the element is not exposed
  /// to VoiceOver and its native subviews are reachable instead.
  let allowsNilLabel: Bool
  let descriptionProvider: (() -> String?)?

  init(
    defaultTraits: AccessibilityElement.Traits = .none,
    allowsNilLabel: Bool = false,
    descriptionProvider: (() -> String?)? = nil
  ) {
    self.defaultTraits = defaultTraits
    self.allowsNilLabel = allowsNilLabel
    self.descriptionProvider = descriptionProvider
  }
}

// Copyright 2020 Yandex LLC. All rights reserved.

import UIKit

@available(iOS 10.0, tvOS 10.0, *)
extension AccessibilityElement.Traits {
  public var uiTraits: UIAccessibilityTraits {
    switch self {
    case .button:
      return .button
    case .header:
      return .header
    case .link:
      return .link
    case .image:
      return .image
    case .staticText:
      return .staticText
    case .searchField:
      return .searchField
    case .tabBar:
      return .tabBar
    case .switchButton:
      // https://github.com/akaDuality/AccessibilityTraits/blob/main/AccessibilityTraits/Trait/Traits.swift#L47
      return [.button, UIAccessibilityTraits(rawValue: 1 << 53)]
    case .none:
      return .none
    case .updatesFrequently:
      return .updatesFrequently
    }
  }
}

@available(iOS 10.0, tvOS 10.0, *)
extension UIView {
  public func applyAccessibility(
    _ element: AccessibilityElement?,
    preservingIdentifier: Bool = true,
    forceIsAccessibilityElement: Bool? = nil
  ) {
    guard let element = element else {
      return
    }
    if element.hideElementWithChildren {
      isAccessibilityElement = false
      accessibilityElementsHidden = true
      return
    }
    isAccessibilityElement = forceIsAccessibilityElement ?? (element.strings.label != nil)
    accessibilityLabel = element.strings.label
    accessibilityValue = element.strings.value
    accessibilityTraits = element.traits.uiTraits
    if !element.enabled {
      accessibilityTraits.insert(.notEnabled)
    }
    if element.selected {
      accessibilityTraits.insert(.selected)
    }
    if element.startsMediaSession {
      accessibilityTraits.insert(.startsMediaSession)
    }
    accessibilityHint = element.strings.hint
    if preservingIdentifier {
      accessibilityIdentifier = element.strings.identifier ?? accessibilityIdentifier
    } else {
      accessibilityIdentifier = element.strings.identifier
    }
  }

  public func resetAccessibility() {
    isAccessibilityElement = false
    accessibilityLabel = nil
    accessibilityTraits = UIAccessibilityTraits()
    accessibilityValue = nil
    accessibilityHint = nil
    accessibilityIdentifier = nil
  }
}

extension UIBarButtonItem {
  public func applyAccessibility(
    label: String,
    traits: UIAccessibilityTraits = .button
  ) {
    isAccessibilityElement = true
    accessibilityLabel = label
    accessibilityTraits.insert(traits)
  }
}

extension UIControl {
  public func applyAccessibilityStrings(_ strings: AccessibilityElement.Strings) {
    accessibilityLabel = strings.label
    accessibilityValue = strings.value
    accessibilityHint = strings.hint
    accessibilityIdentifier = strings.identifier
  }
}

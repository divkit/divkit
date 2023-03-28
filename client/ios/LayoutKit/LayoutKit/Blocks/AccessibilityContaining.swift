import BaseUIPublic

public protocol AccessibilityContaining {
  var accessibilityElement: AccessibilityElement? { get }
}

extension AccessibilityContaining {
  public var accessibilityElement: AccessibilityElement? { nil }
}

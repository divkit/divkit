import VGSL

public protocol AccessibilityContaining {
  var accessibilityElement: AccessibilityElement? { get }
  var accessibilityChildren: [AccessibilityContaining] { get }
}

extension AccessibilityContaining {
  public var accessibilityElement: AccessibilityElement? { nil }
  public var accessibilityChildren: [AccessibilityContaining] { [] }
}

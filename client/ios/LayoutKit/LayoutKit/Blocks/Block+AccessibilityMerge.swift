import VGSL

extension AccessibilityContaining {
  public var mergedAccessibilityLabel: String? {
    if let element = accessibilityElement {
      if element.hideElementWithChildren {
        return nil
      }
      if let label = element.strings.label, !label.isEmpty {
        return label
      }
    }
    let parts = accessibilityChildren
      .compactMap(\.mergedAccessibilityLabel)
      .filter { !$0.isEmpty }
    return parts.isEmpty ? nil : parts.joined(separator: " ")
  }
}

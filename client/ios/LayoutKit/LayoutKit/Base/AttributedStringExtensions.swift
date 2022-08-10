import Foundation

extension Optional where Wrapped == NSAttributedString {
  func attribute<T>(key: NSAttributedString.Key, defaultValue: T) -> T {
    guard case let .some(string) = self, !string.isEmpty else {
      return defaultValue
    }

    let value = string.attribute(key, at: 0, effectiveRange: nil) as? T
    return value ?? defaultValue
  }

  var isNilOrEmpty: Bool {
    self == nil || isEmpty
  }

  var isEmpty: Bool {
    self?.length == 0
  }
}

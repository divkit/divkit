import XCTest

extension XCUIElement {
  var hasKeyboardFocus: Bool? {
    let key = "hasKeyboardFocus"
    guard responds(to: NSSelectorFromString(key)) else {
      assertionFailure("XCUIElement no longer exposes \(key)")
      return nil
    }
    return value(forKey: key) as? Bool
  }
}

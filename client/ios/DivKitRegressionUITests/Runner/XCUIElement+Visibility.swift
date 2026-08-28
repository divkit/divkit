import XCTest

extension XCUIElement {
  func waitUntilVisible(
    timeout: TimeInterval
  ) -> Bool {
    wait(timeout: timeout) { element in
      element.isVisible
    }
  }

  func waitUntilVisible(
    withText text: String,
    timeout: TimeInterval
  ) -> Bool {
    wait(timeout: timeout) { element in
      element.isVisible && element.label == text
    }
  }

  var isVisible: Bool {
    guard exists else {
      return false
    }

    let elementFrame = frame
    let windowFrame = AppMainWindow.shared.frame
    guard elementFrame.isValidForVisibility,
          windowFrame.isValidForVisibility else {
      return false
    }

    return windowFrame.contains(
      CGPoint(x: elementFrame.midX, y: elementFrame.midY)
    )
  }

  private func wait(
    timeout: TimeInterval,
    condition: @escaping (XCUIElement) -> Bool
  ) -> Bool {
    let predicate = NSPredicate { object, _ in
      guard let element = object as? XCUIElement else {
        return false
      }
      return condition(element)
    }
    let expectation = XCTNSPredicateExpectation(predicate: predicate, object: self)
    return XCTWaiter.wait(for: [expectation], timeout: timeout) == .completed
  }
}

extension CGRect {
  fileprivate var isValidForVisibility: Bool {
    !isNull && !isInfinite && !isEmpty
  }
}

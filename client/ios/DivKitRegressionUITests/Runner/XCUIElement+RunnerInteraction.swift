import XCTest

extension XCUIElement {
  func interact(
    with target: RunnerTarget,
    operation: (XCUIElement) -> Void
  ) throws {
    let element = try resolveTarget(target, in: self)
    guard element.waitUntilVisible(
      timeout: runnerDefaultTimeout
    ) else {
      throw TargetInteractionError.notVisible(target: target)
    }
    operation(element)
  }

  func verifyText(
    _ expectedText: String,
    of target: RunnerTarget
  ) throws {
    let element = try resolveTarget(target, in: self)
    guard element.waitUntilVisible(
      withText: expectedText,
      timeout: runnerDefaultTimeout
    ) else {
      if !element.isVisible {
        throw TargetInteractionError.notVisible(target: target)
      }
      throw TargetInteractionError.textMismatch(
        target: target,
        expected: expectedText,
        actual: element.label
      )
    }
  }
}

private enum TargetInteractionError: LocalizedError {
  case notVisible(target: RunnerTarget)
  case textMismatch(target: RunnerTarget, expected: String, actual: String)

  var errorDescription: String? {
    switch self {
    case let .notVisible(target):
      return "Target did not become visible: \(target)"
    case let .textMismatch(target, expected, actual):
      let expectedDescription = expected.debugDescription
      let actualDescription = actual.debugDescription
      return "Text mismatch for \(target): expected \(expectedDescription), actual \(actualDescription)"
    }
  }
}

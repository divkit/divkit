import XCTest

func resolveTarget(
  _ target: RunnerTarget,
  in root: XCUIElement
) throws -> XCUIElement {
  let query: XCUIElementQuery = switch target {
  case let .divID(id):
    root.descendants(matching: .any).matching(identifier: id)
  }

  let element = query.firstMatch
  guard element.waitForExistence(timeout: runnerDefaultTimeout) else {
    throw TargetResolutionError.didNotAppear(target: target)
  }

  let matchCount = query.count
  guard matchCount == 1 else {
    throw TargetResolutionError.unexpectedMatchCount(
      target: target,
      matchCount: matchCount
    )
  }
  return element
}

private enum TargetResolutionError: LocalizedError {
  case didNotAppear(target: RunnerTarget)
  case unexpectedMatchCount(target: RunnerTarget, matchCount: Int)

  var errorDescription: String? {
    switch self {
    case let .didNotAppear(target):
      "Target did not appear: \(target)"
    case let .unexpectedMatchCount(target, matchCount):
      "Expected exactly one target, found \(matchCount): \(target)"
    }
  }
}

let runnerDefaultTimeout: TimeInterval = 3

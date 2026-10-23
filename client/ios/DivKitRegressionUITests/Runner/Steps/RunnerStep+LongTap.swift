import XCTest

extension RunnerStep {
  struct LongTap: Decodable {
    let target: RunnerTarget
    let point: Point?
  }
}

extension RunnerExecutor {
  func execute(_ step: RunnerStep.LongTap) throws {
    try root.interact(with: step.target) { element in
      if let point = step.point {
        element.coordinate(withNormalizedOffset: point.vector).press(forDuration: longTapDuration)
      } else {
        element.press(forDuration: longTapDuration)
      }
    }
  }
}

private let longTapDuration: TimeInterval = 1

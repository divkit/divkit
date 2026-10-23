import XCTest

extension RunnerStep {
  struct DoubleTap: Decodable {
    let target: RunnerTarget
    let point: Point?
  }
}

extension RunnerExecutor {
  func execute(_ step: RunnerStep.DoubleTap) throws {
    try root.interact(with: step.target) { element in
      if let point = step.point {
        element.coordinate(withNormalizedOffset: point.vector).doubleTap()
      } else {
        element.doubleTap()
      }
    }
  }
}

import XCTest

extension RunnerStep {
  struct DoubleTap: Decodable {
    let target: RunnerTarget
  }
}

extension RunnerExecutor {
  func execute(_ step: RunnerStep.DoubleTap) throws {
    try root.interact(with: step.target) { $0.doubleTap() }
  }
}

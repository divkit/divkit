import XCTest

extension RunnerStep {
  struct Tap: Decodable {
    let target: RunnerTarget
  }
}

extension RunnerExecutor {
  func execute(_ step: RunnerStep.Tap) throws {
    try root.interact(with: step.target) { $0.tap() }
  }
}

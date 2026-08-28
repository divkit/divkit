import XCTest

extension RunnerStep {
  struct LongTap: Decodable {
    let target: RunnerTarget
  }
}

extension RunnerExecutor {
  func execute(_ step: RunnerStep.LongTap) throws {
    try root.interact(with: step.target) {
      $0.press(forDuration: longTapDuration)
    }
  }
}

private let longTapDuration: TimeInterval = 1

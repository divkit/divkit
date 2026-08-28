import XCTest

extension RunnerStep {
  struct VerifyText: Decodable {
    let target: RunnerTarget
    let text: String
  }
}

extension RunnerExecutor {
  func execute(_ step: RunnerStep.VerifyText) throws {
    try root.verifyText(step.text, of: step.target)
  }
}

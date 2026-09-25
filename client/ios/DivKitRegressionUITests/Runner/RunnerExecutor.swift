import XCTest

@MainActor
struct RunnerExecutor {
  let root: XCUIElement
  let connection: UITestConnection

  func execute(_ steps: [RunnerStep]) async throws {
    for (index, step) in steps.enumerated() {
      do {
        try await execute(step)
      } catch {
        throw StepExecutionError(
          number: index + 1,
          type: step.type,
          underlyingError: error
        )
      }
    }
  }

  private func execute(_ step: RunnerStep) async throws {
    switch step {
    case let .tap(step):
      try execute(step)
    case let .longTap(step):
      try execute(step)
    case let .doubleTap(step):
      try execute(step)
    case let .verifyText(step):
      try execute(step)
    case let .divAction(step):
      try await execute(step)
    }
  }
}

private struct StepExecutionError: LocalizedError {
  let number: Int
  let type: RunnerStep.StepType
  let underlyingError: Error

  var errorDescription: String? {
    "Step \(number) (\(type.rawValue)): \(underlyingError.localizedDescription)"
  }
}

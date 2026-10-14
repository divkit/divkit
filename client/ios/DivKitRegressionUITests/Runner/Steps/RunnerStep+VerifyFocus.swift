import XCTest

extension RunnerStep {
  struct VerifyFocus: Decodable {
    let target: RunnerTarget
    let focused: Bool
  }
}

extension RunnerExecutor {
  func execute(_ step: RunnerStep.VerifyFocus) throws {
    let target = try resolveTarget(step.target, in: root)
    let element = try resolveTextInput(in: target, target: step.target)
    guard element.wait(timeout: runnerDefaultTimeout, condition: {
      $0.exists && $0.hasKeyboardFocus == step.focused
    }) else {
      let actual = element.exists ? element.hasKeyboardFocus : nil
      throw VerifyFocusError.focusMismatch(
        target: step.target,
        expected: step.focused,
        actual: actual
      )
    }
  }
}

private enum VerifyFocusError: LocalizedError {
  case focusMismatch(target: RunnerTarget, expected: Bool, actual: Bool?)

  var errorDescription: String? {
    switch self {
    case let .focusMismatch(target, expected, actual):
      "Focus mismatch for \(target): expected \(expected), "
        + "actual \(actual.map { String($0) } ?? "unavailable")"
    }
  }
}

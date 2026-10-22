import XCTest

extension RunnerStep {
  struct EditText: Decodable {
    let target: RunnerTarget
    let text: String
    let mode: Mode

    enum Mode: String, Decodable {
      case replace
      case append
    }
  }
}

extension RunnerExecutor {
  func execute(_ step: RunnerStep.EditText) throws {
    try root.interact(with: step.target) { element in
      let input = try resolveTextInput(in: element, target: step.target)

      input.tap()

      // Select the range without deleting text first, preserving input filter semantics.
      let length = (input.value as? String)?.utf16.count ?? 0
      for _ in 0..<length {
        input.typeKey(.rightArrow, modifierFlags: [])
      }
      if step.mode == .replace {
        for _ in 0..<length {
          input.typeKey(.leftArrow, modifierFlags: .shift)
        }
      }
      if step.mode == .replace, step.text.isEmpty {
        input.typeText(XCUIKeyboardKey.delete.rawValue)
      }

      for character in step.text {
        input.typeText(String(character))
      }
    }
  }
}

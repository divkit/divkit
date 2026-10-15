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
      let types: [XCUIElement.ElementType] = [.textField, .secureTextField, .textView]
      let input: XCUIElement
      if types.contains(element.elementType) {
        input = element
      } else {
        let inputs = element.descendants(matching: .any).matching(
          NSPredicate(format: "elementType IN %@", types.map(\.rawValue))
        )
        guard inputs.count == 1 else {
          throw EditTextError.invalidInputCount(inputs.count)
        }
        input = inputs.element(boundBy: 0)
      }

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

private enum EditTextError: LocalizedError {
  case invalidInputCount(Int)

  var errorDescription: String? {
    switch self {
    case let .invalidInputCount(count):
      "edit_text requires one native text input, found \(count)"
    }
  }
}

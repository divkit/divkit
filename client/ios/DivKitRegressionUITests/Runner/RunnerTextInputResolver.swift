import XCTest

func resolveTextInput(
  in element: XCUIElement,
  target: RunnerTarget
) throws -> XCUIElement {
  let requiredInputTypes: [XCUIElement.ElementType] = [.textField, .secureTextField, .textView]
  if requiredInputTypes.contains(element.elementType) {
    return element
  }

  var inputs: [XCUIElement] = []
  // Walk through wrappers to find native inputs.
  var elementsToCheck = element.children(matching: .any).allElementsBoundByIndex
  while let candidate = elementsToCheck.popLast() {
    if requiredInputTypes.contains(candidate.elementType) {
      // A non-editable UITextView can expose text lines as accessibility children.
      // Skip the input's children so those lines are not counted as separate inputs.
      inputs.append(candidate)
    } else {
      elementsToCheck.append(
        contentsOf: candidate.children(matching: .any).allElementsBoundByIndex
      )
    }
  }
  guard inputs.count == 1 else {
    throw TextInputResolutionError.invalidInputCount(target: target, count: inputs.count)
  }
  return inputs[0]
}

private enum TextInputResolutionError: LocalizedError {
  case invalidInputCount(target: RunnerTarget, count: Int)

  var errorDescription: String? {
    switch self {
    case let .invalidInputCount(target, count):
      "Expected one native text input accessible to XCTest for \(target), found \(count)"
    }
  }
}

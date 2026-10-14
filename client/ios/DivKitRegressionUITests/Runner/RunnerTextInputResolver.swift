import XCTest

func resolveTextInput(
  in element: XCUIElement,
  target: RunnerTarget
) throws -> XCUIElement {
  let types: [XCUIElement.ElementType] = [.textField, .secureTextField, .textView]
  if types.contains(element.elementType) {
    return element
  }

  let inputs = element.descendants(matching: .any).matching(
    NSPredicate(format: "elementType IN %@", types.map(\.rawValue))
  )
  guard inputs.count == 1 else {
    throw TextInputResolutionError.invalidInputCount(target: target, count: inputs.count)
  }
  return inputs.element(boundBy: 0)
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

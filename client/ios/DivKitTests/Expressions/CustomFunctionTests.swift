@testable import DivKit
@testable import LayoutKit

import XCTest

final class CustomFunctionTests: XCTestCase {
  private let incrementFunction = DivFunction(
    arguments: [DivFunctionArgument(name: "value", type: .integer)],
    body: "@{value + 1}",
    name: "increment",
    returnType: .integer
  )
  private let doubleIncrementFunction = DivFunction(
    arguments: [DivFunctionArgument(name: "value", type: .integer)],
    body: "@{value + 2}",
    name: "increment",
    returnType: .integer
  )
  private let stringLenFunction = DivFunction(
    arguments: [DivFunctionArgument(name: "str", type: .string)],
    body: "@{len(str)}",
    name: "stringLen",
    returnType: .integer
  )
  private let customOverloadedLenFunction = DivFunction(
    arguments: [DivFunctionArgument(name: "str", type: .string)],
    body: "@{str}",
    name: "len",
    returnType: .string
  )
  private let variablesCaptureFunction = DivFunction(
    arguments: [],
    body: "@{len(string_var)}",
    name: "captureFunction",
    returnType: .string
  )
  private let dateTimeFunction = DivFunction(
    arguments: [DivFunctionArgument(name: "datetime", type: .datetime)],
    body: "@{setYear(datetime, 2064)}",
    name: "set2064Year",
    returnType: .datetime
  )
  private let colorFunction = DivFunction(
    arguments: [DivFunctionArgument(name: "color", type: .color)],
    body: "@{color}",
    name: "colorFunction",
    returnType: .color
  )
  private let urlFunction = DivFunction(
    arguments: [DivFunctionArgument(name: "url", type: .url)],
    body: "@{url}",
    name: "urlFunction",
    returnType: .url
  )
  private let dictFunction = DivFunction(
    arguments: [
      DivFunctionArgument(name: "dict", type: .dict),
      DivFunctionArgument(name: "key", type: .string)
    ],
    body: "@{dict.containsKey(key)}",
    name: "containsKeyFunction",
    returnType: .boolean
  )
  private let arrayFunction = DivFunction(
    arguments: [DivFunctionArgument(name: "array", type: .array)],
    body: "@{len(array)}",
    name: "getArrayLengthFunction",
    returnType: .integer
  )
  private let numberFunction = DivFunction(
    arguments: [DivFunctionArgument(name: "number", type: .number)],
    body: "@{number}",
    name: "numberFunction",
    returnType: .number
  )
  private let numberFunctionWithIntegerArgument = DivFunction(
    arguments: [DivFunctionArgument(name: "number", type: .integer)],
    body: "@{number}",
    name: "numberFunction",
    returnType: .number
  )

  private let path = UIElementPath("card_id")
  private let variablesStorage = DivVariablesStorage()
  private var mockReporter = MockReporter()

  private lazy var functionsStorage = DivFunctionsStorage(reporter: mockReporter)
  private lazy var resolver = makeExpressionResolver(path: path)

  private func makeExpressionResolver(path: UIElementPath) -> ExpressionResolver {
    ExpressionResolver(
      path: path,
      variablesStorage: variablesStorage,
      functionsStorage: functionsStorage,
      persistentValuesStorage: DivPersistentValuesStorage(),
      reporter: mockReporter
    )
  }

  override func setUp() {
    variablesStorage.set(cardId: "card_id", variables: variables)
  }

  func test_InvokeCustomFunc_WithStringVariableArgument_ReturnsInteger() {
    functionsStorage.setIfNeeded(path: path, functions: [stringLenFunction])

    XCTAssertEqual(
      resolver.resolveNumeric("@{stringLen(string_var)}"),
      4
    )
  }

  func test_InvokeCustomFunc_WithStringLiteralArgument_ReturnsInteger() {
    functionsStorage.setIfNeeded(path: path, functions: [stringLenFunction])

    XCTAssertEqual(
      resolver.resolveNumeric("@{stringLen('hello')}"),
      5
    )
  }

  func test_InvokeCustomFunc_WithIntVariableArgument_ReturnsInteger() {
    functionsStorage.setIfNeeded(path: path, functions: [incrementFunction])

    XCTAssertEqual(
      resolver.resolveNumeric("@{increment(integer_var)}"),
      124
    )
  }

  func test_InvokeCustomFunc_WithIntLiteralArgument_ReturnsInteger() {
    functionsStorage.setIfNeeded(path: path, functions: [incrementFunction])

    XCTAssertEqual(
      resolver.resolveNumeric("@{increment(123)}"),
      124
    )
  }

  func test_InvokeCustomFunc_WithDictVariableArgument_ReturnsBoolean() {
    functionsStorage.setIfNeeded(path: path, functions: [dictFunction])

    XCTAssertEqual(
      resolver.resolveNumeric("@{containsKeyFunction(dict_var, 'number')}"),
      true
    )
  }

  func test_InvokeCustomFunc_WithArrayVariableArgument_ReturnsInteger() {
    functionsStorage.setIfNeeded(path: path, functions: [arrayFunction])

    XCTAssertEqual(
      resolver.resolveNumeric("@{getArrayLengthFunction(array_var)}"),
      2
    )
  }

  func test_InvokeCustomFunc_WithUrlVariableArgument_ReturnsUrl() {
    functionsStorage.setIfNeeded(path: path, functions: [urlFunction])

    XCTAssertEqual(
      resolver.resolveUrl("@{urlFunction(url_var)}"),
      URL(string: "https://some.url")
    )
  }

  func test_InvokeCustomFunc_WithColorVariableArgument_ReturnsColor() {
    functionsStorage.setIfNeeded(path: path, functions: [colorFunction])

    XCTAssertEqual(
      resolver.resolveColor("@{colorFunction(color_var)}"),
      color("#AABBCC")
    )
  }

  func test_InvokeCustomFunc_WithDatetimeVariableArgument_ReturnsInteger() {
    functionsStorage.setIfNeeded(path: path, functions: [dateTimeFunction])

    XCTAssertEqual(
      resolver.resolveNumeric("@{getYear(set2064Year(nowLocal()))}"),
      2064
    )
  }

  func test_InvokeTwoCustomFunctionsInBody_ReturnsInteger() {
    functionsStorage.setIfNeeded(
      path: path,
      functions: [incrementFunction, stringLenFunction]
    )

    XCTAssertEqual(
      resolver.resolveNumeric("@{stringLen(string_var) + increment(100)}"),
      105
    )
  }

  func test_ParentFunctionIsAvailable() {
    let elementPath = path + "element_id"

    functionsStorage.setIfNeeded(path: path, functions: [incrementFunction])
    functionsStorage.setIfNeeded(path: elementPath, functions: [stringLenFunction])
    let resolver = makeExpressionResolver(path: elementPath)

    XCTAssertEqual(
      resolver.resolveNumeric("@{increment(integer_var)}"),
      124
    )
  }

  func test_ParentFunctionIsAvailable_WithEmptyLocalFunctionsStorage() {
    let elementPath = path + "element_id"

    functionsStorage.setIfNeeded(path: path, functions: [incrementFunction])
    functionsStorage.setIfNeeded(path: elementPath, functions: [])
    let resolver = makeExpressionResolver(path: elementPath)

    XCTAssertEqual(
      resolver.resolveNumeric("@{increment(integer_var)}"),
      124
    )
  }

  func test_InvokeOverloadedCustomFunc_WithIntegerVariableArgument_ReturnsInteger() {
    let elementPath = path + "element_id"
    let resolver = makeExpressionResolver(path: elementPath)
    functionsStorage.setIfNeeded(path: path, functions: [incrementFunction])
    functionsStorage.setIfNeeded(path: elementPath, functions: [doubleIncrementFunction])

    XCTAssertEqual(
      resolver.resolveNumeric("@{increment(integer_var)}"),
      125
    )
  }

  func test_InvokeCustomFunc_WithRecursiveCustomFuncArgument_ReturnsInteger() {
    functionsStorage.setIfNeeded(path: path, functions: [incrementFunction])

    XCTAssertEqual(
      resolver.resolveNumeric("@{increment(increment(123))}"),
      125
    )
  }

  func test_InvokeCustomFunc_WithOverloadedIncludedFuncName_ReturnsString() {
    functionsStorage.setIfNeeded(path: path, functions: [customOverloadedLenFunction])

    XCTAssertEqual(
      resolver.resolveString("@{len(string_var)}"),
      "test"
    )
  }

  func test_InvokeCustomFunc_WithCastIntegerToNumber() {
    functionsStorage.setIfNeeded(path: path, functions: [numberFunction])

    XCTAssertEqual(
      resolver.resolveNumeric("@{numberFunction(integer_var)}"),
      123
    )
  }

  func test_InvokeCustomFunc_WithOverloadedNumberAndIntegerArguments() {
    functionsStorage.setIfNeeded(path: path, functions: [numberFunction, numberFunctionWithIntegerArgument])

    XCTAssertEqual(
      resolver.resolveNumeric("@{numberFunction(integer_var)}"),
      123
    )
    XCTAssertEqual(
      resolver.resolveNumeric("@{numberFunction(number_var)}"),
      12.9
    )
  }

  func test_SetTwoFunctions_WithOneSignature_ReportError() {
    functionsStorage.setIfNeeded(path: path, functions: [incrementFunction, doubleIncrementFunction])

    XCTAssertEqual(mockReporter.lastCardId, path.cardId)
    XCTAssertEqual(
      mockReporter.lastError?.message,
      "Function increment(integer) declared multiple times."
    )
  }

  func test_InvokeCustomFunc_WithLocalVariables_ReturnsNilAndReportError() {
    functionsStorage.setIfNeeded(path: path, functions: [variablesCaptureFunction])

    XCTAssertNil(resolver.resolve("@{captureFunction()}"))
    XCTAssertEqual(mockReporter.lastCardId, path.cardId)
    XCTAssertEqual(
      mockReporter.lastError?.message,
      "Failed to evaluate custom function captureFunction() with arguments: [] and body: @{len(string_var)}. Expression: @{captureFunction()}"
    )
  }

  func test_InvokeMethod_WithCustomFuncName_ReturnsNil() {
    functionsStorage.setIfNeeded(path: path, functions: [incrementFunction])

    XCTAssertNil(resolver.resolve("@{array_var.increment(2)}"))
  }
}

private let variables: DivVariables = [
  "array_var": .array(["value", [true, 123, 123.45] as [AnyHashable]]),
  "boolean_var": .bool(true),
  "color_var": .color(color("#AABBCC")),
  "dict_var": .dict(["boolean": true, "integer": 1, "number": 1.0, "string": "value"]),
  "integer_var": .integer(123),
  "number_var": .number(12.9),
  "string_var": .string("test"),
  "url_var": .url(url("https://some.url")),
]

private final class MockReporter: DivReporter {
  private(set) var lastCardId: DivCardID?
  private(set) var lastError: DivError?

  func reportError(cardId: DivCardID, error: DivError) {
    lastCardId = cardId
    lastError = error
  }
}

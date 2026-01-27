@testable @_spi(Internal) import DivKit
import DivKitTestsSupport
@testable import LayoutKit
import Testing

@Suite
struct CustomFunctionTests {
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
      DivFunctionArgument(name: "key", type: .string),
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

  private let functionsStorage: DivFunctionsStorage
  private let reporter = MockReporter()
  private let resolver: ExpressionResolver
  private let variablesStorage = DivVariablesStorage()

  init() {
    functionsStorage = DivFunctionsStorage(reporter: reporter)
    resolver = ExpressionResolver(
      path: path,
      variablesStorage: variablesStorage,
      functionsStorage: functionsStorage,
      persistentValuesStorage: DivPersistentValuesStorage(),
      reporter: reporter
    )
    variablesStorage.set(cardId: "card_id", variables: variables)
  }

  @Test
  func setCustomFunction() {
    functionsStorage.set(cardId: path.cardId, functions: [incrementFunction])

    #expect(functionsStorage.getStorage(path: path, contains: "increment") != nil)
  }

  @Test
  func invokeCustomFunc_WithStringVariableArgument_ReturnsInteger() {
    functionsStorage.setIfNeeded(path: path, functions: [stringLenFunction])

    #expect(resolver.resolveNumeric("@{stringLen(string_var)}") == 4)
  }

  @Test
  func invokeCustomFunc_WithStringLiteralArgument_ReturnsInteger() {
    functionsStorage.setIfNeeded(path: path, functions: [stringLenFunction])

    #expect(resolver.resolveNumeric("@{stringLen('hello')}") == 5)
  }

  @Test
  func invokeCustomFunc_WithIntVariableArgument_ReturnsInteger() {
    functionsStorage.setIfNeeded(path: path, functions: [incrementFunction])

    #expect(resolver.resolveNumeric("@{increment(integer_var)}") == 124)
  }

  @Test
  func ivokeCustomFunc_WithIntLiteralArgument_ReturnsInteger() {
    functionsStorage.setIfNeeded(path: path, functions: [incrementFunction])

    #expect(resolver.resolveNumeric("@{increment(123)}") == 124)
  }

  @Test
  func invokeCustomFunc_WithDictVariableArgument_ReturnsBoolean() {
    functionsStorage.setIfNeeded(path: path, functions: [dictFunction])

    #expect(resolver.resolveNumeric("@{containsKeyFunction(dict_var, 'number')}") == true)
  }

  @Test
  func invokeCustomFunc_WithArrayVariableArgument_ReturnsInteger() {
    functionsStorage.setIfNeeded(path: path, functions: [arrayFunction])

    #expect(resolver.resolveNumeric("@{getArrayLengthFunction(array_var)}") == 2)
  }

  @Test
  func invokeCustomFunc_WithUrlVariableArgument_ReturnsUrl() {
    functionsStorage.setIfNeeded(path: path, functions: [urlFunction])

    #expect(resolver.resolveUrl("@{urlFunction(url_var)}") == url("https://some.url"))
  }

  @Test
  func invokeCustomFunc_WithColorVariableArgument_ReturnsColor() {
    functionsStorage.setIfNeeded(path: path, functions: [colorFunction])

    #expect(
      resolver.resolveColor("@{colorFunction(color_var)}") ==
        color("#AABBCC")
    )
  }

  @Test
  func invokeCustomFunc_WithDatetimeVariableArgument_ReturnsInteger() {
    functionsStorage.setIfNeeded(path: path, functions: [dateTimeFunction])

    #expect(resolver.resolveNumeric("@{getYear(set2064Year(nowLocal()))}") == 2064)
  }

  @Test
  func invokeTwoCustomFunctionsInBody_ReturnsInteger() {
    functionsStorage.setIfNeeded(
      path: path,
      functions: [incrementFunction, stringLenFunction]
    )

    #expect(resolver.resolveNumeric("@{stringLen(string_var) + increment(100)}") == 105)
  }

  @Test
  func parentFunctionIsAvailable() {
    let elementPath = path + "element_id"

    functionsStorage.setIfNeeded(path: path, functions: [incrementFunction])
    functionsStorage.setIfNeeded(path: elementPath, functions: [stringLenFunction])

    let resolver = makeExpressionResolver(path: elementPath)

    #expect(resolver.resolveNumeric("@{increment(integer_var)}") == 124)
  }

  @Test
  func parentFunctionIsAvailable_WithEmptyLocalFunctionsStorage() {
    let elementPath = path + "element_id"

    functionsStorage.setIfNeeded(path: path, functions: [incrementFunction])
    functionsStorage.setIfNeeded(path: elementPath, functions: [])

    let resolver = makeExpressionResolver(path: elementPath)

    #expect(resolver.resolveNumeric("@{increment(integer_var)}") == 124)
  }

  @Test
  func invokeOverloadedCustomFunc_WithIntegerVariableArgument_ReturnsInteger() {
    let elementPath = path + "element_id"

    functionsStorage.setIfNeeded(path: path, functions: [incrementFunction])
    functionsStorage.setIfNeeded(path: elementPath, functions: [doubleIncrementFunction])

    let resolver = makeExpressionResolver(path: elementPath)

    #expect(resolver.resolveNumeric("@{increment(integer_var)}") == 125)
  }

  @Test
  func invokeCustomFunc_WithRecursiveCustomFuncArgument_ReturnsInteger() {
    functionsStorage.setIfNeeded(path: path, functions: [incrementFunction])

    #expect(resolver.resolveNumeric("@{increment(increment(123))}") == 125)
  }

  @Test
  func invokeCustomFunc_WithOverloadedIncludedFuncName_ReturnsString() {
    functionsStorage.setIfNeeded(path: path, functions: [customOverloadedLenFunction])

    #expect(resolver.resolveString("@{len(string_var)}") == "test")
  }

  @Test
  func invokeCustomFunc_WithCastIntegerToNumber() {
    functionsStorage.setIfNeeded(path: path, functions: [numberFunction])

    #expect(resolver.resolveNumeric("@{numberFunction(integer_var)}") == 123)
  }

  @Test
  func invokeCustomFunc_WithOverloadedNumberAndIntegerArguments() {
    functionsStorage.setIfNeeded(
      path: path,
      functions: [numberFunction, numberFunctionWithIntegerArgument]
    )

    #expect(resolver.resolveNumeric("@{numberFunction(integer_var)}") == 123)
    #expect(resolver.resolveNumeric("@{numberFunction(number_var)}") == 12.9)
  }

  @Test
  func setTwoFunctions_WithOneSignature_ReportError() {
    functionsStorage.setIfNeeded(
      path: path,
      functions: [incrementFunction, doubleIncrementFunction]
    )

    #expect(reporter.lastCardId == path.cardId)
    #expect(reporter.lastError?.message == "Function increment(integer) declared multiple times.")
  }

  @Test
  func invokeCustomFunc_WithLocalVariables_ReturnsNilAndReportError() {
    functionsStorage.setIfNeeded(path: path, functions: [variablesCaptureFunction])

    #expect(resolver.resolve("@{captureFunction()}") == nil)
    #expect(reporter.lastCardId == path.cardId)
    #expect(
      reporter.lastError?.message ==
        "Failed to evaluate custom function captureFunction() with arguments: [] and body: @{len(string_var)}. Expression: @{captureFunction()}"
    )
  }

  @Test
  func invokeMethod_WithCustomFuncName_ReturnsNil() {
    functionsStorage.setIfNeeded(path: path, functions: [incrementFunction])

    #expect(resolver.resolve("@{array_var.increment(2)}") == nil)
  }

  private func makeExpressionResolver(path: UIElementPath) -> ExpressionResolver {
    ExpressionResolver(
      path: path,
      variablesStorage: variablesStorage,
      functionsStorage: functionsStorage,
      persistentValuesStorage: DivPersistentValuesStorage(),
      reporter: reporter
    )
  }
}

private let path = UIElementPath("card_id")

private let variables: DivVariables = [
  "array_var": .array(["value", [true, 123, 123.45] as DivArray]),
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

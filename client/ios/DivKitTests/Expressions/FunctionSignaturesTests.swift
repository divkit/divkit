@testable import DivKit

import XCTest

import CommonCorePublic

final class FunctionSignaturesTests: XCTestCase {
  override class var defaultTestSuite: XCTestSuite {
    makeSuite(input: makeTestCases(), test: runTest)
  }
}

private func makeTestCases() -> [(String, SignatureTestCase)] {
  try! Bundle(for: DivKitTests.self)
    .urls(forResourcesWithExtension: "json", subdirectory: "expression_test_data")!
    .flatMap { url in
      let fileName = url.lastPathComponent
      let testCases = try JSONDecoder()
        .decode(TestCases.self, from: Data(contentsOf: url))
        .signatures ?? []
      return testCases
        .filter { $0.platforms.contains(.ios) }
        .map { ("\(fileName): \($0.name)", $0) }
    }
}

private func runTest(_ testCase: SignatureTestCase) {
  let functionsProvider = FunctionsProvider(
    variableValueProvider: { _ in nil },
    persistentValuesStorage: DivPersistentValuesStorage()
  )
  let functionName = testCase.functionName
  let function = functionsProvider.functions
    .first { symbol, _ in symbol.name == functionName }
    .map { $1 }
  guard let function else {
    XCTFail("Function \(functionName) is not found")
    return
  }

  XCTAssertNoThrow(
    try function.verify(signature: testCase.toSignature),
    "Function signature mismatch"
  )
}

private struct TestCases: Decodable {
  let signatures: [SignatureTestCase]?
}

private struct SignatureTestCase: Decodable {
  let name: String
  let functionName: String
  let arguments: [FunctionSignature.Argument]?
  let resultType: FunctionSignature.ArgumentType
  let platforms: [Platform]

  var toSignature: FunctionSignature {
    FunctionSignature(
      arguments: arguments ?? [],
      resultType: resultType
    )
  }

  private enum CodingKeys: String, CodingKey {
    case name
    case functionName = "function_name"
    case arguments
    case resultType = "result_type"
    case platforms
  }
}

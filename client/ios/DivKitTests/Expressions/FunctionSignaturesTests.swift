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
    .first { name, _ in name == functionName }
    .map { $1 }
  guard let function else {
    XCTFail("Function \(functionName) is not found")
    return
  }

  XCTAssertTrue(
    function.verify(signature: testCase.toSignature),
    "Function signature mismatch"
  )
}

private struct TestCases: Decodable {
  let signatures: [SignatureTestCase]?
}

private struct SignatureTestCase: Decodable {
  let functionName: String
  let arguments: [ArgumentSignature]?
  let resultType: ArgumentType
  let platforms: [Platform]

  var toSignature: FunctionSignature {
    FunctionSignature(
      arguments: arguments ?? [],
      resultType: resultType
    )
  }

  var name: String {
    let args = (arguments ?? [])
      .map { $0.vararg == true ? "vararg \($0.type.rawValue)" : $0.type.rawValue }
      .joined(separator: ", ")
    return "\(functionName)(\(args)) -> \(resultType)"
  }

  private enum CodingKeys: String, CodingKey {
    case functionName = "function_name"
    case arguments
    case resultType = "result_type"
    case platforms
  }
}

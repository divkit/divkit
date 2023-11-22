@testable import DivKit

import XCTest

import CommonCorePublic

final class FunctionSignaturesTests: XCTestCase {
  override class var defaultTestSuite: XCTestSuite {
    makeSuite(input: signatures.map { (name: $0.name, data: $0) }, test: doTest)
  }
}

private func doTest(_ testDto: SuiteDTO.TestDTO) {
  let functionsProvider = FunctionsProvider(
    variableValueProvider: { _ in nil },
    persistentValuesStorage: DivPersistentValuesStorage()
  )
  let functionName = testDto.functionName
  let function = functionsProvider.functions
    .first { symbol, _ in symbol.name == functionName }
    .map { $1 }
  guard let function = function else {
    XCTFail("Function \(functionName) is not found")
    return
  }

  XCTAssertNoThrow(
    try function.verify(signature: testDto.toSignature),
    "Function signature mismatch"
  )
}

private var signatures: [SuiteDTO.TestDTO] {
  (
    try! makeSignatures(for: "function_signatures_datetime") +
      makeSignatures(for: "function_signatures_color") +
      makeSignatures(for: "function_signatures_strings") +
      makeSignatures(for: "function_signatures_std") +
      makeSignatures(for: "function_signatures_math")
  )
  .filter {
    $0.platforms.contains(.ios)
  }
}

private func makeSignatures(for name: String) throws -> [SuiteDTO.TestDTO] {
  let bundle = Bundle(for: DivKitTests.self)
  let subdirectory = "expression_test_data"
  let url = bundle.url(forResource: name, withExtension: "json", subdirectory: subdirectory)!
  let data = try Data(contentsOf: url)
  return try JSONDecoder().decode(SuiteDTO.self, from: data).signatures
}

private struct SuiteDTO: Decodable {
  let signatures: [TestDTO]

  struct TestDTO: Decodable {
    let name: String
    let functionName: String
    let arguments: [FunctionSignature.Argument]?
    let resultType: FunctionSignature.ArgumentType
    let platforms: [Platform]

    var toSignature: FunctionSignature {
      .init(
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
}

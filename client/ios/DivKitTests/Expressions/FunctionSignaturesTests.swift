@testable import DivKit
import VGSL
import XCTest

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
  let arguments: [ArgumentSignature]
  let resultType: Any.Type
  let platforms: [Platform]

  init(from decoder: Decoder) throws {
    let container = try decoder.container(keyedBy: CodingKeys.self)
    functionName = try container.decode(String.self, forKey: .functionName)
    arguments = (try? container.decode([ArgumentSignature].self, forKey: .arguments)) ?? []
    resultType = parseType(try container.decode(String.self, forKey: .resultType))
    platforms = try container.decode([Platform].self, forKey: .platforms)
  }

  var toSignature: FunctionSignature {
    FunctionSignature(arguments: arguments, resultType: resultType)
  }

  var name: String {
    let args = arguments
      .map {
        let type = formatTypeForError($0.type)
        return $0.vararg ? "vararg \(type)" : type
      }
      .joined(separator: ", ")
    return "\(functionName)(\(args)) -> \(formatTypeForError(resultType))"
  }

  private enum CodingKeys: String, CodingKey {
    case functionName = "function_name"
    case arguments
    case resultType = "result_type"
    case platforms
  }
}

extension Function {
  fileprivate func verify(signature: FunctionSignature) -> Bool {
    switch self {
    case let function as SimpleFunction:
      function.signature == signature
    case let function as OverloadedFunction:
      function.functions.contains { $0.verify(signature: signature) }
    default:
      false
    }
  }
}

extension FunctionSignature: Swift.Equatable {
  public static func ==(lhs: FunctionSignature, rhs: FunctionSignature) -> Bool {
    lhs.arguments == rhs.arguments && lhs.resultType == rhs.resultType
  }
}

extension ArgumentSignature: Swift.Decodable {
  public init(from decoder: Decoder) throws {
    let container = try decoder.container(keyedBy: CodingKeys.self)
    self.init(
      type: parseType(try container.decode(String.self, forKey: .type)),
      vararg: (try? container.decode(Bool.self, forKey: .vararg)) ?? false
    )
  }

  private enum CodingKeys: String, CodingKey {
    case type
    case vararg
  }
}

extension ArgumentSignature: Swift.Equatable {
  public static func ==(lhs: ArgumentSignature, rhs: ArgumentSignature) -> Bool {
    lhs.type == rhs.type && lhs.vararg == rhs.vararg
  }
}

private func parseType(_ type: String) -> Any.Type {
  switch type {
  case "array":
    [AnyHashable].self
  case "boolean":
    Bool.self
  case "color":
    RGBAColor.self
  case "datetime":
    Date.self
  case "dict":
    [String: AnyHashable].self
  case "integer":
    Int.self
  case "number":
    Double.self
  case "string":
    String.self
  case "url":
    URL.self
  default:
    Error.self
  }
}

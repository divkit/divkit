import DivKit
import DivKitTestsSupport
import XCTest

final class DivDataParsingTests: XCTestCase {
  override class var defaultTestSuite: XCTestSuite {
    makeSuite(input: makeTestCases(), test: runTest)
  }
}

private func runTest(_ data: TestData) {
  let typedResult = DivData.resolve(
    card: data.card,
    templates: data.templates,
    flagsInfo: DivFlagsInfo(useUntypedTemplateResolver: false)
  )
  let untypedResult = DivData.resolve(
    card: data.card,
    templates: data.templates,
    flagsInfo: DivFlagsInfo(useUntypedTemplateResolver: true)
  )
  let expectedResult = DivData.resolve(card: data.expectedCard, templates: [:])

  XCTAssertNil(
    expectedResult.errorsOrWarnings,
    "Test data corrupted: expected result contains errors"
  )

  assertEqual(typedResult.value, expectedResult.value)
  assertEqual(untypedResult.value, expectedResult.value)

  XCTAssertEqual(
    typedResult.errorsOrWarnings?.count ?? 0,
    data.expectedErrorCount,
    "Expected error count does not match actual result"
  )
  XCTAssertEqual(
    untypedResult.errorsOrWarnings?.count ?? 0,
    typedResult.errorsOrWarnings?.count ?? 0,
    "Untyped pipeline should produce same number of warnings/errors as typed"
  )
}

private func makeTestCases() -> [(String, TestData)] {
  try! getFiles(
    "parsing_test_data",
    forBundle: Bundle(for: DivDataParsingTests.self)
  ).map { url in
    let testName = url.pathComponents
      .trimmingPrefix { $0 != "parsing_test_data" }
      .dropFirst()
      .joined(separator: "/")
      .dropLast(5) // .json
    let data = try Data(contentsOf: url)
    let json = try JSONSerialization.jsonObject(with: data) as! [String: Any]
    return ("\(testName)", TestData(json: json))
  }
}

private struct TestData {
  let card: [String: Any]
  let templates: [String: Any]
  let expectedCard: [String: Any]
  let expectedErrorCount: Int

  init(json: [String: Any]) {
    card = json["card"] as! [String: Any]
    templates = json["templates"] as? [String: Any] ?? [:]

    let expectedJson = json["expected"] as! [String: Any]
    expectedCard = expectedJson["card"] as! [String: Any]
    expectedErrorCount = expectedJson["error_count"] as? Int ?? 0
  }
}

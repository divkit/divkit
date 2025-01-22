import DivKit
import XCTest

final class DivDataParsingTests: XCTestCase {
  override class var defaultTestSuite: XCTestSuite {
    makeSuite(input: makeTestCases(), test: runTest)
  }
}

private func runTest(_ data: TestData) {
  let result = DivData.resolve(card: data.card, templates: data.templates)
  let expectedResult = DivData.resolve(card: data.expectedCard, templates: [:])

  XCTAssertNil(
    expectedResult.errorsOrWarnings,
    "Test data corrupted: expected result contains errors"
  )

  assertEqual(result.value, expectedResult.value)

  XCTAssertEqual(
    result.errorsOrWarnings?.count ?? 0,
    data.expectedErrorCount,
    "Expected error count does not match actual result"
  )
}

private func getFiles(_ path: String) -> [URL] {
  let bundle = Bundle(for: DivDataParsingTests.self)
  var files: [URL] = []
  try! FileManager.default
    .contentsOfDirectory(atPath: bundle.path(forResource: path, ofType: nil)!)
    .forEach {
      if $0.hasSuffix(".json") {
        files.append(bundle.url(forResource: $0, withExtension: nil, subdirectory: path)!)
      } else {
        files.append(contentsOf: getFiles("\(path)/\($0)"))
      }
    }
  return files
}

private func makeTestCases() -> [(String, TestData)] {
  try! getFiles("parsing_test_data").map { url in
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

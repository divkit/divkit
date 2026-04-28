import XCTest

final class AppRouterTests: XCTestCase {
  private let dummyURL = URL(string: "file:///dummy.json")!

  // MARK: - open test by id

  func test_openTest_byValidId_setsRegressionTest() {
    let router = makeRouter()
    router.handle(URL(string: "playground://test?id=42")!)
    XCTAssertEqual(router.pendingRegressionTest?.title, "Simple animation")
  }

  func test_openTest_byInvalidId_leavesNil() {
    let router = makeRouter()
    router.handle(URL(string: "playground://test?id=999")!)
    XCTAssertNil(router.pendingRegressionTest)
  }

  func test_openTest_byNonIntId_leavesNil() {
    let router = makeRouter()
    router.handle(URL(string: "playground://test?id=abc")!)
    XCTAssertNil(router.pendingRegressionTest)
  }

  // MARK: - open test by title

  func test_openTest_byExactTitle_setsRegressionTest() {
    let router = makeRouter()
    router.handle(URL(string: "playground://test?title=Variable%20test")!)
    XCTAssertEqual(router.pendingRegressionTest?.caseId, 100)
  }

  func test_openTest_byTitleCaseInsensitive_setsRegressionTest() {
    let router = makeRouter()
    router.handle(URL(string: "playground://test?title=VARIABLE%20TEST")!)
    XCTAssertEqual(router.pendingRegressionTest?.caseId, 100)
  }

  func test_openTest_byUnknownTitle_leavesNil() {
    let router = makeRouter()
    router.handle(URL(string: "playground://test?title=Unknown")!)
    XCTAssertNil(router.pendingRegressionTest)
  }

  func test_openTest_noParams_leavesNil() {
    let router = makeRouter()
    router.handle(URL(string: "playground://test")!)
    XCTAssertNil(router.pendingRegressionTest)
  }

  // MARK: - open json

  func test_openJson_validUrl_setsPlaygroundURL() {
    let router = makeRouter()
    router.handle(URL(string: "playground://json?url=https%3A%2F%2Fexample.com%2Fcard.json")!)
    XCTAssertEqual(router.pendingPlaygroundURL?.absoluteString, "https://example.com/card.json")
  }

  func test_openJson_missingParam_leavesNil() {
    let router = makeRouter()
    router.handle(URL(string: "playground://json")!)
    XCTAssertNil(router.pendingPlaygroundURL)
  }

  func test_openJson_emptyUrl_leavesNil() {
    let router = makeRouter()
    router.handle(URL(string: "playground://json?url=")!)
    XCTAssertNil(router.pendingPlaygroundURL)
  }

  // MARK: - wrong scheme / host

  func test_wrongScheme_ignored() {
    let router = makeRouter()
    router.handle(URL(string: "other://test?id=42")!)
    XCTAssertNil(router.pendingRegressionTest)
  }

  func test_unknownHost_ignored() {
    let router = makeRouter()
    router.handle(URL(string: "playground://unknown?id=42")!)
    XCTAssertNil(router.pendingRegressionTest)
    XCTAssertNil(router.pendingPlaygroundURL)
  }

  private func makeTests() -> [RegressionTestModel] {
    [
      RegressionTestModel(caseId: 42, title: "Simple animation", url: dummyURL),
      RegressionTestModel(caseId: 100, title: "Variable test", url: dummyURL),
      RegressionTestModel(
        caseId: nil,
        title: "No ID test",
        url: URL(string: "file:///other.json")!
      ),
    ]
  }

  private func makeRouter() -> AppRouter {
    AppRouter(tests: makeTests())
  }
}

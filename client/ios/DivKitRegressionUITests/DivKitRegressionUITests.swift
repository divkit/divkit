import Foundation
import XCTest

final class DivKitRegressionUITests: XCTestCase {
  private let app = XCUIApplication()

  override class var defaultTestSuite: XCTestSuite {
    let suite = XCTestSuite(forTestCaseClass: self)

    do {
      for scenario in try loadAutomatedScenarios(from: Bundle(for: self)) {
        addTest(
          named: testName(for: scenario),
          to: suite
        ) { testCase in
          do {
            try testCase.app.openRegressionCase(scenario.caseID)
            try testCase.app.waitUntilRunning()
            let root = try testCase.app.waitForRootDivView()
            try RunnerExecutor(root: root).execute(scenario.steps)
          } catch {
            testCase.attachDiagnostics()
            XCTFail(
              "Case \(scenario.caseID) (\(scenario.relativePath)): "
                + error.localizedDescription
            )
          }
        }
      }
    } catch {
      addTest(named: "testScenarioLoading", to: suite) { _ in
        XCTFail("Failed to load automated regression scenarios: \(error.localizedDescription)")
      }
    }

    return suite
  }

  override func setUpWithError() throws {
    try super.setUpWithError()
    continueAfterFailure = false
  }

  override func tearDownWithError() throws {
    if app.state != .notRunning {
      app.terminate()
    }
    try super.tearDownWithError()
  }

  private static func addTest(
    named name: String,
    to suite: XCTestSuite,
    body: @escaping (DivKitRegressionUITests) -> Void
  ) {
    let block: @convention(block) (XCTestCase) -> Void = { testCase in
      guard let testCase = testCase as? DivKitRegressionUITests else {
        XCTFail("Unexpected test case type: \(type(of: testCase))")
        return
      }
      body(testCase)
    }
    let selector = NSSelectorFromString(name)
    class_addMethod(self, selector, imp_implementationWithBlock(block), "v@:")
    suite.addTest(self.init(selector: selector))
  }

  private static func testName(for scenario: RunnerScenario) -> String {
    let path = scenario.relativePath
      .components(separatedBy: CharacterSet.alphanumerics.inverted)
      .filter { !$0.isEmpty }
      .joined(separator: "_")
    return "testCase_\(scenario.caseID)_\(path)"
  }

  private func attachDiagnostics() {
    guard app.state != .notRunning else {
      return
    }

    let screenshot = XCTAttachment(screenshot: app.screenshot())
    screenshot.name = "Failure screenshot"
    screenshot.lifetime = .keepAlways
    add(screenshot)

    let hierarchy = XCTAttachment(string: app.debugDescription)
    hierarchy.name = "Accessibility hierarchy"
    hierarchy.lifetime = .keepAlways
    add(hierarchy)
  }
}

extension XCUIApplication {
  fileprivate func waitUntilRunning() throws {
    guard wait(for: .runningForeground, timeout: 10) else {
      throw AppError.appDidNotReachForeground
    }
  }

  fileprivate func openRegressionCase(_ caseID: Int) throws {
    var components = URLComponents()
    components.scheme = "playground"
    components.host = "test"
    components.queryItems = [URLQueryItem(name: "id", value: String(caseID))]

    guard let url = components.url else {
      throw AppError.invalidDeepLink(caseID: caseID)
    }

    open(url)
  }

  fileprivate func waitForRootDivView() throws -> XCUIElement {
    let element = windows.element(boundBy: 0)
      .descendants(matching: .any)
      .matching(identifier: "baseDivView")
      .firstMatch

    guard element.waitForExistence(timeout: 5) else {
      throw AppError.rootDivViewDidNotAppear
    }
    return element
  }
}

private enum AppError: LocalizedError {
  case appDidNotReachForeground
  case rootDivViewDidNotAppear
  case invalidDeepLink(caseID: Int)

  var errorDescription: String? {
    switch self {
    case .appDidNotReachForeground:
      "App did not reach foreground"
    case .rootDivViewDidNotAppear:
      "Root DivView did not appear"
    case let .invalidDeepLink(caseID):
      "Could not create a deep link for case \(caseID)"
    }
  }
}

import Foundation
import XCTest

final class DivKitRegressionUITests: XCTestCase {
  override class var defaultTestSuite: XCTestSuite {
    let suite = XCTestSuite(forTestCaseClass: self)

    do {
      let configuration = try UITestConfiguration()
      let scenarios = try selectedScenarios(
        loadScenarios(from: configuration.scenariosDirectoryURL),
        configuration: configuration
      )
      try validateTestNames(scenarios, configuration: configuration)

      for scenario in scenarios {
        let resourcePath = configuration.scenarioResourcePath(for: scenario.relativePath)
        addTest(
          named: testName(for: resourcePath),
          to: suite
        ) { testCase in
          do {
            let connection = try await UITestConnection()
            defer { connection.close() }
            let launchArguments: [UITestLaunchArgument] = [
              .scenarioPath(resourcePath),
              .connectionPort(connection.port),
              .snapshotTesting,
            ]
            testCase.app.launch(launchArguments: launchArguments)
            try testCase.app.waitUntilRunning()
            let root = try testCase.app.waitForRootDivView()
            try await RunnerExecutor(
              root: root,
              connection: connection,
              verifySnapshotPerformer: VerifySnapshotPerformer(
                referenceSnapshotsDirectoryURL: configuration.referenceSnapshotsDirectoryURL,
                scenarioPath: scenario.relativePath
              )
            ).execute(scenario.steps)
          } catch {
            testCase.attachDiagnostics()
            XCTFail(
              "\(scenario.relativePath): "
                + error.localizedDescription
            )
          }
        }
      }
    } catch {
      addTest(named: "testScenarioLoading", to: suite) { _ in
        XCTFail("Failed to load UI scenarios: \(error.localizedDescription)")
      }
    }

    return suite
  }

  private let app = XCUIApplication()

  override func setUpWithError() throws {
    try super.setUpWithError()
    continueAfterFailure = false
    executionTimeAllowance = 120
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
    body: @escaping @MainActor (DivKitRegressionUITests) async -> Void
  ) {
    let block: @convention(block) (XCTestCase) -> Void = { testCase in
      guard let testCase = testCase as? DivKitRegressionUITests else {
        XCTFail("Unexpected test case type: \(type(of: testCase))")
        return
      }
      let completion = XCTestExpectation(description: name)
      Task { @MainActor in
        await body(testCase)
        completion.fulfill()
      }
      let result = XCTWaiter().wait(for: [completion])
      XCTAssertEqual(result, .completed, "Scenario \(name) did not finish")
    }
    let selector = NSSelectorFromString(name)
    class_addMethod(self, selector, imp_implementationWithBlock(block), "v@:")
    suite.addTest(self.init(selector: selector))
  }

  private static func selectedScenarios(
    _ scenarios: [RunnerScenario],
    configuration: UITestConfiguration
  ) throws -> [RunnerScenario] {
    guard let selector = configuration.scenarioFilter else {
      return scenarios
    }

    let selected = scenarios.filter {
      configuration.scenarioResourcePath(for: $0.relativePath) == selector
    }
    guard !selected.isEmpty else {
      throw TestRegistrationError.noMatchingScenario(selector)
    }
    return selected
  }

  private static func validateTestNames(
    _ scenarios: [RunnerScenario],
    configuration: UITestConfiguration
  ) throws {
    var pathsByName: [String: String] = [:]
    for scenario in scenarios {
      let resourcePath = configuration.scenarioResourcePath(for: scenario.relativePath)
      let name = testName(for: resourcePath)
      if let previousPath = pathsByName[name] {
        throw TestRegistrationError.duplicateName(
          name: name,
          firstPath: previousPath,
          secondPath: scenario.relativePath
        )
      }
      pathsByName[name] = scenario.relativePath
    }
  }

  private static func testName(for resourcePath: String) -> String {
    let path = resourcePath
      .components(separatedBy: CharacterSet.alphanumerics.inverted)
      .filter { !$0.isEmpty }
      .joined(separator: "_")
    return "test_\(path)"
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
  fileprivate func launch(launchArguments: [UITestLaunchArgument]) {
    self.launchArguments += launchArguments.map(\.rawValue)
    launch()
  }

  fileprivate func waitUntilRunning() throws {
    guard wait(for: .runningForeground, timeout: 10) else {
      throw AppError.appDidNotReachForeground
    }
  }

  fileprivate func waitForRootDivView() throws -> XCUIElement {
    let element = windows.element(boundBy: 0)
      .descendants(matching: .any)
      .matching(identifier: "rootDivView")
      .firstMatch

    try waitForCardOrLoadingError(root: element)
    return element
  }

  private func waitForCardOrLoadingError(root: XCUIElement) throws {
    let loadingError = staticTexts["uiTestLoadError"]
    guard root.wait(timeout: 5, condition: { $0.exists || loadingError.exists }) else {
      throw AppError.cardLoadingTimedOut
    }
    if loadingError.exists {
      throw AppError.cardLoadingFailed(loadingError.label)
    }
  }
}

private enum TestRegistrationError: LocalizedError {
  case noMatchingScenario(String)
  case duplicateName(name: String, firstPath: String, secondPath: String)

  var errorDescription: String? {
    switch self {
    case let .noMatchingScenario(selector):
      "No iOS UI scenario matches: \(selector)"
    case let .duplicateName(name, firstPath, secondPath):
      "Duplicate test name: \(name)\nConflicting scenarios:\n- \(firstPath)\n- \(secondPath)"
    }
  }
}

private enum AppError: LocalizedError {
  case appDidNotReachForeground
  case cardLoadingTimedOut
  case cardLoadingFailed(String)

  var errorDescription: String? {
    switch self {
    case .appDidNotReachForeground:
      "App did not reach foreground"
    case .cardLoadingTimedOut:
      "Neither the card nor a loading error appeared within 5 seconds"
    case let .cardLoadingFailed(message):
      message
    }
  }
}

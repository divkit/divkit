@testable import DivKit

import XCTest

final class DivActionHandlerTests: XCTestCase {
  private var actionHandler: DivActionHandler!
  private let logger = MockActionLogger()
  private let reporter = MockReporter()
  private let variablesStorage = DivVariablesStorage()

  private var handledUrl: URL?

  override func setUp() {
    actionHandler = DivActionHandler(
      stateUpdater: DefaultDivStateManagement(),
      patchProvider: MockPatchProvider(),
      variablesStorage: variablesStorage,
      updateCard: { _ in },
      logger: logger,
      urlHandler: DivUrlHandlerDelegate { url, _ in self.handledUrl = url },
      reporter: reporter
    )
  }

  func test_UrlPassedToUrlHandler() {
    handle(
      DivAction(
        logId: "test_log_id",
        url: .value(url("https://some.url"))
      )
    )

    XCTAssertEqual(url("https://some.url"), handledUrl)
  }

  func test_UrlPassedToUrlHandler_IfTypedActionNotHandled() {
    handle(
      DivAction(
        logId: "test_log_id",
        typed: .divActionFocusElement(
          DivActionFocusElement(elementId: .value("id"))
        ),
        url: .value(url("https://some.url"))
      )
    )

    XCTAssertEqual(url("https://some.url"), handledUrl)
  }

  func test_UrlNotPassedToUrlHandler_IfTypedActionHandled() {
    handle(
      DivAction(
        logId: "test_log_id",
        typed: .divActionSetVariable(
          DivActionSetVariable(
            value: .stringValue(StringValue(value: .value("new value"))),
            variableName: .value("string_var")
          )
        ),
        url: .value(url("https://some.url"))
      )
    )

    XCTAssertNil(handledUrl)
  }

  func test_SetVariableAction_SetsStringVariable() {
    variablesStorage.set(
      cardId: cardId,
      variables: ["string_var": .string("default")]
    )

    handle(
      DivAction(
        logId: "test_log_id",
        typed: .divActionSetVariable(
          DivActionSetVariable(
            value: .stringValue(StringValue(value: .value("new value"))),
            variableName: .value("string_var")
          )
        )
      )
    )

    XCTAssertEqual("new value", getVaribaleValue("string_var"))
  }

  func test_SetVariableAction_SetsArrayVariable() {
    variablesStorage.set(
      cardId: cardId,
      variables: ["array_var": .array([])]
    )

    handle(
      DivAction(
        logId: "test_log_id",
        typed: .divActionSetVariable(
          DivActionSetVariable(
            value: .arrayValue(ArrayValue(value: .value(["value 1", "value 2"]))),
            variableName: .value("array_var")
          )
        )
      )
    )

    XCTAssertEqual(["value 1", "value 2"], getVaribaleValue("array_var"))
  }

  func test_LoggerIsCalled() {
    handle(
      DivAction(
        logId: "test_log_id",
        logUrl: .value(url("https://some.log.url")),
        payload: ["key": "value"],
        referer: .value(url("https://some.referer.url")),
        url: .value(url("https://some.url"))
      )
    )

    XCTAssertEqual(url("https://some.log.url"), logger.lastUrl)
    XCTAssertEqual(url("https://some.referer.url"), logger.lastReferer)
    XCTAssertEqual(["key": "value"], logger.lastPayload as! [String: AnyHashable])
  }

  func test_ActionIsReported() {
    handle(
      DivAction(
        logId: "test_log_id",
        url: .value(url("https://some.url"))
      )
    )

    XCTAssertEqual(cardId, reporter.lastCardId)
    XCTAssertEqual("test_log_id", reporter.lastActionInfo?.logId)
  }

  private func handle(_ action: DivActionBase) {
    actionHandler.handle(
      action,
      cardId: cardId,
      source: .tap,
      sender: nil
    )
  }

  private func getVaribaleValue<T>(_ name: DivVariableName) -> T? {
    variablesStorage.makeVariables(for: cardId)[name]?.typedValue()
  }
}

private let cardId: DivCardID = "test_card"

private func url(_ string: String) -> URL {
  URL(string: string)!
}

private final class MockActionLogger: DivActionLogger {
  private(set) var lastUrl: URL?
  private(set) var lastReferer: URL?
  private(set) var lastPayload: [String: Any]?

  func log(url: URL, referer: URL?, payload: [String: Any]?) {
    lastUrl = url
    lastReferer = referer
    lastPayload = payload
  }
}

private final class MockPatchProvider: DivPatchProvider {
  func getPatch(url _: URL, completion _: @escaping DivPatchProviderCompletion) {}

  func cancelRequests() {}
}

private final class MockReporter: DivReporter {
  private(set) var lastCardId: DivCardID?
  private(set) var lastActionInfo: DivActionInfo?
  private(set) var lastError: DivError?

  func reportAction(cardId: DivCardID, info: DivActionInfo) {
    lastCardId = cardId
    lastActionInfo = info
  }

  func reportError(cardId: DivCardID, error: DivError) {
    lastCardId = cardId
    lastError = error
  }
}

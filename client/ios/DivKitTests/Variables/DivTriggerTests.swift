@testable import DivKit

import XCTest

final class DivTriggerTests: XCTestCase {
  private let variablesStorage = DivVariablesStorage()

  private lazy var actionHandler = DivActionHandler(
    stateUpdater: FakeDivStateUpdater(),
    patchProvider: FakeDivPatchDownloader(),
    variablesStorage: variablesStorage,
    updateCard: { _, _ in },
    showTooltip: { _ in }
  )

  private lazy var triggerStorage = DivTriggersStorage(
    variablesStorage: variablesStorage,
    actionHandler: actionHandler,
    urlOpener: { [unowned self] in
      self.openedUrls.append($0)
    }
  )

  private var openedUrls: [URL] = []

  func test_WhenVariableDeclared_PerformsActions() {
    for mode in DivTrigger.Mode.allCases {
      let trigger = DivTrigger(
        actions: [DivAction(logId: "1", url: .value(actionURL))],
        condition: .link(ExpressionLink(rawValue: "@{sample_variable}", validator: nil)!),
        mode: .value(mode)
      )
      triggerStorage.set(cardId: "id", triggers: [trigger])
      variablesStorage.set(variables: ["sample_variable": .bool(true)], triggerUpdate: true)
    }

    XCTAssertEqual(openedUrls, [actionURL, actionURL])
  }

  func test_WhenTriggersSetAfterVariableDeclaration_PerformsActions() {
    for mode in DivTrigger.Mode.allCases {
      let trigger = DivTrigger(
        actions: [DivAction(logId: "1", url: .value(actionURL))],
        condition: .link(ExpressionLink(rawValue: "@{sample_variable}", validator: nil)!),
        mode: .value(mode)
      )
      variablesStorage.set(variables: ["sample_variable": .bool(true)], triggerUpdate: true)
      triggerStorage.set(cardId: "id", triggers: [trigger])
    }

    XCTAssertEqual(openedUrls, [actionURL, actionURL])
  }
}

private class FakeDivStateUpdater: DivStateUpdater {
  func set(
    path _: DivStatePath,
    cardId _: DivCardID,
    lifetime _: DivStateLifetime
  ) {}
}

private class FakeDivPatchDownloader: DivPatchProvider {
  func getPatch(
    url _: URL,
    completion _: @escaping DivPatchProviderCompletion
  ) {}

  func cancelRequests() {}
}

private let actionURL = URL(string: "action://host")!

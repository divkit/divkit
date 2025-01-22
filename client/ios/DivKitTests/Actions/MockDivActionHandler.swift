@testable @_spi(Internal) import DivKit
import Foundation
import LayoutKit
import XCTest

extension DivActionHandler {
  convenience init(
    blockStateStorage: DivBlockStateStorage = DivBlockStateStorage(),
    flags: DivFlagsInfo = .default,
    idToPath: IdToPath = IdToPath(),
    persistentValuesStorage: DivPersistentValuesStorage = DivPersistentValuesStorage(),
    reporter: DivReporter = DefaultDivReporter(),
    stateManagement: DivStateManagement = DefaultDivStateManagement(),
    updateCard: @escaping DivActionURLHandler.UpdateCardAction = { _ in },
    urlHandler: DivUrlHandler = DivUrlHandlerDelegate { _, _ in },
    variablesStorage: DivVariablesStorage = DivVariablesStorage()
  ) {
    self.init(
      stateUpdater: stateManagement,
      blockStateStorage: blockStateStorage,
      patchProvider: MockPatchProvider(),
      submitter: MockSubmitter(),
      variablesStorage: variablesStorage,
      functionsStorage: nil,
      updateCard: updateCard,
      showTooltip: nil,
      tooltipActionPerformer: nil,
      trackVisibility: { _, _ in },
      trackDisappear: { _, _ in },
      performTimerAction: { _, _, _ in },
      urlHandler: urlHandler,
      persistentValuesStorage: persistentValuesStorage,
      reporter: reporter,
      idToPath: idToPath,
      animatorController: DivAnimatorController(),
      flags: flags
    )
  }
}

private final class TestReporter: DivReporter {
  func reportError(cardId _: DivCardID, error: DivError) {
    XCTFail(error.message)
  }
}

private final class MockSubmitter: DivSubmitter {
  func submit(
    request _: SubmitRequest,
    data _: [String: String],
    completion _: @escaping DivSubmitterCompletion
  ) {}

  func cancelRequests() {}
}

final class MockPatchProvider: DivPatchProvider {
  func getPatch(url _: URL, completion _: @escaping DivPatchProviderCompletion) {}

  func cancelRequests() {}
}

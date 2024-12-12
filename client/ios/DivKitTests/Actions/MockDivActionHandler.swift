@testable import DivKit
import Foundation

extension DivActionHandler {
  convenience init(
    blockStateStorage: DivBlockStateStorage = DivBlockStateStorage(),
    idToPath: IdToPath = IdToPath(),
    logger: DivActionLogger = EmptyDivActionLogger(),
    reporter: DivReporter = DefaultDivReporter(),
    updateCard: @escaping DivActionURLHandler.UpdateCardAction = { _ in },
    urlHandler: DivUrlHandler = DivUrlHandlerDelegate { _, _ in },
    variablesStorage: DivVariablesStorage = DivVariablesStorage()
  ) {
    self.init(
      stateUpdater: DefaultDivStateManagement(),
      blockStateStorage: blockStateStorage,
      patchProvider: MockPatchProvider(),
      submitter: MockSubmitter(),
      variablesStorage: variablesStorage,
      functionsStorage: nil,
      updateCard: updateCard,
      showTooltip: nil,
      tooltipActionPerformer: nil,
      logger: logger,
      trackVisibility: { _, _ in },
      trackDisappear: { _, _ in },
      performTimerAction: { _, _, _ in },
      urlHandler: urlHandler,
      persistentValuesStorage: DivPersistentValuesStorage(),
      reporter: reporter,
      idToPath: idToPath,
      animatorController: DivAnimatorController()
    )
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

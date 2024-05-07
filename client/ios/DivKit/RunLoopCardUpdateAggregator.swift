import BasePublic
import BaseTinyPublic
import Foundation

final class RunLoopCardUpdateAggregator {
  private var enabled: Bool = true
  private var batch: [DivActionURLHandler.UpdateReason] = []
  private let updateCardAction: DivKitComponents.UpdateCardAction
  private let mainThreadAsyncRunner: MainThreadAsyncRunner

  init(
    updateCardAction: @escaping DivKitComponents.UpdateCardAction,
    mainThreadAsyncRunner: @escaping MainThreadAsyncRunner = onMainThreadAsync
  ) {
    self.updateCardAction = updateCardAction
    self.mainThreadAsyncRunner = mainThreadAsyncRunner
  }

  func performWithNoUpdates(_ action: Action) {
    Thread.assertIsMain()
    enabled = false
    action()
    enabled = true
  }

  func aggregate(_ reason: DivActionURLHandler.UpdateReason) {
    Thread.assertIsMain()
    guard enabled else { return }
    let notScheduled = batch.isEmpty
    batch.append(reason)
    if notScheduled {
      mainThreadAsyncRunner { [weak self] in
        self?.flushUpdateActions()
      }
    }
  }

  func flushUpdateActions() {
    guard let reasons = NonEmptyArray(self.batch.merge()) else {
      return
    }
    batch = []
    updateCardAction(reasons)
  }

  func forceUpdate() {
    batch.append(.external)
    flushUpdateActions()
  }
}

extension [DivActionURLHandler.UpdateReason] {
  fileprivate func merge() -> [DivActionURLHandler.UpdateReason] {
    var cards: [DivCardID: Set<DivVariableName>] = [:]

    let reasons: [DivActionURLHandler.UpdateReason] = compactMap {
      switch $0 {
      case let .variable(affectedCards):
        for (key, value) in affectedCards {
          cards[key] = cards[key]?.union(value) ?? value
        }
        return nil
      case .patch, .timer, .state, .external:
        return $0
      }
    }

    return reasons + (cards.isEmpty ? [] : [.variable(cards)])
  }
}

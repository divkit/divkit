import Foundation
import VGSL

final class RunLoopCardUpdateAggregator {
  private var enabled: Bool = true
  private var batch: [DivCardUpdateReason] = []
  private let updateCardAction: ([DivCardUpdateReason]) -> Void
  private let mainThreadAsyncRunner: MainThreadAsyncRunner

  init(
    updateCardAction: @escaping ([DivCardUpdateReason]) -> Void,
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

  func aggregate(_ reason: DivCardUpdateReason) {
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
    let reasons = batch.merge()
    batch = []
    updateCardAction(reasons)
  }

  func forceUpdate() {
    batch.append(.external)
    flushUpdateActions()
  }
}

extension [DivCardUpdateReason] {
  fileprivate func merge() -> [DivCardUpdateReason] {
    var cards: [DivCardID: Set<DivVariableName>] = [:]

    let reasons: [DivCardUpdateReason] = compactMap {
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

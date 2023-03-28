import Foundation
import BaseTinyPublic
import BasePublic

final class RunLoopCardUpdateAggregator {
  private var enabled: Bool = true
  private var scheduled: Bool = false
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
    batch.append(reason)
    if !scheduled {
      scheduled = true
      mainThreadAsyncRunner { [weak self] in
        guard let self = self else { return }
        if let reasons = NonEmptyArray(self.batch.merge()) {
          self.batch = []
          self.scheduled = false
          self.updateCardAction(reasons)
        }
      }
    }
  }
}

extension Array where Element == DivActionURLHandler.UpdateReason {
  fileprivate func merge() -> [DivActionURLHandler.UpdateReason] {
    var variables: DivActionURLHandler.UpdateReason.AffectedCards = .specific([])

    let reasons: [DivActionURLHandler.UpdateReason] = compactMap {
      switch $0 {
      case let .variable(affectedCards):
        switch (variables, affectedCards) {
        case let (.specific(lhs), .specific(rhs)):
          variables = .specific(lhs.union(rhs))
        case (_, .all):
          variables = .all
        case (.all, _):
          break
        }
        return nil
      case .patch, .timer, .state:
        return $0
      }
    }

    return reasons + (variables == .specific([]) ? [] :  [.variable(variables)])
  }
}

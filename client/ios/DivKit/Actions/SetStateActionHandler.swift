import Foundation
import LayoutKit
import VGSL

final class SetStateActionHandler {
  private let stateUpdater: DivStateUpdater

  init(stateUpdater: DivStateUpdater) {
    self.stateUpdater = stateUpdater
  }

  func handle(_ action: DivActionSetState, context: DivActionHandlingContext) {
    guard let stateId = action.resolveStateId(context.expressionResolver) else {
      return
    }

    handle(
      divStatePath: DivStatePath.makeDivStatePath(from: stateId),
      lifetime: .short,
      context: context
    )
  }

  func handle(
    divStatePath: DivStatePath,
    lifetime: DivStateLifetime,
    context: DivActionHandlingContext
  ) {
    let cardId = context.path.cardId
    let fullStatePath: DivStatePath = if let tooltipId = context.path.tooltipId,
                                         divStatePath.isLocal {
      DivStatePath.makeDivStatePath(
        from: "\(tooltipId)/0/\(divStatePath.description)"
      )
    } else {
      divStatePath
    }
    stateUpdater.set(
      path: fullStatePath,
      cardId: cardId,
      lifetime: lifetime
    )
    context.updateCard(.state(cardId))
  }
}

extension UIElementPath {
  fileprivate var tooltipId: String? {
    var root: UIElementPath? = parent
    var leaf: String?
    while let parent = root?.parent {
      leaf = root?.leaf
      root = parent
    }
    if let leaf, Int(leaf) == nil {
      // assume the card has an tooltip id if the element after the root is not numeric
      return leaf
    }
    return nil
  }
}

extension DivStatePath {
  fileprivate var isLocal: Bool {
    // State path for the main card starts with a numeric div-data state:
    // 0/div_state_in_main_card/state1
    //
    // State path for the state inside tooltip does not have a leading numeric state:
    // div_state_in_tooltip/state1
    Int(rawValue.root) == nil
  }
}

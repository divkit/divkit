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
    let cardId = context.cardId
    // A relative address from a tooltip is resolved in that tooltip's `tooltip#<id>` namespace.
    // A numeric-rooted address remains absolute and can address the host from inside a tooltip.
    //
    // For compatibility, a nonnumeric-rooted path originating in the host is treated as an
    // absolute tooltip address written using the old bare-id namespace. Its root is rewritten
    // to `tooltip#<id>`, preserving existing actions such as `my_tooltip/0/state/state2`.
    // An address already rooted at `tooltip#<id>` is left unchanged.
    let fullStatePath: DivStatePath = if let tooltipId = context.sourceViewId.additionalId,
                                         divStatePath.isLocal {
      divStatePath.resolvedRelativeToTooltip(id: tooltipId)
    } else if divStatePath.isLocal, !divStatePath.isInTooltipNamespace {
      divStatePath.replacingRootWithTooltipNamespace()
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

extension DivStatePath {
  /// Whether the address written by the layout author has a nonnumeric root. From a tooltip,
  /// such an address is relative to that tooltip. From the host, it is the legacy absolute
  /// spelling of a tooltip address and its root is rewritten into the tooltip namespace.
  /// This remains a property of the address itself, not a guess about the context:
  ///
  /// - `0/div_state_in_main_card/state1` - absolute, starts with a div-data state id;
  /// - `div_state_in_tooltip/state1` - relative, no leading div-data state.
  fileprivate var isLocal: Bool {
    Int(rawValue.root) == nil
  }

  fileprivate var isInTooltipNamespace: Bool {
    rawValue.root.hasPrefix(DivStatePath.tooltipRootPrefix)
  }

  fileprivate func resolvedRelativeToTooltip(id: String) -> DivStatePath {
    DivStatePath(
      rawValue: DivStatePath.tooltipRoot(id: id).rawValue
        + "0"
        + description.split(separator: "/").map(String.init)
    )
  }

  fileprivate func replacingRootWithTooltipNamespace() -> DivStatePath {
    let components = description.split(separator: "/").map(String.init)
    return DivStatePath(
      rawValue: DivStatePath.tooltipRoot(id: rawValue.root).rawValue
        + Array(components.dropFirst())
    )
  }
}

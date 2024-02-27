import CoreFoundation
import CoreGraphics
import Foundation

import CommonCorePublic
import LayoutKit

extension DivState: DivBlockModeling {
  public func makeBlock(context: DivBlockModelingContext) throws -> Block {
    try addSwipeHandling(
      to: applyBaseProperties(
        to: { try makeBaseBlock(context: context) },
        context: context,
        actionsHolder: nil
      ),
      context: context
    )
  }

  private func makeBaseBlock(context: DivBlockModelingContext) throws -> Block {
    guard let id = divId ?? id else {
      throw DivBlockModelingError("DivState has no id", path: context.parentPath)
    }

    let stateInterceptor = context.getStateInterceptor(for: self)
    let divStatePath = context.parentDivStatePath + id
    let defaultStateId = resolveDefaultStateId(context.expressionResolver)
    let stateManager = context.stateManager
    if let stateIdVariable {
      let stateBinding = context.makeBinding(
        variableName: stateIdVariable,
        defaultValue: defaultStateId ?? states[0].stateId
      )
      stateManager.setState(stateBlockPath: divStatePath, stateBinding: stateBinding)
    } else {
      stateManager.resetBinding(for: divStatePath)
    }
    let stateManagerItem = stateManager.get(stateBlockPath: divStatePath)

    let expressionResolver = context.expressionResolver
    let activeState = stateInterceptor?.getAppropriateState(divState: self, context: context)
      ?? states.first { $0.stateId == stateManagerItem?.currentStateID.rawValue }
      ?? states.first { $0.stateId == defaultStateId }
      ?? states[0]
    let activeStateID = DivStateID(rawValue: activeState.stateId)
    let activeStatePath = divStatePath + activeStateID

    var previousBlock: Block?
    var animationIn: [TransitioningAnimation]?
    var animationOut: [TransitioningAnimation]?
    let activeStateId = activeState.stateId
    if let stateManagerItem,
       let previousState = getPreviousState(stateManagerItem: stateManagerItem),
       previousState.stateId != activeStateId,
       let previousDiv = previousState.div {
      // state changed -> drop visibility cache for all children
      context.lastVisibleBoundsCache.dropVisibleBounds(forMatchingPrefix: context.parentPath)
      previousBlock = try previousDiv.value.makeBlock(
        context: context.makeContextForState(
          id: id,
          stateId: previousState.stateId
        )
      )
      animationOut = previousState.animationOut?
        .makeTransitioningAnimations(for: .disappearing, with: expressionResolver)
      animationIn = activeState.animationIn?
        .makeTransitioningAnimations(for: .appearing, with: expressionResolver)
    }

    if let div = activeState.div {
      stateManager.updateBlockIdsWithStateChangeTransition(
        statePath: activeStatePath,
        div: div
      )
    }

    let activeStateDiv = activeState.div?.value
    let activeBlock = try activeStateDiv?.makeBlock(
      context: context.makeContextForState(
        id: id,
        stateId: activeStateId
      )
    ) ?? stub

    // state is being applied, updating DivStateManager
    stateManager.setState(stateBlockPath: divStatePath, stateID: activeStateID)

    let defaultStateAlignment = BlockAlignment2D(
      horizontal: .leading,
      vertical: .leading
    )
    let stateAlignment = activeStateDiv?
      .resolveAlignment(context, defaultAlignment: defaultStateAlignment)
      ?? defaultStateAlignment

    return LayeredBlock(
      widthTrait: resolveContentWidthTrait(context),
      heightTrait: resolveContentHeightTrait(context),
      children: [
        LayeredBlock.Child(
          content: TransitioningBlock(
            from: previousBlock,
            to: activeBlock,
            animationOut: animationOut,
            animationIn: animationIn
          ),
          alignment: stateAlignment
        ),
      ]
    ).addingStateBlock(
      ids: stateManager.getVisibleIds(statePath: activeStatePath)
    )
  }

  private func addSwipeHandling(
    to child: Block,
    context: DivBlockModelingContext
  ) -> Block {
    let swipeOutActions = states.compactMap(\.swipeOutActions).flatMap { $0 }
    guard !swipeOutActions.isEmpty else { return child }

    return SwipeContainerBlock(
      child: child,
      state: .default,
      path: context.parentPath + DivState.type,
      swipeOutActions: swipeOutActions.uiActions(context: context)
    )
  }

  private func getPreviousState(
    stateManagerItem: DivStateManager.Item
  ) -> DivState.State? {
    switch stateManagerItem.previousState {
    case .empty:
      nil
    case .initial:
      states[0]
    case let .withID(stateId):
      states.first(where: { $0.stateId == stateId.rawValue })
    }
  }
}

private let stub: Block = EmptyBlock.zeroSized

extension DivBlockModelingContext {
  fileprivate func makeContextForState(
    id: String,
    stateId: String
  ) -> DivBlockModelingContext {
    modifying(
      parentPath: parentPath + id + stateId,
      parentDivStatePath: parentDivStatePath + id + stateId
    )
  }
}

extension TransitioningAnimation.Kind {
  fileprivate func defaultStartValue(for type: TransitioningAnimationType) -> Double {
    switch (self, type) {
    case (.fade, .appearing), (.scaleXY, .appearing):
      return 0
    case (.fade, .disappearing), (.scaleXY, .disappearing):
      return 1
    case (.translationY, .appearing):
      return TransitioningAnimation.defaultTrailingSlideDistance
    case (.translationY, .disappearing):
      return 0
    case (.translationX, _):
      assertionFailure()
      return 0
    }
  }

  fileprivate func defaultEndValue(for type: TransitioningAnimationType) -> Double {
    defaultStartValue(for: type.inverted())
  }
}

extension DivAnimation.Name {
  fileprivate var kind: TransitioningAnimation.Kind? {
    switch self {
    case .fade: .fade
    case .scale: .scaleXY
    case .translate: .translationY
    case .set, .noAnimation, .native: nil
    }
  }
}

extension DivAnimation {
  fileprivate func makeTransitioningAnimations(
    for type: TransitioningAnimationType,
    with expressionResolver: ExpressionResolver
  ) -> [TransitioningAnimation] {
    let name = resolveName(expressionResolver) ?? .noAnimation
    if name == .set {
      return (items ?? []).flatMap {
        $0.makeTransitioningAnimations(for: type, with: expressionResolver)
      }
    }

    guard let kind = name.kind else {
      return []
    }

    let animation = TransitioningAnimation(
      kind: kind,
      start: resolveStartValue(expressionResolver) ?? kind.defaultStartValue(for: type),
      end: resolveEndValue(expressionResolver) ?? kind.defaultEndValue(for: type),
      duration: Duration(milliseconds: resolveDuration(expressionResolver)),
      delay: Delay(milliseconds: resolveStartDelay(expressionResolver)),
      timingFunction: resolveInterpolator(expressionResolver).asTimingFunction()
    )

    return [animation]
  }
}

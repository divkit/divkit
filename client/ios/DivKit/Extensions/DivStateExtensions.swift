import CoreFoundation
import CoreGraphics
import Foundation

import CommonCorePublic
import LayoutKit

extension DivState: DivBlockModeling {
  public func makeBlock(context: DivBlockModelingContext) throws -> Block {
    addSwipeHandling(
      to: try applyBaseProperties(
        to: { try makeBaseBlock(context: context) },
        context: context,
        actions: nil,
        actionAnimation: nil,
        doubleTapActions: nil,
        longTapActions: nil
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
    let stateManager = context.stateManager
    let stateManagerItem = stateManager.get(stateBlockPath: divStatePath)

    let selectedState = stateInterceptor?.getAppropriateState(divState: self, context: context)
      ?? states.first { $0.stateId == stateManagerItem?.currentStateID.rawValue }
    let defaultState = states.first { $0.stateId == defaultStateId?.rawValue }
      ?? states[0]
    let activeState = selectedState ?? defaultState
    let activeStateID = DivStateID(rawValue: activeState.stateId)
    let activeStatePath = divStatePath + activeStateID

    var previousBlock: Block?
    var animationIn: [TransitioningAnimation]?
    var animationOut: [TransitioningAnimation]?
    let expressionResolver = context.expressionResolver
    let activeStateId = activeState.stateId
    if let stateManagerItem = stateManagerItem,
       let previousState = getPreviousState(stateManagerItem: stateManagerItem),
       previousState.stateId != activeStateId,
       let previousDiv = previousState.div {
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
      .alignment2D(withDefault: defaultStateAlignment, expressionResolver: expressionResolver) ??
      defaultStateAlignment

    return LayeredBlock(
      widthTrait: makeContentWidthTrait(with: context),
      heightTrait: makeContentHeightTrait(with: context),
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
      swipeOutActions: swipeOutActions.map {
        $0.uiAction(context: context.actionContext)
      }
    )
  }

  private func getPreviousState(
    stateManagerItem: DivStateManager.Item
  ) -> DivState.State? {
    switch stateManagerItem.previousState {
    case .empty:
      return nil
    case .initial:
      return states[0]
    case let .withID(stateId):
      return states.first(where: { $0.stateId == stateId.rawValue })
    }
  }
}

private let stub: Block = EmptyBlock.zeroSized

extension DivBlockModelingContext {
  fileprivate func makeContextForState(
    id: String,
    stateId: String
  ) -> DivBlockModelingContext {
    modified(self) {
      $0.parentPath = parentPath + id + stateId
      $0.parentDivStatePath = parentDivStatePath + id + stateId
    }
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
    case .fade: return .fade
    case .scale: return .scaleXY
    case .translate: return .translationY
    case .set, .noAnimation, .native: return nil
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

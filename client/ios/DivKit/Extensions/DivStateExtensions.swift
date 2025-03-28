import CoreFoundation
import CoreGraphics
import Foundation
import LayoutKit
import VGSL

extension DivState: DivBlockModeling {
  public func makeBlock(context parentContext: DivBlockModelingContext) throws -> Block {
    let stateId = parentContext.overridenId ?? divId ?? id ?? ""
    let context = modifiedContextParentPath(parentContext)
    return try addSwipeHandling(
      to: applyBaseProperties(
        to: { try makeBaseBlock(context: context, id: stateId) },
        context: context,
        actionsHolder: nil,
        clipToBounds: resolveClipToBounds(context.expressionResolver)
      ),
      context: context
    )
  }

  private func makeBaseBlock(context: DivBlockModelingContext, id: String) throws -> Block {
    if id == "" {
      context.addWarning(message: "DivState has no id")
    }

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
    let activeState = states.first { $0.stateId == stateManagerItem?.currentStateID.rawValue }
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
      context.triggersStorage?
        .disableTriggers(path: context.path + previousState.stateId)
      context.triggersStorage?.enableTriggers(path: context.path + activeStateId)

      // state changed -> drop visibility cache for all children
      if let parentPrefix = context.path.parent {
        context.lastVisibleBoundsCache.dropVisibleBounds(prefix: parentPrefix)
      }
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
    let child = if animationOut == nil, animationIn == nil {
      LayeredBlock.Child(
        content: activeBlock,
        alignment: stateAlignment
      )
    } else {
      LayeredBlock.Child(
        content: TransitioningBlock(
          from: previousBlock,
          to: activeBlock,
          animationOut: animationOut,
          animationIn: animationIn
        ),
        alignment: stateAlignment
      )
    }

    return LayeredBlock(
      widthTrait: resolveContentWidthTrait(context),
      heightTrait: resolveContentHeightTrait(context),
      children: [child]
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
      path: context.path,
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
      pathSuffix: stateId,
      parentDivStatePath: parentDivStatePath + id + stateId
    )
  }
}

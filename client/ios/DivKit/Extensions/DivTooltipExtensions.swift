import Foundation
import LayoutKit
import VGSL

extension DivTooltip {
  fileprivate func makeTooltip(
    context: DivBlockModelingContext
  ) throws -> BlockTooltip? {
    let expressionResolver = context.expressionResolver
    guard let position = resolvePosition(expressionResolver)?.cast() else {
      return nil
    }

    let tooltipViewFactory: TooltipViewFactory = { [weak self] in
      guard let self, let tooltipViewFactory = context.tooltipViewFactory else {
        return nil
      }
      return await tooltipViewFactory.makeView(div: self.div, tooltipId: self.id)
    }

    let mode: BlockTooltip.Mode = switch mode {
    case .divTooltipModeModal:
      .modal
    case .divTooltipModeNonModal:
      .nonModal
    }

    return try BlockTooltip(
      // Legacy behavior. Views should be created with tooltipViewFactory.
      block: div.value.makeBlock(context: context),
      params: BlockTooltipParams(
        id: id,
        mode: mode,
        duration: TimeInterval(milliseconds: resolveDuration(expressionResolver)),
        closeByTapOutside: resolveCloseByTapOutside(expressionResolver),
        tapOutsideActions: tapOutsideActions?.uiActions(context: context) ?? [],
        backgroundAccessibilityDescription: resolveBackgroundAccessibilityDescription(
          expressionResolver
        ),
        animationIn: animationIn?.makeTransitioningAnimations(
          for: .appearing,
          with: expressionResolver
        ),
        animationOut: animationOut?.makeTransitioningAnimations(
          for: .disappearing,
          with: expressionResolver
        )
      ),
      offset: offset?.resolve(expressionResolver) ?? .zero,
      position: position,
      useLegacyWidth: context.flagsInfo.useTooltipLegacyWidth,
      tooltipViewFactory: tooltipViewFactory
    )
  }
}

extension DivTooltip.Position {
  fileprivate func cast() -> BlockTooltip.Position {
    switch self {
    case .left: .left
    case .topLeft: .topLeft
    case .top: .top
    case .topRight: .topRight
    case .right: .right
    case .bottomRight: .bottomRight
    case .bottom: .bottom
    case .bottomLeft: .bottomLeft
    case .center: .center
    }
  }
}

extension [DivTooltip]? {
  func makeTooltips(
    context: DivBlockModelingContext
  ) throws -> [BlockTooltip] {
    let items = self ?? []
    if !items.isEmpty, context.viewId.isTooltip {
      context.errorsStorage.add(
        DivBlockModelingError(
          "Tooltip can not host another tooltips",
          path: context.path
        )
      )
      return []
    }

    return try items.compactMap {
      let tooltipContext = context.cloneForTooltip(tooltipId: $0.id)
      return try $0.makeTooltip(context: tooltipContext)
    }
  }
}

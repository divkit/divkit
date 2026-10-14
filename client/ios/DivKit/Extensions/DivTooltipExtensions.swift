import Foundation
import LayoutKit
import VGSL

extension DivTooltip {
  fileprivate func makeTooltip(
    context: DivBlockModelingContext,
    anchorPath: UIElementPath
  ) throws -> BlockTooltip? {
    let expressionResolver = context.expressionResolver
    guard let position = resolvePosition(expressionResolver)?.cast() else {
      return nil
    }

    let tooltipRef = DivViewId.Tooltip(id: id, anchorPath: anchorPath)

    let substrateViewFactory: TooltipViewFactory? = {
      guard let substrateDiv,
            let factory = context.tooltipViewFactory else {
        return nil
      }

      return {
        await factory.makeSubstrateView(div: substrateDiv, tooltip: tooltipRef)
      }
    }()

    let mode: BlockTooltip.Mode = switch mode {
    case .divTooltipModeModal:
      .modal
    case .divTooltipModeNonModal:
      .nonModal
    }

    let viewSource: BlockTooltip.ViewSource = if let factory = context.tooltipViewFactory {
      .factory(
        key: FactoryKey(
          div: div.value as AnyObject,
          substrateDiv: substrateDiv.map { $0.value as AnyObject },
          tooltip: tooltipRef
        )
      ) { [div] in
        await factory.makeView(div: div, tooltip: tooltipRef)
      }
    } else {
      try .block(div.value.makeBlock(context: context))
    }

    return BlockTooltip(
      viewSource: viewSource,
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
      bringToTopId: bringToTopId,
      substrateViewFactory: substrateViewFactory
    )
  }
}

private struct FactoryKey: Equatable {
  let div: AnyObject
  let substrateDiv: AnyObject?
  let tooltip: DivViewId.Tooltip

  static func ==(lhs: FactoryKey, rhs: FactoryKey) -> Bool {
    lhs.div === rhs.div && lhs.substrateDiv === rhs.substrateDiv && lhs.tooltip == rhs.tooltip
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

    let anchorPath = context.path
    return try items.compactMap {
      let tooltipContext = context.cloneForTooltip(tooltipId: $0.id)
      return try $0.makeTooltip(context: tooltipContext, anchorPath: anchorPath)
    }
  }
}

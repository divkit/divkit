import BasePublic
import CommonCorePublic
import Foundation
import LayoutKit

#if os(iOS)
public typealias DivTooltipViewFactory = (Div) -> VisibleBoundsTrackingView
#else
public typealias DivTooltipViewFactory = (Div) -> ViewType
#endif

extension DivTooltip {
  fileprivate func makeTooltip(
    context: DivBlockModelingContext
  ) throws -> BlockTooltip? {
    let expressionResolver = context.expressionResolver
    guard let position = resolvePosition(expressionResolver)?.cast() else {
      return nil
    }

    let block = try div.value.makeBlock(context: context)

    let tooltipViewFactory: TooltipViewFactory =
      Variable { [tooltipViewFactory = context.tooltipViewFactory, div = self.div] in
        guard let tooltipViewFactory else { return nil }
        return tooltipViewFactory(div)
      }

    return BlockTooltip(
      id: id,
      block: block,
      duration: Duration(milliseconds: resolveDuration(expressionResolver)),
      offset: offset?.resolve(expressionResolver) ?? .zero,
      position: position,
      tooltipViewFactory: tooltipViewFactory
    )
  }
}

extension DivTooltip.Position {
  fileprivate func cast() -> BlockTooltip.Position {
    switch self {
    case .left: return .left
    case .topLeft: return .topLeft
    case .top: return .top
    case .topRight: return .topRight
    case .right: return .right
    case .bottomRight: return .bottomRight
    case .bottom: return .bottom
    case .bottomLeft: return .bottomLeft
    case .center: return .center
    }
  }
}

extension [DivTooltip]? {
  func makeTooltips(
    context: DivBlockModelingContext
  ) throws -> [BlockTooltip] {
    try self?.iterativeFlatMap { div, index in
      let tooltipContext = context.modifying(
        parentPath: context.parentPath + "tooltip" + index
      )
      return try div.makeTooltip(context: tooltipContext)
    } ?? []
  }
}

public func makeTooltipViewFactory(
  divKitComponents: DivKitComponents,
  cardId: DivCardID
) -> DivTooltipViewFactory? {
  #if os(iOS)
  return { (div: Div) in
    let view = DivView(divKitComponents: divKitComponents)
    let divData = DivData(
      logId: cardId.rawValue,
      states: [.init(div: div, stateId: 0)],
      timers: nil,
      transitionAnimationSelector: nil,
      variableTriggers: nil,
      variables: nil
    )
    view.setSource(.init(kind: .divData(divData), cardId: cardId))
    return view
  }
  #else
  return nil
  #endif
}

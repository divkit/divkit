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

    let tooltipViewFactory: TooltipViewFactory = Variable { [weak self] in
      guard let self, let tooltipViewFactory = context.tooltipViewFactory else {
        return nil
      }
      return tooltipViewFactory.makeView(div: self.div, tooltipId: self.id)
    }

    return try BlockTooltip(
      id: id,
      block: div.value.makeBlock(context: context),
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
    try self?.iterativeFlatMap { div, index in
      let tooltipContext = context.modifying(
        parentPath: context.parentPath + "tooltip" + index
      )
      return try div.makeTooltip(context: tooltipContext)
    } ?? []
  }
}

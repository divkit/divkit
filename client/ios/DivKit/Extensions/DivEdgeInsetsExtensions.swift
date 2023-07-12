import CoreGraphics

import BaseUIPublic
import CommonCorePublic
import LayoutKit

extension DivEdgeInsets {
  func makeEdgeInsets(context: DivBlockModelingContext) -> EdgeInsets {
    let unit = resolveUnit(context.expressionResolver)

    let top = unit.makeScaledValue(resolveTop(context.expressionResolver))
    let bottom = unit.makeScaledValue(resolveBottom(context.expressionResolver))

    var left = unit.makeScaledValue(resolveLeft(context.expressionResolver))
    var right = unit.makeScaledValue(resolveRight(context.expressionResolver))

    let start = resolveStart(context.expressionResolver).flatMap(unit.makeScaledValue)
    let end = resolveEnd(context.expressionResolver).flatMap(unit.makeScaledValue)

    if start != nil || end != nil {
      switch (context.layoutDirection.uiLayoutDirection) {
      case .rightToLeft:
        right = start ?? 0
        left = end ?? 0
      case .leftToRight:
        left = start ?? 0
        right = end ?? 0
      @unknown default:
        assertionFailure("uiLayoutDirection (UIUserInterfaceLayoutDirection) value is incorrect")
      }
    }

    return EdgeInsets(top: top, left: left, bottom: bottom, right: right)
  }

  func makeHorizontalInsets(with expressionResolver: ExpressionResolver) -> SideInsets {
    let unit = resolveUnit(expressionResolver)
    return SideInsets(
      leading: unit.makeScaledValue(resolveLeft(expressionResolver)),
      trailing: unit.makeScaledValue(resolveRight(expressionResolver))
    )
  }

  func makeVerticalInsets(with expressionResolver: ExpressionResolver) -> SideInsets {
    let unit = resolveUnit(expressionResolver)
    return SideInsets(
      leading: unit.makeScaledValue(resolveTop(expressionResolver)),
      trailing: unit.makeScaledValue(resolveBottom(expressionResolver))
    )
  }
}

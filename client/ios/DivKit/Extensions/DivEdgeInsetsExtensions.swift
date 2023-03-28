import CoreGraphics

import BaseUIPublic
import CommonCorePublic
import LayoutKit

extension DivEdgeInsets {
  func makeEdgeInsets(with expressionResolver: ExpressionResolver) -> EdgeInsets {
    let unit = resolveUnit(expressionResolver)
    return EdgeInsets(
      top: unit.makeScaledValue(resolveTop(expressionResolver)),
      left: unit.makeScaledValue(resolveLeft(expressionResolver)),
      bottom: unit.makeScaledValue(resolveBottom(expressionResolver)),
      right: unit.makeScaledValue(resolveRight(expressionResolver))
    )
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

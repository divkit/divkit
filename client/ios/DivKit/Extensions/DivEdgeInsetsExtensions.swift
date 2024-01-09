import CoreGraphics

import BaseUIPublic
import CommonCorePublic
import LayoutKit

extension DivEdgeInsets? {
  func resolve(_ context: DivBlockModelingContext) -> EdgeInsets {
    self?.resolve(context) ?? EdgeInsets.zero
  }

  func resolveHorizontalInsets(_ expressionResolver: ExpressionResolver) -> SideInsets {
    self?.resolveHorizontalInsets(expressionResolver) ?? SideInsets.zero
  }

  func resolveVerticalInsets(_ expressionResolver: ExpressionResolver) -> SideInsets {
    self?.resolveVerticalInsets(expressionResolver) ?? SideInsets.zero
  }
}

extension DivEdgeInsets {
  func resolve(_ context: DivBlockModelingContext) -> EdgeInsets {
    let expressionResolver = context.expressionResolver
    let unit = resolveUnit(expressionResolver)
    let top = unit.makeScaledValue(resolveTop(expressionResolver))
    let bottom = unit.makeScaledValue(resolveBottom(expressionResolver))
    var left = unit.makeScaledValue(resolveLeft(expressionResolver))
    var right = unit.makeScaledValue(resolveRight(expressionResolver))
    let start = resolveStart(expressionResolver).flatMap(unit.makeScaledValue)
    let end = resolveEnd(expressionResolver).flatMap(unit.makeScaledValue)

    if start != nil || end != nil {
      switch context.layoutDirection {
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

  func resolveHorizontalInsets(_ expressionResolver: ExpressionResolver) -> SideInsets {
    let unit = resolveUnit(expressionResolver)
    return SideInsets(
      leading: unit.makeScaledValue(resolveLeft(expressionResolver)),
      trailing: unit.makeScaledValue(resolveRight(expressionResolver))
    )
  }

  func resolveVerticalInsets(_ expressionResolver: ExpressionResolver) -> SideInsets {
    let unit = resolveUnit(expressionResolver)
    return SideInsets(
      leading: unit.makeScaledValue(resolveTop(expressionResolver)),
      trailing: unit.makeScaledValue(resolveBottom(expressionResolver))
    )
  }
}

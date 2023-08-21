import CoreGraphics

import CommonCorePublic
import LayoutKit

extension DivShapeDrawable {
  func makeBlock(
    context: DivBlockModelingContext,
    widthTrait: DivDrawableWidthTrait,
    corners: CGRect.Corners
  ) throws -> Block {
    let expressionResolver = context.expressionResolver
    switch shape {
    case let .divRoundedRectangleShape(roundedRectangle):
      let separatorBlock: Block
      switch widthTrait {
      case .fixed:
        let width = CGFloat(
          roundedRectangle.itemWidth
            .resolveValue(expressionResolver) ?? 0
        )
        separatorBlock = SeparatorBlock(size: width)
      case .resizable:
        separatorBlock = SeparatorBlock()
      }
      let height = CGFloat(
        roundedRectangle
          .itemHeight
          .resolveValue(expressionResolver) ?? 0
      )
      let cornerRadius = CGFloat(
        roundedRectangle
          .cornerRadius
          .resolveValue(expressionResolver) ?? 0
      )
      let blockBorder = stroke.flatMap { BlockBorder(
        color: $0.resolveColor(expressionResolver) ?? .black,
        width: CGFloat($0.resolveWidth(expressionResolver)) / 2
      ) }
      return separatorBlock
        .addingVerticalGaps(height / 2 - 0.5)
        .addingDecorations(
          boundary: .clipCorner(radius: cornerRadius, corners: corners),
          border: blockBorder,
          backgroundColor: resolveColor(expressionResolver)
        )
    case .divCircleShape:
      context.addError(level: .error, message: "unsupported block")
      return EmptyBlock()
    }
  }

  func getWidth(context: DivBlockModelingContext) -> CGFloat {
    switch shape {
    case let .divRoundedRectangleShape(rectangle):
      return CGFloat(rectangle.itemWidth.resolveValue(context.expressionResolver) ?? 0)
    case .divCircleShape:
      context.addError(level: .error, message: "unsupported circle shape")
      return 0
    }
  }

  func getHeight(context: DivBlockModelingContext) -> CGFloat {
    switch shape {
    case let .divRoundedRectangleShape(rectangle):
      let expressionResolver = context.expressionResolver
      let stroke = stroke?.resolveWidth(expressionResolver) ?? 0
      return CGFloat(
        (rectangle.itemHeight.resolveValue(expressionResolver) ?? 0) + stroke
      )
    case .divCircleShape:
      context.addError(level: .error, message: "unsupported circle shape")
      return 0
    }
  }
}

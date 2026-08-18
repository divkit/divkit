import CoreGraphics
import LayoutKit
import VGSL

extension DivShapeDrawable {
  func makeBlock(
    context: DivBlockModelingContext,
    widthTrait: DivDrawableWidthTrait,
    corners: CGRect.Corners
  ) -> Block {
    let expressionResolver = context.expressionResolver
    let separatorBlock: Block
    let height: CGFloat
    let cornerRadius: CGFloat
    let backgroundColor: Color?
    let shapeStroke: DivStroke?

    switch shape {
    case let .divRoundedRectangleShape(roundedRectangle):
      switch widthTrait {
      case .fixed:
        let width = CGFloat(
          roundedRectangle.itemWidth.resolveValue(expressionResolver) ?? 0
        )
        separatorBlock = SeparatorBlock(size: width)
      case .resizable:
        separatorBlock = SeparatorBlock()
      }
      height = CGFloat(
        roundedRectangle.itemHeight.resolveValue(expressionResolver) ?? 0
      )
      cornerRadius = CGFloat(
        roundedRectangle.cornerRadius.resolveValue(expressionResolver) ?? 0
      )
      backgroundColor = roundedRectangle.resolveBackgroundColor(expressionResolver)
      shapeStroke = roundedRectangle.stroke

    case let .divCircleShape(circle):
      cornerRadius = CGFloat(
        circle.radius.resolveValue(expressionResolver) ?? 0
      )
      let sideSize = cornerRadius * 2
      separatorBlock = SeparatorBlock(size: sideSize)
      height = sideSize
      backgroundColor = circle.resolveBackgroundColor(expressionResolver)
      shapeStroke = circle.stroke
    }

    let border: BlockBorder? = if let stroke = shapeStroke ?? stroke {
      BlockBorder(
        color: stroke.resolveColor(expressionResolver) ?? .black,
        width: stroke.resolveWidth(expressionResolver)
      )
    } else {
      nil
    }

    return separatorBlock
      .addingVerticalGaps(height / 2 - 0.5)
      .addingDecorations(
        boundary: .clipCorner(radius: cornerRadius, corners: corners),
        border: border,
        backgroundColor: backgroundColor ?? resolveColor(expressionResolver)
      )
  }

  func resolveWidth(_ context: DivBlockModelingContext) -> CGFloat {
    switch shape {
    case let .divRoundedRectangleShape(rectangle):
      CGFloat(rectangle.itemWidth.resolveValue(context.expressionResolver) ?? 0)
    case let .divCircleShape(circle):
      CGFloat(circle.radius.resolveValue(context.expressionResolver) ?? 0) * 2
    }
  }

  func resolveHeight(_ context: DivBlockModelingContext) -> CGFloat {
    switch shape {
    case let .divRoundedRectangleShape(rectangle):
      let expressionResolver = context.expressionResolver
      let strokeWidth = stroke?.resolveWidth(expressionResolver) ?? 0
      return CGFloat(
        Double(rectangle.itemHeight.resolveValue(expressionResolver) ?? 0) + strokeWidth
      )
    case .divCircleShape:
      return resolveWidth(context)
    }
  }
}

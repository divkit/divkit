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

    switch shape {
    case let .divRoundedRectangleShape(roundedRectangle):
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
      height = CGFloat(
        roundedRectangle
          .itemHeight
          .resolveValue(expressionResolver) ?? 0
      )
      cornerRadius = CGFloat(
        roundedRectangle
          .cornerRadius
          .resolveValue(expressionResolver) ?? 0
      )

    case let .divCircleShape(circle):
      cornerRadius = CGFloat(
        circle.radius.resolveValue(expressionResolver) ?? 0
      )
      let sideSize = cornerRadius * 2
      separatorBlock = SeparatorBlock(size: sideSize)
      height = sideSize
    }

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
  }

  func resolveWidth(_ context: DivBlockModelingContext) -> CGFloat {
    switch shape {
    case let .divRoundedRectangleShape(rectangle):
      return CGFloat(rectangle.itemWidth.resolveValue(context.expressionResolver) ?? 0)
    case let .divCircleShape(circle):
      return CGFloat(circle.radius.resolveValue(context.expressionResolver) ?? 0) * 2
    }
  }

  func resolveHeight(_ context: DivBlockModelingContext) -> CGFloat {
    switch shape {
    case let .divRoundedRectangleShape(rectangle):
      let expressionResolver = context.expressionResolver
      let stroke = stroke?.resolveWidth(expressionResolver) ?? 0
      return CGFloat(
        Double(rectangle.itemHeight.resolveValue(expressionResolver) ?? 0) + stroke
      )
    case .divCircleShape:
      return resolveWidth(context)
    }
  }
}

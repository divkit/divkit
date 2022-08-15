import CoreGraphics

import CommonCore
import LayoutKit

extension DivDrawable {
  public func makeBlock(
    context: DivBlockModelingContext,
    widthTrait: DivDrawableWidthTrait = .fixed,
    corners: CGRect.Corners
  ) throws -> Block {
    switch self {
    case let .divShapeDrawable(shapeDrawable):
      return try shapeDrawable.makeBlock(
        context: context,
        widthTrait: widthTrait,
        corners: corners
      )
    }
  }

  public func getWidth(context: DivBlockModelingContext) -> CGFloat {
    switch self {
    case let .divShapeDrawable(shapeDrawable):
      return shapeDrawable.getWidth(context: context)
    }
  }

  public func getHeight(context: DivBlockModelingContext) -> CGFloat {
    switch self {
    case let .divShapeDrawable(shapeDrawable):
      return shapeDrawable.getHeight(context: context)
    }
  }
}

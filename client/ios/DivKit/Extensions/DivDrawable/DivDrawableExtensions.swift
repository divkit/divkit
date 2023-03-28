import CoreGraphics

import CommonCorePublic
import LayoutKit

extension DivDrawable {
  func makeBlock(
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

  func getWidth(context: DivBlockModelingContext) -> CGFloat {
    switch self {
    case let .divShapeDrawable(shapeDrawable):
      return shapeDrawable.getWidth(context: context)
    }
  }

  func getHeight(context: DivBlockModelingContext) -> CGFloat {
    switch self {
    case let .divShapeDrawable(shapeDrawable):
      return shapeDrawable.getHeight(context: context)
    }
  }
}

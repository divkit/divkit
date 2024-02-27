import CoreGraphics

import CommonCorePublic
import LayoutKit

extension DivDrawable {
  func makeBlock(
    context: DivBlockModelingContext,
    widthTrait: DivDrawableWidthTrait = .fixed,
    corners: CGRect.Corners
  ) -> Block {
    switch self {
    case let .divShapeDrawable(shapeDrawable):
      shapeDrawable.makeBlock(
        context: context,
        widthTrait: widthTrait,
        corners: corners
      )
    }
  }

  func resolveWidth(_ context: DivBlockModelingContext) -> CGFloat {
    switch self {
    case let .divShapeDrawable(shapeDrawable):
      shapeDrawable.resolveWidth(context)
    }
  }

  func resolveHeight(_ context: DivBlockModelingContext) -> CGFloat {
    switch self {
    case let .divShapeDrawable(shapeDrawable):
      shapeDrawable.resolveHeight(context)
    }
  }
}

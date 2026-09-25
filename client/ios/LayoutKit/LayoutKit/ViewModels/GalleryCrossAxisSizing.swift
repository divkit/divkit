import CoreGraphics
import Foundation
import VGSL

enum GalleryCrossAxisSizing {
  static func boundedCrossAxisSize(
    crossAxis: CGFloat?,
    crossInsets: SideInsets
  ) -> CGFloat? {
    crossAxis.map { max(0, $0 - crossInsets.sum) }
  }

  static func constrainedCrossAxisSize(
    intrinsicSize: CGFloat,
    minSize: CGFloat,
    boundedCrossAxis: CGFloat?
  ) -> CGFloat {
    if let boundedCrossAxis {
      let cappedByViewportSize = min(intrinsicSize, boundedCrossAxis)
      return max(cappedByViewportSize, minSize)
    } else {
      return max(intrinsicSize, minSize)
    }
  }

  static func layoutWidth(
    for block: Block,
    maxCrossAxisSize: CGFloat,
    boundedCrossAxis: CGFloat?
  ) -> CGFloat {
    if block.isHorizontallyResizable {
      clamp(maxCrossAxisSize, min: block.minWidth, max: block.maxWidth)
    } else if block.isHorizontallyConstrained {
      constrainedCrossAxisSize(
        intrinsicSize: block.widthOfHorizontallyNonResizableBlock,
        minSize: block.minWidth,
        boundedCrossAxis: boundedCrossAxis
      )
    } else {
      block.widthOfHorizontallyNonResizableBlock
    }
  }

  static func layoutHeight(
    for block: Block,
    maxCrossAxisSize: CGFloat,
    boundedCrossAxis: CGFloat?,
    width: CGFloat
  ) -> CGFloat {
    if block.isVerticallyResizable {
      clamp(maxCrossAxisSize, min: block.minHeight, max: block.maxHeight)
    } else if block.isVerticallyConstrained {
      constrainedCrossAxisSize(
        intrinsicSize: block.heightOfVerticallyNonResizableBlock(forWidth: width),
        minSize: block.minHeight,
        boundedCrossAxis: boundedCrossAxis
      )
    } else {
      block.heightOfVerticallyNonResizableBlock(forWidth: width)
    }
  }
}

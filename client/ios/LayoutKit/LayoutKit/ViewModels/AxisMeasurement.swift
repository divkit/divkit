import CoreGraphics
import Foundation
import VGSL

/// Sizes of scroll items resolved for a scroll axis.
struct AxialMeasurements {
  /// Sizes of the items, in the order of the items.
  var sizes: [AxialSize]
  /// Size of the cross-axis slot the items are aligned in.
  var slotAcross: CGFloat
}

/// Axis-specific sizing of scroll items.
///
/// The order in which the axes are resolved is a part of the implementation, not of the layout
/// that uses it.
protocol AxisMeasurement {
  func isResizableAlongScroll(_ block: Block) -> Bool

  /// Size along the scroll axis: a resizable block fills `limit` within its own constraints,
  /// a non-resizable block takes its own size for the given size across the scroll axis.
  func sizeAlongScroll(of block: Block, limit: CGFloat, acrossSize: CGFloat) -> CGFloat

  /// Intrinsic content size along the scroll axis for the given size across the scroll axis.
  func intrinsicSizeAlongScroll(of block: Block, acrossSize: CGFloat) -> CGFloat

  /// Resolves the sizes of all items.
  ///
  /// - Parameters:
  ///   - boundedAcross: Size of the cross-axis slot, `nil` if the cross axis is unbounded.
  ///   The slot then fits the largest non-resizable item.
  ///   - sizeAlong: Size of an item along the scroll axis for its size across the scroll axis.
  func measure(
    _ items: [GalleryViewModel.Item],
    boundedAcross: CGFloat?,
    sizeAlong: (Block, _ acrossSize: CGFloat) -> CGFloat
  ) -> AxialMeasurements
}

struct HorizontalScrollMeasurement: AxisMeasurement {
  func isResizableAlongScroll(_ block: Block) -> Bool {
    block.isHorizontallyResizable
  }

  func sizeAlongScroll(of block: Block, limit: CGFloat, acrossSize _: CGFloat) -> CGFloat {
    block.isHorizontallyResizable
      ? clamp(max(limit, 0), min: block.minWidth, max: block.maxWidth)
      : block.widthOfHorizontallyNonResizableBlock
  }

  func intrinsicSizeAlongScroll(of block: Block, acrossSize _: CGFloat) -> CGFloat {
    block.intrinsicContentWidth
  }

  func measure(
    _ items: [GalleryViewModel.Item],
    boundedAcross: CGFloat?,
    sizeAlong: (Block, _ acrossSize: CGFloat) -> CGFloat
  ) -> AxialMeasurements {
    let blocks = items.map(\.content)
    // The width does not depend on the height, so the height is not known yet.
    let widths = blocks.map { sizeAlong($0, 0) }
    let slotHeight = boundedAcross
      ?? blocks.maxHeightOfVerticallyNonResizableBlocks(for: widths)
      ?? 0
    let sizes = zip(items, widths).map { item, width in
      AxialSize(
        along: width,
        across: height(
          of: item,
          width: width,
          slotHeight: slotHeight,
          boundedHeight: boundedAcross
        )
      )
    }
    return AxialMeasurements(sizes: sizes, slotAcross: slotHeight)
  }

  private func height(
    of item: GalleryViewModel.Item,
    width: CGFloat,
    slotHeight: CGFloat,
    boundedHeight: CGFloat?
  ) -> CGFloat {
    let block = item.content
    let height = height(
      of: block,
      width: width,
      slotHeight: slotHeight,
      boundedHeight: boundedHeight
    )
    return item.fillsCrossAxis
      ? clamp(max(height, slotHeight), min: block.minHeight, max: block.maxHeight)
      : height
  }

  private func height(
    of block: Block,
    width: CGFloat,
    slotHeight: CGFloat,
    boundedHeight: CGFloat?
  ) -> CGFloat {
    if block.isVerticallyResizable {
      clamp(slotHeight, min: block.minHeight, max: block.maxHeight)
    } else if block.isVerticallyConstrained {
      CrossAxisSizing.constrainedCrossAxisSize(
        intrinsicSize: block.heightOfVerticallyNonResizableBlock(forWidth: width),
        minSize: block.minHeight,
        boundedCrossAxis: boundedHeight
      )
    } else {
      block.heightOfVerticallyNonResizableBlock(forWidth: width)
    }
  }
}

struct VerticalScrollMeasurement: AxisMeasurement {
  func isResizableAlongScroll(_ block: Block) -> Bool {
    block.isVerticallyResizable
  }

  func sizeAlongScroll(of block: Block, limit: CGFloat, acrossSize: CGFloat) -> CGFloat {
    block.isVerticallyResizable
      ? clamp(max(limit, 0), min: block.minHeight, max: block.maxHeight)
      : block.heightOfVerticallyNonResizableBlock(forWidth: acrossSize)
  }

  func intrinsicSizeAlongScroll(of block: Block, acrossSize: CGFloat) -> CGFloat {
    block.intrinsicContentHeight(forWidth: acrossSize)
  }

  func measure(
    _ items: [GalleryViewModel.Item],
    boundedAcross: CGFloat?,
    sizeAlong: (Block, _ acrossSize: CGFloat) -> CGFloat
  ) -> AxialMeasurements {
    // The height may depend on the width, so the width is resolved first.
    let slotWidth = boundedAcross
      ?? items.map(\.content).maxWidthOfHorizontallyNonResizableBlocks
      ?? 0
    let sizes = items.map { item in
      let width = width(of: item, slotWidth: slotWidth, boundedWidth: boundedAcross)
      return AxialSize(along: sizeAlong(item.content, width), across: width)
    }
    return AxialMeasurements(sizes: sizes, slotAcross: slotWidth)
  }

  private func width(
    of item: GalleryViewModel.Item,
    slotWidth: CGFloat,
    boundedWidth: CGFloat?
  ) -> CGFloat {
    let block = item.content
    let width = width(of: block, slotWidth: slotWidth, boundedWidth: boundedWidth)
    // The height is measured for the stretched width.
    return item.fillsCrossAxis
      ? clamp(max(width, slotWidth), min: block.minWidth, max: block.maxWidth)
      : width
  }

  private func width(of block: Block, slotWidth: CGFloat, boundedWidth: CGFloat?) -> CGFloat {
    if block.isHorizontallyResizable {
      clamp(slotWidth, min: block.minWidth, max: block.maxWidth)
    } else if block.isHorizontallyConstrained {
      CrossAxisSizing.constrainedCrossAxisSize(
        intrinsicSize: block.widthOfHorizontallyNonResizableBlock,
        minSize: block.minWidth,
        boundedCrossAxis: boundedWidth
      )
    } else {
      block.widthOfHorizontallyNonResizableBlock
    }
  }
}

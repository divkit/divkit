import CoreGraphics
import Foundation
import VGSL

public struct PagerViewLayout: GalleryViewLayouting, Equatable {
  public struct Page {
    public let index: Int
    public let origin: CGFloat
    public let size: CGFloat

    public func contains(_ offset: CGFloat) -> Bool {
      let range = origin..<origin + size
      return range.contains(offset)
    }
  }

  public let model: GalleryViewModel
  public let layoutMode: PagerBlock.LayoutMode
  public let boundsSize: CGSize
  public let blockFrames: [CGRect]
  public let blockPages: [Page]
  public let contentSize: CGSize

  public var pageOrigins: [CGFloat] {
    blockPages.map(\.origin)
  }

  public var transformation: ElementsTransformation? {
    model.transformation
  }

  public var scrollDirection: ScrollDirection {
    model.direction
  }

  public init(
    model: GalleryViewModel,
    layoutMode: PagerBlock.LayoutMode,
    boundsSize: CGSize
  ) {
    self.model = model
    self.layoutMode = layoutMode
    let fittingSize = PagerFittingSize(boundsSize: boundsSize, direction: model.direction)
    blockFrames = model.frames(
      fitting: fittingSize,
      layoutMode: layoutMode
    )
    blockPages = model.pages(
      for: blockFrames,
      fitting: fittingSize
    )

    let contentSize = model.contentSize(
      for: blockFrames,
      fitting: fittingSize
    )

    self.contentSize = model.direction.isHorizontal
      ? CGSize(width: contentSize.width, height: boundsSize.height)
      : CGSize(width: boundsSize.width, height: contentSize.height)
    self.boundsSize = boundsSize
  }

  public static func ==(_ lhs: PagerViewLayout, _ rhs: PagerViewLayout) -> Bool {
    lhs.isEqual(to: rhs.model, boundsSize: rhs.boundsSize)
  }

  public func contentOffset(pageIndex: CGFloat) -> CGFloat {
    let integralIndex = Int(pageIndex)

    guard blockFrames.indices.contains(integralIndex) else {
      return 0
    }

    let page = blockPages[integralIndex]
    let fractionalIndex = pageIndex.truncatingRemainder(dividingBy: 1)
    return min(maxContentOffset, page.origin + page.size * fractionalIndex)
  }

  public func pageIndex(forContentOffset contentOffset: CGFloat) -> CGFloat {
    if let page = blockPages.first(where: { $0.contains(contentOffset) }) {
      let index = CGFloat(page.index)
      return page.size > 0 ? index + (contentOffset - page.origin) / page.size : index
    }

    return 0
  }

  public func isEqual(to model: GalleryViewModel, boundsSize: CGSize) -> Bool {
    self.model == model && self.boundsSize == boundsSize
  }
}

extension GalleryViewModel {
  public func intrinsicPagerSize(
    forWidth width: CGFloat?,
    layoutMode: PagerBlock.LayoutMode
  ) -> CGSize {
    let fittingSize = PagerFittingSize(scrollAxis: width, crossAxis: nil)
    return contentSize(
      for: self.frames(fitting: fittingSize, layoutMode: layoutMode),
      fitting: fittingSize
    )
  }
}

struct PagerFittingSize: Equatable {
  static let unbounded = PagerFittingSize(scrollAxis: nil, crossAxis: nil)

  var scrollAxis: CGFloat?
  var crossAxis: CGFloat?

  init(scrollAxis: CGFloat?, crossAxis: CGFloat?) {
    self.scrollAxis = scrollAxis
    self.crossAxis = crossAxis
  }

  init(boundsSize: CGSize, direction: ScrollDirection) {
    self.scrollAxis = boundsSize.dimension(in: direction)
    self.crossAxis = direction.isHorizontal ? boundsSize.height : boundsSize.width
  }
}

extension GalleryViewModel {
  func frames(
    fitting fittingSize: PagerFittingSize? = nil,
    layoutMode: PagerBlock.LayoutMode
  ) -> [CGRect] {
    let fitting = fittingSize ?? .unbounded
    return switch direction {
    case .horizontal:
      horizontallyOrientedFrames(fitting: fitting, layoutMode: layoutMode)
    case .vertical:
      verticallyOrientedFrames(fitting: fitting, layoutMode: layoutMode)
    }
  }

  fileprivate func pages(
    for frames: [CGRect],
    fitting fittingSize: PagerFittingSize
  ) -> [PagerViewLayout.Page] {
    guard let firstFrame = frames.first, let lastFrame = frames.last else {
      return []
    }

    let bound = fittingSize.scrollAxis ?? 0
    let contentSize = contentSize(
      for: frames,
      fitting: fittingSize
    ).dimension(in: direction)

    let isScrollable = contentSize > bound
    let segmentBoundaries: [CGFloat] = if isScrollable {
      scrollOrigins(
        for: frames,
        fitting: fittingSize,
        bound: bound,
        contentSize: contentSize,
        firstFrame: firstFrame,
        lastFrame: lastFrame
      )
    } else {
      frames.map { $0.origin.dimension(in: direction) }
    }

    return segmentBoundaries.indices.map { index in
      let boundary = segmentBoundaries[index]
      let nextBoundary = index < segmentBoundaries.count - 1
        ? segmentBoundaries[index + 1]
        : contentSize

      return PagerViewLayout.Page(
        index: index,
        origin: isScrollable ? boundary : 0,
        size: nextBoundary - boundary
      )
    }
  }

  private func scrollOrigins(
    for frames: [CGRect],
    fitting fittingSize: PagerFittingSize,
    bound: CGFloat,
    contentSize: CGFloat,
    firstFrame: CGRect,
    lastFrame: CGRect
  ) -> [CGFloat] {
    let firstFrameOrigin = firstFrame.origin.dimension(in: direction)
    let lastFrameOffset = lastGap(
      forFitting: fittingSize,
      elementMainAxisSize: lastFrame.size.dimension(in: direction)
    )
    var frameOrigin = 0.0
    return frames.enumerated().map { index, frame -> CGFloat in
      guard index > 0 else { return 0.0 }

      if index == frames.count - 1, transformation?.style != .overlap {
        return max(contentSize - bound, frameOrigin)
      }

      let alignmentOffset = alignment.offset(
        availableSpace: bound,
        contentSize: frame.size.dimension(in: direction),
        firstFrameOffset: firstFrameOrigin,
        lastFrameOffset: lastFrameOffset
      )
      frameOrigin = frame.origin.dimension(in: direction) - alignmentOffset
      return max(0, frameOrigin)
    }
  }

  fileprivate func contentSize(
    for frames: [CGRect],
    fitting fittingSize: PagerFittingSize
  ) -> CGSize {
    guard let lastFrame = frames.last else { return .zero }

    switch direction {
    case .horizontal:
      let rightGap = lastGap(
        forFitting: fittingSize,
        elementMainAxisSize: lastFrame.size.dimension(in: direction)
      )

      let bottomGap = crossInsets(forFitting: fittingSize).trailing
      let width = lastFrame.maxX + rightGap
      let maxHeight = frames.map(\.maxY).max()!

      return CGSize(width: width, height: maxHeight + bottomGap)
    case .vertical:
      let rightGap = crossInsets(forFitting: fittingSize).trailing
      let bottomGap = lastGap(
        forFitting: fittingSize,
        elementMainAxisSize: lastFrame.size.dimension(in: direction)
      )
      let maxWidth = frames.map(\.maxX).max()!
      let height = lastFrame.maxY + bottomGap
      return CGSize(width: maxWidth + rightGap, height: height)
    }
  }

  fileprivate func pageSize(
    for block: Block,
    fitting fittingSize: PagerFittingSize,
    crossAxisReferenceSize: CGFloat,
    layoutMode: PagerBlock.LayoutMode,
    axialInsets: SideInsets,
    itemSpacing: CGFloat
  ) -> CGFloat {
    let intrinsicScrollAxisSize: () -> CGFloat = {
      switch direction {
      case .horizontal:
        block.intrinsicContentWidth
      case .vertical:
        block.intrinsicContentHeight(forWidth: crossAxisReferenceSize)
      }
    }

    guard let availableSize = fittingSize.scrollAxis else {
      return layoutMode == .pageContentSize ? intrinsicScrollAxisSize() : 0
    }

    guard availableSize > 0 else {
      return 0 // No space, nothing to layout
    }

    return switch layoutMode {
    case let .pageSize(relative):
      relative.absoluteValue(in: availableSize)

    case let .neighbourPageSize(neighbourPageSize):
      neighbourPageScrollAxisSize(
        availableSize: availableSize,
        alignment: alignment,
        neighbourPageSize: neighbourPageSize,
        axialInsets: axialInsets,
        itemSpacing: itemSpacing
      )

    case .pageContentSize:
      pageContentScrollAxisSize(
        availableSize: availableSize,
        block: block,
        direction: direction,
        crossAxisReferenceSize: crossAxisReferenceSize,
        axialInsets: axialInsets
      )
    }
  }

  private func horizontallyOrientedFrames(
    fitting fittingSize: PagerFittingSize,
    layoutMode: PagerBlock.LayoutMode
  ) -> [CGRect] {
    if let scrollAxis = fittingSize.scrollAxis, scrollAxis <= 0 {
      return []
    }

    let blocks = items.map(\.content)
    let axialInsets = self.axialInsets(forFitting: fittingSize)
    let itemSpacing = metrics.spacings.first ?? 0
    let pageWidths = blocks.map { block in
      pageSize(
        for: block,
        fitting: fittingSize,
        crossAxisReferenceSize: 0,
        layoutMode: layoutMode,
        axialInsets: axialInsets,
        itemSpacing: itemSpacing
      )
    }

    let crossInsets = crossInsets(forFitting: fittingSize)
    let maxElementHeight: CGFloat = if let crossAxis = fittingSize.crossAxis {
      max(0, crossAxis - crossInsets.sum)
    } else {
      blocks.maxHeightOfVerticallyNonResizableBlocks(for: pageWidths) ?? 0
    }
    let minY = crossInsets.leading
    let gaps = gaps(forFitting: fittingSize, elementMainAxisSize: pageWidths.first)
    var x = gaps[0]
    return zip3(items, pageWidths, gaps.dropFirst()).map { item, width, gap in
      let block = item.content
      let height = block.isVerticallyResizable ?
        clamp(maxElementHeight, min: block.minHeight, max: block.maxHeight) :
        block.heightOfVerticallyNonResizableBlock(forWidth: width)
      let frame = CGRect(
        x: x,
        y: item.crossAlignment.origin(of: height, minimum: minY, maximum: minY + maxElementHeight),
        width: width,
        height: height
      )
      x = frame.maxX + gap
      return frame
    }
  }

  private func verticallyOrientedFrames(
    fitting fittingSize: PagerFittingSize,
    layoutMode: PagerBlock.LayoutMode
  ) -> [CGRect] {
    let crossInsets = self.crossInsets(forFitting: fittingSize)
    let blocks = items.map(\.content)
    let maxWidth: CGFloat = if let crossAxis = fittingSize.crossAxis {
      max(0, crossAxis - crossInsets.sum)
    } else {
      blocks.maxWidthOfHorizontallyNonResizableBlocks ?? 0
    }

    let minX = crossInsets.leading

    let axialInsets = self.axialInsets(forFitting: fittingSize)
    let itemSpacing = metrics.spacings.first ?? 0
    let heights = blocks.map { block in
      pageSize(
        for: block,
        fitting: fittingSize,
        crossAxisReferenceSize: maxWidth,
        layoutMode: layoutMode,
        axialInsets: axialInsets,
        itemSpacing: itemSpacing
      )
    }

    let gaps = self.gaps(forFitting: fittingSize, elementMainAxisSize: heights.first)
    var y = gaps[0]

    return zip3(items, heights, gaps.dropFirst()).map { item, height, gap in
      let block = item.content
      let width = block.isHorizontallyResizable ?
        clamp(maxWidth, min: block.minWidth, max: block.maxWidth) :
        block.widthOfHorizontallyNonResizableBlock

      let frame = CGRect(
        x: item.crossAlignment.origin(of: width, minimum: minX, maximum: minX + maxWidth),
        y: y,
        width: width,
        height: height
      )

      y = frame.maxY + gap
      return frame
    }
  }

  private func crossInsets(forFitting fittingSize: PagerFittingSize) -> SideInsets {
    metrics.crossInsetMode.insets(forSize: fittingSize.scrollAxis ?? 0)
  }

  private func axialInsets(forFitting fittingSize: PagerFittingSize) -> SideInsets {
    metrics.axialInsetMode.insets(forSize: fittingSize.scrollAxis ?? 0)
  }

  private func lastGap(
    forFitting fittingSize: PagerFittingSize,
    elementMainAxisSize: CGFloat?
  ) -> CGFloat {
    let modelInset = metrics.axialInsetMode.insets(forSize: fittingSize.scrollAxis ?? 0)
      .trailing
    let overlapInset: CGFloat = if transformation?.style == .overlap,
                                   let mainAxisSpace = fittingSize.scrollAxis,
                                   let elementSize = elementMainAxisSize {
      (mainAxisSpace - elementSize) / 2
    } else {
      0
    }
    return modelInset + overlapInset
  }

  private func gaps(
    forFitting fittingSize: PagerFittingSize,
    elementMainAxisSize: CGFloat?
  ) -> [CGFloat] {
    let modelInset = metrics.gaps(forSize: fittingSize.scrollAxis ?? 0)
    if transformation?.style == .overlap,
       let mainAxisSpace = fittingSize.scrollAxis,
       let elementSize = elementMainAxisSize {
      let overlapInset = (mainAxisSpace - elementSize) / 2
      return [overlapInset] + modelInset.dropFirst()
    } else {
      return modelInset
    }
  }
}

extension Alignment {
  fileprivate func origin(of dimension: CGFloat, minimum: CGFloat, maximum: CGFloat) -> CGFloat {
    switch self {
    case .leading:
      minimum
    case .center:
      max(minimum, floor((minimum + maximum - dimension) / 2))
    case .trailing:
      max(maximum - dimension, minimum)
    }
  }

  fileprivate func offset(
    availableSpace: CGFloat,
    contentSize: CGFloat,
    firstFrameOffset: CGFloat,
    lastFrameOffset: CGFloat
  ) -> CGFloat {
    switch self {
    case .leading:
      firstFrameOffset
    case .center:
      ((availableSpace - contentSize) * 0.5).roundedToScreenScale
    case .trailing:
      availableSpace - contentSize - lastFrameOffset
    }
  }
}

extension CGPoint {
  fileprivate func dimension(in direction: ScrollDirection) -> CGFloat {
    switch direction {
    case .horizontal:
      x
    case .vertical:
      y
    }
  }
}

extension CGSize {
  fileprivate func dimension(in direction: ScrollDirection) -> CGFloat {
    switch direction {
    case .horizontal:
      width
    case .vertical:
      height
    }
  }
}

fileprivate func pageContentScrollAxisSize(
  availableSize: CGFloat,
  block: Block,
  direction: ScrollDirection,
  crossAxisReferenceSize: CGFloat,
  axialInsets: SideInsets
) -> CGFloat {
  let scrollAxisAvailableSize = max(availableSize - axialInsets.sum, 0)

  return switch direction {
  case .horizontal:
    if block.isHorizontallyResizable {
      clamp(scrollAxisAvailableSize, min: block.minWidth, max: block.maxWidth)
    } else {
      block.intrinsicContentWidth
    }
  case .vertical:
    if block.isVerticallyResizable {
      clamp(scrollAxisAvailableSize, min: block.minHeight, max: block.maxHeight)
    } else {
      block.intrinsicContentHeight(forWidth: crossAxisReferenceSize)
    }
  }
}

fileprivate func neighbourPageScrollAxisSize(
  availableSize: CGFloat,
  alignment: Alignment,
  neighbourPageSize: CGFloat,
  axialInsets: SideInsets,
  itemSpacing: CGFloat
) -> CGFloat {
  let neighbourSize = neighbourPageSize + itemSpacing

  let rawPageSize: CGFloat = switch alignment {
  case .leading:
    availableSize - axialInsets.leading - neighbourSize
  case .center:
    availableSize - neighbourSize * 2
  case .trailing:
    availableSize - axialInsets.trailing - neighbourSize
  }
  return max(0, rawPageSize)
}

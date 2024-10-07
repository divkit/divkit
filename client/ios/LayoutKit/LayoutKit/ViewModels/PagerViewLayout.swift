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
    pageIndex: Int,
    layoutMode: PagerBlock.LayoutMode,
    boundsSize: CGSize
  ) {
    self.model = model
    self.layoutMode = layoutMode
    blockFrames = model.frames(fitting: boundsSize, layoutMode: layoutMode)
    blockPages = model.pages(for: blockFrames, fitting: boundsSize)

    let contentSize = model.contentSize(
      for: blockFrames,
      fitting: boundsSize,
      pageIndex: pageIndex
    )

    self.contentSize = model.direction.isHorizontal
      ? CGSize(width: contentSize.width, height: boundsSize.height)
      : CGSize(width: boundsSize.width, height: contentSize.height)
    self.boundsSize = boundsSize
  }

  public func contentOffset(pageIndex: CGFloat) -> CGFloat {
    let integralIndex = Int(pageIndex)

    guard blockFrames.indices.contains(integralIndex) else {
      return 0
    }

    let page = blockPages[integralIndex]
    let fractionalIndex = pageIndex.truncatingRemainder(dividingBy: 1)
    return page.origin + page.size * fractionalIndex
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

  public static func ==(_ lhs: PagerViewLayout, _ rhs: PagerViewLayout) -> Bool {
    lhs.isEqual(to: rhs.model, boundsSize: rhs.boundsSize)
  }
}

extension GalleryViewModel {
  public func intrinsicPagerSize(
    forWidth width: CGFloat?,
    pageIndex: Int,
    layoutMode: PagerBlock.LayoutMode
  ) -> CGSize {
    let size = width.map { CGSize(width: $0, height: 0) }
    return contentSize(
      for: self.frames(fitting: size, layoutMode: layoutMode),
      fitting: size,
      pageIndex: pageIndex
    )
  }
}

extension GalleryViewModel {
  fileprivate func frames(fitting size: CGSize?, layoutMode: PagerBlock.LayoutMode) -> [CGRect] {
    switch direction {
    case .horizontal:
      horizontallyOrientedFrames(fitting: size, layoutMode: layoutMode)
    case .vertical:
      verticallyOrientedFrames(fitting: size, layoutMode: layoutMode)
    }
  }

  fileprivate func pages(
    for frames: [CGRect],
    fitting size: CGSize?
  ) -> [PagerViewLayout.Page] {
    let bound = (size ?? .zero).dimension(in: direction)
    let contentSize = contentSize(
      for: frames,
      fitting: nil,
      pageIndex: 0
    ).dimension(in: direction)

    let origins = frames.enumerated().map { index, frame -> CGFloat in
      guard index > 0 else { return 0.0 }

      if index == frames.count - 1,
         transformation?.style != .overlap {
        return contentSize - bound
      }

      return frame.origin
        .dimension(in: direction) - ((bound - frame.size.dimension(in: direction)) / 2)
    }

    return (0..<frames.count).map { index in
      let origin = origins[index]
      let nextOrigin = index < origins.count - 1 ? origins[index + 1] : contentSize

      return PagerViewLayout.Page(
        index: index,
        origin: origin,
        size: nextOrigin - origin
      )
    }
  }

  fileprivate func contentSize(
    for frames: [CGRect],
    fitting size: CGSize?,
    pageIndex: Int
  ) -> CGSize {
    guard let lastFrame = frames.last else { return .zero }

    let neigbourFrames = frames[max(0, pageIndex - 1)...min(frames.count - 1, pageIndex + 1)]

    switch direction {
    case .horizontal:
      let rightGap = lastGap(
        forSize: size,
        elementMainAxisSize: lastFrame.size.dimension(in: direction)
      )

      let bottomGap = crossInsets(forSize: size).trailing
      let width = lastFrame.maxX + rightGap
      let maxHeight = neigbourFrames.map(\.maxY).max()!

      return CGSize(width: width, height: maxHeight + bottomGap)
    case .vertical:
      let rightGap = crossInsets(forSize: size).trailing
      let bottomGap = lastGap(
        forSize: size,
        elementMainAxisSize: lastFrame.size.dimension(in: direction)
      )
      let maxWidth = neigbourFrames.map(\.maxX).max()!
      let height = lastFrame.maxY + bottomGap
      return CGSize(width: maxWidth + rightGap, height: height)
    }
  }

  fileprivate func pageSize(
    fitting size: CGSize?,
    layoutMode: PagerBlock.LayoutMode
  ) -> CGFloat {
    let availableSize = size?.dimension(in: direction) ?? 0

    guard availableSize > 0 else {
      return 0.0 // No space, nothing to layout
    }

    switch layoutMode {
    case let .pageSize(relative):
      return relative.absoluteValue(in: availableSize)
    case let .neighbourPageSize(neighbourPageSize):

      let gaps = gaps(forSize: nil, elementMainAxisSize: nil)
      let leadingMargin = gaps.first ?? 0.0
      let trailingMargin = gaps.last ?? 0.0

      let spacing = gaps.dropFirst().dropLast().first ?? 0.0

      return availableSize - max(
        neighbourPageSize * 2 + spacing * 2,
        leadingMargin,
        trailingMargin
      )
    }
  }

  private func horizontallyOrientedFrames(
    fitting size: CGSize?,
    layoutMode: PagerBlock.LayoutMode
  ) -> [CGRect] {
    let blocks = items.map(\.content)
    let pageWidth = pageSize(fitting: size, layoutMode: layoutMode)

    guard pageWidth > 0 else { return [] } // No space, no frames

    let crossInsets = self.crossInsets(forSize: size)

    let maxElementHeight = size.map { $0.height - crossInsets.sum }
      ?? blocks.maxHeightOfVerticallyNonResizableBlocks(for: pageWidth)!

    let minY = crossInsets.leading
    let gaps = self.gaps(forSize: size, elementMainAxisSize: pageWidth)

    var x = gaps[0]

    return zip(items, gaps.dropFirst()).map { item, gap in
      let block = item.content

      let height = block.isVerticallyResizable ?
        maxElementHeight :
        block.heightOfVerticallyNonResizableBlock(forWidth: pageWidth)

      let frame = CGRect(
        x: x,
        y: minY,
        width: pageWidth,
        height: height
      )

      x = frame.maxX + gap
      return frame
    }
  }

  private func verticallyOrientedFrames(
    fitting size: CGSize?,
    layoutMode: PagerBlock.LayoutMode
  ) -> [CGRect] {
    let crossInsets = self.crossInsets(forSize: size)
    let blocks = items.map(\.content)
    let maxWidth = size.map { $0.width - crossInsets.sum } ??
      blocks
      .filter { !$0.isHorizontallyResizable }
      .map(\.widthOfHorizontallyNonResizableBlock).max()!

    let minX = crossInsets.leading

    let heights = (0..<blocks.count).map { _ in
      pageSize(fitting: size, layoutMode: layoutMode)
    }

    let gaps = self.gaps(forSize: size, elementMainAxisSize: heights.first)
    var y = gaps[0]

    return zip3(items, heights, gaps.dropFirst()).map { item, height, gap in
      let block = item.content
      let width = block.isHorizontallyResizable ?
        maxWidth :
        block.widthOfHorizontallyNonResizableBlock

      let frame = CGRect(x: minX, y: y, width: width, height: height)

      y = frame.maxY + gap
      return frame
    }
  }

  private func crossInsets(forSize size: CGSize?) -> SideInsets {
    metrics.crossInsetMode.insets(forSize: size?.dimension(in: direction) ?? 0)
  }

  private func lastGap(forSize size: CGSize?, elementMainAxisSize: CGFloat?) -> CGFloat {
    let modelInset = metrics.axialInsetMode.insets(forSize: size?.dimension(in: direction) ?? 0)
      .trailing
    let overlapInset: CGFloat = if transformation?.style == .overlap,
                                   let mainAxisSpace = size?.dimension(in: direction),
                                   let elementSize = elementMainAxisSize {
      (mainAxisSpace - elementSize) / 2
    } else {
      0
    }
    return modelInset + overlapInset
  }

  private func gaps(forSize size: CGSize?, elementMainAxisSize: CGFloat?) -> [CGFloat] {
    let modelInset = metrics.gaps(forSize: size?.dimension(in: direction) ?? 0)
    if transformation?.style == .overlap,
       let mainAxisSpace = size?.dimension(in: direction),
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
      floor((minimum + maximum - dimension) / 2)
    case .trailing:
      maximum - dimension
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

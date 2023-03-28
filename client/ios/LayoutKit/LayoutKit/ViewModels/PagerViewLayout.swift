import CoreGraphics
import Foundation

import BaseUIPublic
import CommonCorePublic

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
    blockPages.map { $0.origin }
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
    blockPages = model.pages(for: blockFrames, fitting: boundsSize, layoutMode: layoutMode)
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
    guard blockPages.indices.contains(integralIndex) else {
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
      return horizontallyOrientedFrames(fitting: size, layoutMode: layoutMode)
    case .vertical:
      return verticallyOrientedFrames(fitting: size, layoutMode: layoutMode)
    }
  }

  fileprivate func pages(
    for frames: [CGRect],
    fitting size: CGSize?,
    layoutMode: PagerBlock.LayoutMode
  ) -> [PagerViewLayout.Page] {
    guard let lastFrame = frames.last else {
      return []
    }

    let lastOrigin = lastFrame.origin.dimension(in: direction)
    let lastSize = lastFrame.size.dimension(in: direction)
    let lastEdge = lastOrigin + lastSize + lastGap(forSize: size)
    let maxOffset = lastEdge - (size?.dimension(in: direction) ?? lastSize)
    let origins = frames.enumerated().map { index, frame -> CGFloat in
      let origin = frame.origin.dimension(in: direction)
      let bound = (size ?? .zero).dimension(in: direction)
      let pageSize = pageSize(
        index: index,
        fitting: size,
        layoutMode: layoutMode
      )
      return min(max(0, origin - (bound - pageSize) / 2), maxOffset)
    }

    return (0..<frames.count).map { index in
      let origin = origins[index]
      let nextOrigin = index < origins.count - 1 ? origins[index + 1] : lastEdge
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
    let lastFrame = frames.last!
    let neigbourFrames = frames[max(0, pageIndex - 1)...min(frames.count - 1, pageIndex + 1)]
    switch direction {
    case .horizontal:
      let rightGap = lastGap(forSize: size)
      let bottomGap = crossInsets(forSize: size).trailing
      let width = lastFrame.maxX + rightGap
      let maxHeight = neigbourFrames.map { $0.maxY }.max()!
      return CGSize(width: width, height: maxHeight + bottomGap)
    case .vertical:
      let rightGap = crossInsets(forSize: size).trailing
      let bottomGap = lastGap(forSize: size)
      let maxWidth = neigbourFrames.map { $0.maxX }.max()!
      let height = lastFrame.maxY + bottomGap
      return CGSize(width: maxWidth + rightGap, height: height)
    }
  }

  fileprivate func pageSize(
    index: Int,
    fitting size: CGSize?,
    layoutMode: PagerBlock.LayoutMode
  ) -> CGFloat {
    let availableSize = size?.dimension(in: direction) ?? 0
    switch layoutMode {
    case let .pageSize(relative):
      return relative.absoluteValue(in: availableSize)
    case let .neighbourPageSize(neighbourPageSize):
      let gaps = gaps(forSize: nil)
      let leadingMargin = gaps.element(at: index) ?? 0
      let trailingMargin = gaps.element(at: index + 1) ?? 0
      let margins = leadingMargin + trailingMargin
      return availableSize - neighbourPageSize - margins
    }
  }

  private func horizontallyOrientedFrames(
    fitting size: CGSize?,
    layoutMode: PagerBlock.LayoutMode
  ) -> [CGRect] {
    let blocks = items.map { $0.content }
    let widths = (0..<blocks.count).map {
      pageSize(index: $0, fitting: size, layoutMode: layoutMode)
    }
    let crossInsets = self.crossInsets(forSize: size)
    let maxElementHeight = size.map { $0.height - crossInsets.sum }
      ?? blocks.maxHeightOfVerticallyNonResizableBlocks(for: widths)!

    let minY = crossInsets.leading
    let gaps = self.gaps(forSize: size)
    var x = gaps[0]
    return zip3(items, widths, gaps.dropFirst()).map { item, width, gap in
      let block = item.content
      let height = block.isVerticallyResizable ?
        maxElementHeight :
        block.heightOfVerticallyNonResizableBlock(forWidth: width)
      let frame = CGRect(x: x, y: minY, width: width, height: height)

      x = frame.maxX + gap
      return frame
    }
  }

  private func verticallyOrientedFrames(
    fitting size: CGSize?,
    layoutMode: PagerBlock.LayoutMode
  ) -> [CGRect] {
    let crossInsets = self.crossInsets(forSize: size)
    let blocks = items.map { $0.content }
    let maxWidth = size.map { $0.width - crossInsets.sum } ??
      blocks
      .filter { !$0.isHorizontallyResizable }
      .map { $0.widthOfHorizontallyNonResizableBlock }.max()!

    let minX = crossInsets.leading
    let gaps = self.gaps(forSize: size)
    var y = gaps[0]

    let heights = (0..<blocks.count).map {
      pageSize(index: $0, fitting: size, layoutMode: layoutMode)
    }

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

  private func lastGap(forSize size: CGSize?) -> CGFloat {
    metrics.axialInsetMode.insets(forSize: size?.dimension(in: direction) ?? 0).trailing
  }

  private func gaps(forSize size: CGSize?) -> [CGFloat] {
    metrics.gaps(forSize: size?.dimension(in: direction) ?? 0)
  }
}

extension Alignment {
  fileprivate func origin(of dimension: CGFloat, minimum: CGFloat, maximum: CGFloat) -> CGFloat {
    switch self {
    case .leading:
      return minimum
    case .center:
      return floor((minimum + maximum - dimension) / 2)
    case .trailing:
      return maximum - dimension
    }
  }
}

extension CGPoint {
  fileprivate func dimension(in direction: GalleryViewModel.Direction) -> CGFloat {
    switch direction {
    case .horizontal:
      return x
    case .vertical:
      return y
    }
  }
}

extension CGSize {
  fileprivate func dimension(in direction: GalleryViewModel.Direction) -> CGFloat {
    switch direction {
    case .horizontal:
      return width
    case .vertical:
      return height
    }
  }
}

import CoreGraphics
import Foundation

import BaseUIPublic
import CommonCorePublic

public protocol GalleryViewLayouting {
  var pageOrigins: [CGFloat] { get }
  var blockFrames: [CGRect] { get }
  var contentSize: CGSize { get }

  func contentOffset(pageIndex: CGFloat) -> CGFloat
  func pageIndex(forContentOffset contentOffset: CGFloat) -> CGFloat
  func isEqual(to model: GalleryViewModel, boundsSize: CGSize) -> Bool
}

public struct GalleryViewLayout: GalleryViewLayouting, Equatable {
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
  public let boundsSize: CGSize
  public let blockFrames: [CGRect]
  public let blockPages: [Page]
  public let contentSize: CGSize

  public var pageOrigins: [CGFloat] {
    blockPages.map { $0.origin }
  }

  public init(model: GalleryViewModel, boundsSize: CGSize? = nil) {
    self.model = model
    blockFrames = model.frames(fitting: boundsSize)
    blockPages = model.pages(for: blockFrames, fitting: boundsSize)
    let contentSize = model.contentSize(for: blockFrames, fitting: boundsSize)
    self.contentSize = contentSize
    self.boundsSize = boundsSize ?? contentSize
  }

  public func contentOffset(pageIndex: CGFloat) -> CGFloat {
    let integralIndex = Int(pageIndex)
    guard blockPages.indices.contains(integralIndex) else {
      return 0
    }

    let page = blockPages[integralIndex]
    let fractionalIndex = pageIndex.truncatingRemainder(dividingBy: 1)
    let maxOffset: CGFloat
    switch model.direction {
    case .horizontal:
      maxOffset = max(0, contentSize.width - boundsSize.width)
    case .vertical:
      maxOffset = max(0, contentSize.height - boundsSize.height)
    }
    return min(maxOffset, page.origin + page.size * fractionalIndex)
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

  public static func ==(_ lhs: GalleryViewLayout, _ rhs: GalleryViewLayout) -> Bool {
    lhs.isEqual(to: rhs.model, boundsSize: rhs.boundsSize)
  }
}

extension GalleryViewModel {
  public var intrinsicSize: CGSize {
    let frames = self.frames(fitting: nil)
    return contentSize(for: frames, fitting: nil)
  }
}

extension GalleryViewModel {
  fileprivate func frames(fitting size: CGSize?) -> [CGRect] {
    switch direction {
    case .horizontal:
      return horizontallyOrientedFrames(fitting: size)
    case .vertical:
      return verticallyOrientedFrames(fitting: size)
    }
  }

  fileprivate func pages(for frames: [CGRect], fitting size: CGSize?) -> [GalleryViewLayout.Page] {
    guard let lastFrame = frames.last else {
      return []
    }

    let lastOrigin = lastFrame.origin.dimension(in: direction)
    let lastSize = lastFrame.size.dimension(in: direction)
    let lastEdge = lastOrigin + lastSize + lastGap(forSize: size)
    let pageSize = self.pageSize(fitting: size)

    let origins = frames.map { frame -> CGFloat in
      let origin = frame.origin.dimension(in: direction)
      switch scrollMode {
      case .default:
        return origin
      case .autoPaging, .fixedPaging:
        let size = frame.size.dimension(in: direction)
        return max(0, origin - (pageSize - size) / 2)
      }
    }

    return (0..<frames.count).map { index in
      let origin = origins[index]
      let nextOrigin = index < origins.count - 1 ? origins[index + 1] : lastEdge
      return GalleryViewLayout.Page(
        index: index,
        origin: origin,
        size: nextOrigin - origin
      )
    }
  }

  fileprivate func contentSize(for frames: [CGRect], fitting size: CGSize?) -> CGSize {
    let lastFrame = frames.last!
    switch direction {
    case .horizontal:
      let rightGap = lastGap(forSize: size)
      let bottomGap = crossInsets(forSize: size).trailing
      let width = lastFrame.maxX + rightGap
      let maxHeight = frames.map { $0.maxY }.max()! + bottomGap
      return CGSize(width: width, height: maxHeight)
    case .vertical:
      let rightGap = crossInsets(forSize: size).trailing
      let bottomGap = lastGap(forSize: size)
      let maxWidth = frames.map { $0.maxX }.max()! + rightGap
      let height = lastFrame.maxY + bottomGap
      return CGSize(width: maxWidth, height: height)
    }
  }

  fileprivate func pageSize(fitting size: CGSize?) -> CGFloat {
    switch scrollMode {
    case .default:
      return 0
    case .autoPaging:
      return size?.dimension(in: direction) ?? 0
    case let .fixedPaging(pageSize: value):
      return value
    }
  }

  private func horizontallyOrientedFrames(fitting size: CGSize?) -> [CGRect] {
    let blocks = items.map { $0.content }
    let widths = blocks.map {
      $0.isHorizontallyResizable
        ? size?.width ?? 0
        : $0.widthOfHorizontallyNonResizableBlock
    }
    let crossInsets = self.crossInsets(forSize: size)
    let crossSpacing = metrics.crossSpacing
    let maxItemHeight: CGFloat
    if let size = size {
      maxItemHeight = (size.height - crossInsets.sum - crossSpacing * CGFloat(columnCount - 1)) /
        CGFloat(columnCount)
    } else if let maxNonResizableHeight = blocks
      .maxHeightOfVerticallyNonResizableBlocks(for: widths) {
      maxItemHeight = maxNonResizableHeight
    } else {
      assertionFailure("Unable to calculate maxItemHeight")
      maxItemHeight = 0
    }

    let gaps = self.gaps(forSize: size)
    var xs = [CGFloat](repeating: gaps[0], times: UInt(columnCount))
    var currentColumnIndex = 0
    return zip3(items, widths, gaps.dropFirst()).map { item, width, gap in
      let block = item.content
      let height = block.isVerticallyResizable
        ? maxItemHeight
        : block.heightOfVerticallyNonResizableBlock(forWidth: width)
      let minY = crossInsets.leading + (maxItemHeight + crossSpacing) * CGFloat(currentColumnIndex)
      let maxY = minY + maxItemHeight
      let frame = CGRect(
        x: xs[currentColumnIndex],
        y: item.crossAlignment.origin(of: height, minimum: minY, maximum: maxY),
        width: width,
        height: height
      )
      xs[currentColumnIndex] = frame.maxX + gap
      let nextColumnIndex = currentColumnIndex == xs.lastElementIndex ? 0 : currentColumnIndex + 1
      if xs[currentColumnIndex] >= xs[nextColumnIndex] {
        currentColumnIndex = nextColumnIndex
      }
      return frame
    }
  }

  private func verticallyOrientedFrames(fitting size: CGSize?) -> [CGRect] {
    let crossInsets = self.crossInsets(forSize: size)
    let crossSpacing = metrics.crossSpacing
    let maxItemWidth: CGFloat
    if let size = size {
      maxItemWidth = (size.width - crossInsets.sum - crossSpacing * CGFloat(columnCount - 1)) /
        CGFloat(columnCount)
    } else {
      let blocks = items.map { $0.content }
      if let maxNonResizebleWidth = blocks.maxWidthOfHorizontallyNonResizableBlocks {
        maxItemWidth = maxNonResizebleWidth
      } else {
        assertionFailure("Unable to calculate maxItemWidth")
        maxItemWidth = 0
      }
    }

    let gaps = self.gaps(forSize: size)
    var ys = [CGFloat](repeating: gaps[0], times: UInt(columnCount))
    var currentColumnIndex = 0
    return zip(items, gaps.dropFirst()).map { item, gap in
      let block = item.content
      let width = block.isHorizontallyResizable
        ? maxItemWidth
        : block.widthOfHorizontallyNonResizableBlock
      let height = block.isVerticallyResizable
        ? size?.height ?? 0
        : block.heightOfVerticallyNonResizableBlock(forWidth: width)
      let minX = crossInsets.leading + (maxItemWidth + crossSpacing) * CGFloat(currentColumnIndex)
      let maxX = minX + maxItemWidth
      let frame = CGRect(
        x: item.crossAlignment.origin(of: width, minimum: minX, maximum: maxX),
        y: ys[currentColumnIndex],
        width: width,
        height: height
      )
      ys[currentColumnIndex] = frame.maxY + gap
      let nextColumnIndex = currentColumnIndex == ys.lastElementIndex ? 0 : currentColumnIndex + 1
      if ys[currentColumnIndex] >= ys[nextColumnIndex] {
        currentColumnIndex = nextColumnIndex
      }
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

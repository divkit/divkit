import CoreGraphics
import Foundation
import VGSL

public protocol GalleryViewLayouting {
  var pageOrigins: [CGFloat] { get }
  var blockFrames: [CGRect] { get }
  var contentSize: CGSize { get }
  var boundsSize: CGSize { get }
  var transformation: ElementsTransformation? { get }
  var scrollDirection: ScrollDirection { get }

  func contentOffset(pageIndex: CGFloat) -> CGFloat
  func pageIndex(forContentOffset contentOffset: CGFloat) -> CGFloat
  func isEqual(to model: GalleryViewModel, boundsSize: CGSize) -> Bool
}

extension GalleryViewLayouting {
  var maxContentOffset: CGFloat {
    max(0, scrollDirection.axial(contentSize).along - scrollDirection.axial(boundsSize).along)
  }

  func contentOffset(pageIndex: CGFloat, in page: (some ScrollPage)?) -> CGFloat {
    guard let page else {
      return 0
    }
    let fractionalIndex = pageIndex.truncatingRemainder(dividingBy: 1)
    return min(maxContentOffset, page.origin + page.size * fractionalIndex)
  }

  func pageIndex(forContentOffset contentOffset: CGFloat, in pages: [some ScrollPage]) -> CGFloat {
    guard let page = pages.first(where: { $0.contains(contentOffset) }) else {
      return 0
    }
    let index = CGFloat(page.index)
    return page.size > 0 ? index + (contentOffset - page.origin) / page.size : index
  }
}

/// A segment of the scroll axis that corresponds to a page index.
protocol ScrollPage {
  var index: Int { get }
  var origin: CGFloat { get }
  var size: CGFloat { get }
}

extension ScrollPage {
  func contains(_ offset: CGFloat) -> Bool {
    let range = origin..<origin + size
    return range.contains(offset)
  }
}

public struct GalleryViewLayout: GalleryViewLayouting, Equatable {
  public struct Page: ScrollPage {
    public let index: Int
    public let origin: CGFloat
    public let size: CGFloat

    fileprivate let mappedFrameIndices: [Int]
  }

  public let model: GalleryViewModel
  public let boundsSize: CGSize
  public let blockFrames: [CGRect]
  public let blockPages: [Page]
  public let contentSize: CGSize

  private let frameIndexToPage: [Int: Page]

  public var transformation: ElementsTransformation? {
    model.transformation
  }

  public var scrollDirection: ScrollDirection {
    model.direction
  }

  public var pageOrigins: [CGFloat] {
    blockPages.map(\.origin)
  }

  public init(model: GalleryViewModel, boundsSize: CGSize? = nil) {
    self.model = model
    let bounds = model.direction.bounds(boundsSize)
    blockFrames = model.frames(fitting: bounds)
    let contentSize = model.contentSize(for: blockFrames, fitting: bounds)
    blockPages = model.pages(
      for: blockFrames,
      contentSize: contentSize,
      fitting: bounds
    )

    frameIndexToPage = blockPages.reduce(into: [Int: Page]()) { res, page in
      for mappedFrameIndie in page.mappedFrameIndices {
        res[mappedFrameIndie] = page
      }
    }

    self.contentSize = contentSize
    self.boundsSize = boundsSize ?? contentSize
  }

  public static func ==(_ lhs: GalleryViewLayout, _ rhs: GalleryViewLayout) -> Bool {
    lhs.isEqual(to: rhs.model, boundsSize: rhs.boundsSize)
  }

  public func contentOffset(pageIndex: CGFloat) -> CGFloat {
    contentOffset(pageIndex: pageIndex, in: frameIndexToPage[Int(pageIndex)])
  }

  public func pageIndex(forContentOffset contentOffset: CGFloat) -> CGFloat {
    pageIndex(forContentOffset: contentOffset, in: blockPages)
  }

  public func isEqual(to model: GalleryViewModel, boundsSize: CGSize) -> Bool {
    self.model == model && self.boundsSize == boundsSize
  }
}

extension GalleryViewModel {
  func intrinsicSize(fitting bounds: AxialBounds) -> CGSize {
    contentSize(for: frames(fitting: bounds), fitting: bounds)
  }
}

extension GalleryViewModel {
  fileprivate func frames(fitting bounds: AxialBounds) -> [CGRect] {
    switch direction {
    case .horizontal:
      frames(fitting: bounds, measurement: HorizontalScrollMeasurement())
    case .vertical:
      frames(fitting: bounds, measurement: VerticalScrollMeasurement())
    }
  }

  private func frames(
    fitting bounds: AxialBounds,
    measurement: some AxisMeasurement
  ) -> [CGRect] {
    let gaps = metrics.gaps(fitting: bounds)
    let paddings = (gaps.first ?? 0) + (gaps.last ?? 0)
    let crossInsets = metrics.crossInsets(fitting: bounds)
    let measured = measurement.measure(
      items,
      boundedAcross: CrossAxisSizing.boundedCrossAxisSize(
        crossAxis: bounds.across,
        crossInsets: crossInsets,
        columnCount: columnCount,
        crossSpacing: metrics.crossSpacing
      )
    ) { block, acrossSize in
      measurement.sizeAlongScroll(
        of: block,
        limit: (bounds.along ?? 0) - paddings,
        acrossSize: acrossSize
      )
    }

    return direction.placeItems(
      measured.sizes,
      alignments: items.map(\.crossAlignment),
      gaps: gaps,
      slots: CrossAxisSlots(
        leading: crossInsets.leading,
        size: measured.slotAcross,
        spacing: metrics.crossSpacing,
        count: columnCount
      )
    )
  }

  fileprivate func pages(
    for frames: [CGRect],
    contentSize: CGSize,
    fitting bounds: AxialBounds
  ) -> [GalleryViewLayout.Page] {
    guard let firstFrame = frames.first else {
      return []
    }

    let firstFrameOrigin = direction.along(firstFrame.origin)
    let lastEdge = direction.axial(contentSize).along
    let pageSize = self.pageSize(fitting: bounds)

    let originsWithIndex: [(CGFloat, Int)] =
      frames.enumerated().map { index, frame in
        let frameOrigin = direction.along(frame.origin)

        let frameWidth = direction.axial(frame.size).along
        let origin: CGFloat = switch (scrollMode, scrollAlignment) {
        case (.default, .leading):
          frameOrigin - firstFrameOrigin
        case (.autoPaging, .leading), (.fixedPaging, .leading):
          frameOrigin
        case (_, .center):
          max(0, frameOrigin - (pageSize - frameWidth) / 2)
        case (_, .trailing):
          max(0, frameOrigin - (pageSize - frameWidth))
        }

        return (origin, index)
      }

    let pageOriginToFrameIndex = Dictionary(
      grouping: originsWithIndex, by: \.0
    ).mapValues { $0.map(\.1) }

    let origins = originsWithIndex.map(\.0).uniqueElements

    return (0..<origins.count).map { index in
      let origin = origins[index]
      let nextOrigin = index < origins.count - 1 ? origins[index + 1] : lastEdge
      return GalleryViewLayout.Page(
        index: index,
        origin: origin,
        size: nextOrigin - origin,
        mappedFrameIndices: pageOriginToFrameIndex[origin] ?? []
      )
    }
  }

  fileprivate func contentSize(for frames: [CGRect], fitting bounds: AxialBounds) -> CGSize {
    direction.contentSize(
      of: frames,
      trailingGap: metrics.axialInsets(fitting: bounds).trailing,
      trailingCrossInset: metrics.crossInsets(fitting: bounds).trailing
    )
  }

  fileprivate func pageSize(fitting bounds: AxialBounds) -> CGFloat {
    switch scrollMode {
    case .autoPaging, .default:
      bounds.along ?? 0
    case let .fixedPaging(pageSize: value):
      value
    }
  }
}

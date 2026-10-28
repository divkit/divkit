import CoreGraphics
import Foundation
import VGSL

public struct PagerViewLayout: GalleryViewLayouting, Equatable {
  public struct Page: ScrollPage {
    public let index: Int
    public let origin: CGFloat
    public let size: CGFloat
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
    let fittingSize = model.direction.bounds(boundsSize)
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

    self.contentSize = model.direction.size(AxialSize(
      along: model.direction.axial(contentSize).along,
      across: model.direction.axial(boundsSize).across
    ))
    self.boundsSize = boundsSize
  }

  public static func ==(_ lhs: PagerViewLayout, _ rhs: PagerViewLayout) -> Bool {
    lhs.isEqual(to: rhs.model, boundsSize: rhs.boundsSize)
  }

  public func contentOffset(pageIndex: CGFloat) -> CGFloat {
    let integralIndex = Int(pageIndex)
    return contentOffset(
      pageIndex: pageIndex,
      in: blockPages.indices.contains(integralIndex) ? blockPages[integralIndex] : nil
    )
  }

  public func pageIndex(forContentOffset contentOffset: CGFloat) -> CGFloat {
    pageIndex(forContentOffset: contentOffset, in: blockPages)
  }

  public func isEqual(to model: GalleryViewModel, boundsSize: CGSize) -> Bool {
    self.model == model && self.boundsSize == boundsSize
  }
}

extension GalleryViewModel {
  func intrinsicPagerSize(
    fitting fittingSize: AxialBounds,
    layoutMode: PagerBlock.LayoutMode
  ) -> CGSize {
    contentSize(
      for: frames(fitting: fittingSize, layoutMode: layoutMode),
      fitting: fittingSize
    )
  }
}

extension GalleryViewModel {
  func frames(
    fitting fittingSize: AxialBounds? = nil,
    layoutMode: PagerBlock.LayoutMode
  ) -> [CGRect] {
    layoutFrames(fitting: fittingSize ?? .unbounded, layoutMode: layoutMode)
  }

  fileprivate func pages(
    for frames: [CGRect],
    fitting fittingSize: AxialBounds
  ) -> [PagerViewLayout.Page] {
    guard let firstFrame = frames.first, let lastFrame = frames.last else {
      return []
    }

    let bound = fittingSize.along ?? 0
    let contentSize = direction.axial(contentSize(
      for: frames,
      fitting: fittingSize
    )).along

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
      frames.map { direction.along($0.origin) }
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
    fitting fittingSize: AxialBounds,
    bound: CGFloat,
    contentSize: CGFloat,
    firstFrame: CGRect,
    lastFrame: CGRect
  ) -> [CGFloat] {
    let firstFrameOrigin = direction.along(firstFrame.origin)
    let lastFrameOffset = lastGap(
      forFitting: fittingSize,
      elementMainAxisSize: direction.axial(lastFrame.size).along
    )
    var frameOrigin = 0.0
    return frames.enumerated().map { index, frame -> CGFloat in
      guard index > 0 else { return 0.0 }

      if index == frames.count - 1, transformation?.style != .overlap {
        return max(contentSize - bound, frameOrigin)
      }

      let alignmentOffset = alignment.offset(
        availableSpace: bound,
        contentSize: direction.axial(frame.size).along,
        firstFrameOffset: firstFrameOrigin,
        lastFrameOffset: lastFrameOffset
      )
      frameOrigin = direction.along(frame.origin) - alignmentOffset
      return max(0, frameOrigin)
    }
  }

  fileprivate func contentSize(
    for frames: [CGRect],
    fitting fittingSize: AxialBounds
  ) -> CGSize {
    guard let lastFrame = frames.last else {
      return .zero
    }
    return direction.contentSize(
      of: frames,
      trailingGap: lastGap(
        forFitting: fittingSize,
        elementMainAxisSize: direction.axial(lastFrame.size).along
      ),
      trailingCrossInset: metrics.crossInsets(fitting: fittingSize).trailing
    )
  }

  private func layoutFrames(
    fitting fittingSize: AxialBounds,
    layoutMode: PagerBlock.LayoutMode
  ) -> [CGRect] {
    if let scrollAxis = fittingSize.along, scrollAxis <= 0 {
      return []
    }

    return switch direction {
    case .horizontal:
      layoutFrames(
        fitting: fittingSize,
        layoutMode: layoutMode,
        measurement: HorizontalScrollMeasurement()
      )
    case .vertical:
      layoutFrames(
        fitting: fittingSize,
        layoutMode: layoutMode,
        measurement: VerticalScrollMeasurement()
      )
    }
  }

  private func layoutFrames(
    fitting fittingSize: AxialBounds,
    layoutMode: PagerBlock.LayoutMode,
    measurement: some AxisMeasurement
  ) -> [CGRect] {
    let axialInsets = metrics.axialInsets(fitting: fittingSize)
    let itemSpacing = metrics.spacings.first ?? 0
    let crossInsets = metrics.crossInsets(fitting: fittingSize)
    let measured = measurement.measure(
      items,
      boundedAcross: CrossAxisSizing.boundedCrossAxisSize(
        crossAxis: fittingSize.across,
        crossInsets: crossInsets
      )
    ) { block, acrossSize in
      pageSize(
        for: block,
        acrossSize: acrossSize,
        measurement: measurement,
        fitting: fittingSize,
        layoutMode: layoutMode,
        axialInsets: axialInsets,
        itemSpacing: itemSpacing
      )
    }

    return direction.placeItems(
      measured.sizes,
      alignments: items.map(\.crossAlignment),
      gaps: gaps(forFitting: fittingSize, elementMainAxisSize: measured.sizes.first?.along),
      slots: CrossAxisSlots(leading: crossInsets.leading, size: measured.slotAcross)
    )
  }

  private func pageSize(
    for block: Block,
    acrossSize: CGFloat,
    measurement: some AxisMeasurement,
    fitting fittingSize: AxialBounds,
    layoutMode: PagerBlock.LayoutMode,
    axialInsets: SideInsets,
    itemSpacing: CGFloat
  ) -> CGFloat {
    guard let availableSize = fittingSize.along else {
      return layoutMode == .pageContentSize
        ? measurement.intrinsicSizeAlongScroll(of: block, acrossSize: acrossSize)
        : 0
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
      measurement.isResizableAlongScroll(block)
        ? measurement.sizeAlongScroll(
          of: block,
          limit: availableSize - axialInsets.sum,
          acrossSize: acrossSize
        )
        : measurement.intrinsicSizeAlongScroll(of: block, acrossSize: acrossSize)
    }
  }

  /// The trailing axial inset, extended by the overlap inset.
  private func lastGap(
    forFitting fittingSize: AxialBounds,
    elementMainAxisSize: CGFloat?
  ) -> CGFloat {
    metrics.axialInsets(fitting: fittingSize).trailing
      + (overlapInset(forFitting: fittingSize, elementMainAxisSize: elementMainAxisSize) ?? 0)
  }

  /// The gaps with the leading axial inset replaced by the overlap inset.
  private func gaps(
    forFitting fittingSize: AxialBounds,
    elementMainAxisSize: CGFloat?
  ) -> [CGFloat] {
    let gaps = metrics.gaps(fitting: fittingSize)
    guard let overlapInset = overlapInset(
      forFitting: fittingSize,
      elementMainAxisSize: elementMainAxisSize
    ) else {
      return gaps
    }
    return [overlapInset] + gaps.dropFirst()
  }

  /// Inset that centers a page in the viewport when pages overlap.
  private func overlapInset(
    forFitting fittingSize: AxialBounds,
    elementMainAxisSize: CGFloat?
  ) -> CGFloat? {
    guard transformation?.style == .overlap,
          let mainAxisSpace = fittingSize.along,
          let elementSize = elementMainAxisSize else {
      return nil
    }
    return (mainAxisSpace - elementSize) / 2
  }
}

extension Alignment {
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

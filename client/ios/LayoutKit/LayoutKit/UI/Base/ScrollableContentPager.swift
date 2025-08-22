import CoreGraphics
import VGSL

public final class ScrollableContentPager: NSObject {
  public private(set) var currentPageIndex: Int?
  public private(set) var lastTargetPageIndex: Int?

  private(set) var isHorizontal = true

  var lastTargetOffset: CGFloat = .zero

  private var indexedPageOrigins: [(origin: CGFloat, index: Int)] = []
  private var isPagingEnabled = true

  public func setPageOrigins(
    _ pageOrigins: [CGFloat],
    withPagingEnabled pagingEnabled: Bool,
    isHorizontal: Bool
  ) {
    indexedPageOrigins = zip(pageOrigins, pageOrigins.indices).map { (origin: $0.0, index: $0.1) }
    isPagingEnabled = pagingEnabled
    self.isHorizontal = isHorizontal

    if !pagingEnabled {
      currentPageIndex = nil
    }
  }

  public func setInitialOffset(_ offset: CGFloat) {
    guard isPagingEnabled else { return }

    currentPageIndex = pageIndex(forOffset: offset)
  }

  public func targetPageOffset(forProposedOffset offset: CGFloat, velocity: CGFloat) -> CGFloat? {
    guard let startPageIndex = currentPageIndex else { return nil }

    var resultPageIndex = pageIndex(forOffset: offset)

    if resultPageIndex == startPageIndex && velocity.isApproximatelyNotEqualTo(0) ||
      resultPageIndex == indexedPageOrigins
      .count - 2 && resultPageIndex <= lastTargetPageIndex ?? 0 && velocity
      .isApproximatelyGreaterThan(0) {
      let forcedIndexOffset = (velocity > 0 ? 1 : -1)
      resultPageIndex += forcedIndexOffset
    }
    resultPageIndex = clamp(
      resultPageIndex,
      min: 0,
      max: indexedPageOrigins.count - 1
    )

    currentPageIndex = resultPageIndex
    lastTargetPageIndex = resultPageIndex
    return indexedPageOrigins[resultPageIndex].origin
  }

  private func pageIndex(forOffset offset: CGFloat) -> Int {
    Int(round(intermediatePageIndex(forOffset: offset)))
  }

  private func intermediatePageIndex(forOffset offset: CGFloat) -> Float {
    guard let rightBound = indexedPageOrigins.first(where: {
      $0.origin.isApproximatelyGreaterOrEqualThan(offset)
    }) else { return Float(indexedPageOrigins.count - 1) }

    guard let leftBound = indexedPageOrigins.reversed().first(where: {
      $0.origin.isApproximatelyLessOrEqualThan(offset)
    }) else { return 0 }

    guard rightBound.index != leftBound.index,
          !rightBound.origin.isApproximatelyEqualTo(leftBound.origin) else {
      return Float(leftBound.index)
    }

    let relativePart = (offset - leftBound.origin) / (rightBound.origin - leftBound.origin)
    return Float(leftBound.index) + Float(relativePart)
  }
}

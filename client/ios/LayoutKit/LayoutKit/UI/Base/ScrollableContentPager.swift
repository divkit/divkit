import CoreGraphics

import VGSL

public final class ScrollableContentPager: NSObject {
  private var indexedPageOrigins: [(origin: CGFloat, index: Int)] = []
  private var isPagingEnabled = true
  private(set) var isHorizontal = true

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
    
    if currentPageIndex == nil, indexedPageOrigins.count > 0 {
      currentPageIndex = pageIndex(forOffset: offset)
    }
  }

  public private(set) var currentPageIndex: Int?

  public func targetPageOffset(
    forProposedOffset offset: CGFloat,
    direction: CGPoint,
    isHorizontal: Bool
  ) -> CGFloat? {
    guard let currentIndex = currentPageIndex else {
      return nil
    }
    
    let forcedIndexOffset = direction.forceIndexOffset(isHorizontal: isHorizontal) > 0 ? -1 : 0
    var resultPageIndex = pageIndex(forOffset: offset) + forcedIndexOffset
    
    // If we scrolled to the first/last page, which may be "small" and try to scroll to the next/previous page,
    // we may be thrown to 2 pages instead of the required 1 page.
    // These checks reduce the "sensitivity" of scrolling on corner cases and shift us to 1 page instead of 2 pages.
    if currentPageIndex == indexedPageOrigins.first?.index && resultPageIndex - currentIndex == 2 {
      resultPageIndex = currentIndex + 1
    } else if currentPageIndex == indexedPageOrigins.last?.index && currentIndex - resultPageIndex == 2 {
      resultPageIndex = currentIndex - 1
    }
    
    resultPageIndex = clamp(
      resultPageIndex,
      min: 0,
      max: indexedPageOrigins.count - 1
    )
    currentPageIndex = resultPageIndex
    
    return indexedPageOrigins[resultPageIndex].origin
  }
  
  private func pageIndex(forOffset offset: CGFloat) -> Int {
    guard let rightBound = indexedPageOrigins.first(where: {
      $0.origin.isApproximatelyGreaterOrEqualThan(offset)
    }) else { return indexedPageOrigins.count - 1 }
    
    guard indexedPageOrigins.reversed().first(where: {
      $0.origin.isApproximatelyLessOrEqualThan(offset)
    }) != nil else { return 0 }
    
    return rightBound.index
  }
}

extension CGPoint {
    fileprivate func forceIndexOffset(isHorizontal: Bool) -> CGFloat {
      isHorizontal ? self.x : self.y
    }
}

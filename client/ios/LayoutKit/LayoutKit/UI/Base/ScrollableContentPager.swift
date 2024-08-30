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
    
    if currentPageIndex == nil, lastContentOffset == nil, indexedPageOrigins.count > 0 {
      let initialPageIndex = pageIndex(forOffset: offset)
      let pageOrigin = indexedPageOrigins[initialPageIndex].origin
      let initialContentOffset = isHorizontal ? CGPoint(x: pageOrigin, y: 0) : CGPoint(x: 0, y: pageOrigin)
      
      currentPageIndex = initialPageIndex
      lastContentOffset = initialContentOffset
    }
  }

  public private(set) var currentPageIndex: Int?
  public private(set) var lastContentOffset: CGPoint?

  public func targetPageOffset(forProposedOffset offset: CGFloat, isHorizontal: Bool) -> CGFloat? {
    guard currentPageIndex != nil,
          var newLastContentOffset = lastContentOffset else {
      return nil
    }
    
    var resultPageIndex = pageIndex(forOffset: offset)

    var forcedIndexOffset = 0
    if newLastContentOffset.forceIndexOffset(isHorizontal: isHorizontal).isApproximatelyNotEqualTo(offset) {
      forcedIndexOffset = newLastContentOffset.forceIndexOffset(isHorizontal: isHorizontal) < offset ? 1 : 0
    }
    resultPageIndex += forcedIndexOffset
    
    resultPageIndex = clamp(
      resultPageIndex,
      min: 0,
      max: indexedPageOrigins.count - 1
    )
    
    let targetPageOffset = indexedPageOrigins[resultPageIndex].origin
    if isHorizontal {
      newLastContentOffset.x = targetPageOffset
    } else {
      newLastContentOffset.y = targetPageOffset
    }
    lastContentOffset = newLastContentOffset
    currentPageIndex = resultPageIndex
    return targetPageOffset
  }
  
  private func pageIndex(forOffset offset: CGFloat) -> Int {
    guard indexedPageOrigins.first(where: {
      $0.origin.isApproximatelyGreaterOrEqualThan(offset)
    }) != nil else { return indexedPageOrigins.count - 1 }
    
    guard let leftBound = indexedPageOrigins.reversed().first(where: {
      $0.origin.isApproximatelyLessOrEqualThan(offset)
    }) else { return 0 }
    
    return leftBound.index
  }
}

extension CGPoint {
    fileprivate func forceIndexOffset(isHorizontal: Bool) -> CGFloat {
      isHorizontal ? self.x : self.y
    }
}

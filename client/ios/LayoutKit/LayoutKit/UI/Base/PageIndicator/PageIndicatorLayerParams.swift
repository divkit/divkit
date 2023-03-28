import CoreGraphics

import CommonCorePublic

struct PageIndicatorLayerParams {
  let itemWidth: CGFloat
  let visiblePageCount: Int
  let scrollOffsetProgress: CGFloat
  let offsetX: CGFloat
  let visibleRect: CGRect

  let head: Int
  let tail: Int
  let renderRange: ClosedRange<Int>

  init(
    numberOfPages: Int,
    itemPlacement: PageIndicatorConfiguration.ItemPlacement,
    position: CGFloat,
    boundsSize: CGSize
  ) {
    switch itemPlacement {
    case let .fixed(spaceBetweenCenters):
      itemWidth = spaceBetweenCenters
      if itemWidth != 0 {
        let itemsVisibleWidth = boundsSize.width / itemWidth
        visiblePageCount = min(Int(itemsVisibleWidth), numberOfPages)
      } else {
        visiblePageCount = 0
      }
    case let .stretch(_, maxVisibleItems):
      visiblePageCount = min(maxVisibleItems, numberOfPages)
      itemWidth = boundsSize.width / CGFloat(visiblePageCount)
    }

    let currentPage = Int(floor(position))
    let maxHead = numberOfPages - visiblePageCount
    let halfVisible = visiblePageCount / 2
    let hasHidden = numberOfPages > visiblePageCount

    if hasHidden {
      head = (currentPage - halfVisible).clamp(0...maxHead)
      tail = max(head + visiblePageCount - 1, 0)
    } else {
      head = 0
      tail = max(numberOfPages - 1, 0)
    }

    let scrollStart = head + halfVisible - 1
    let scrollEnd = numberOfPages - halfVisible - visiblePageCount % 2
    let isScrolling = scrollStart < currentPage && currentPage < scrollEnd

    let visibleWidth = itemWidth * CGFloat(visiblePageCount)
    var offsetX = (boundsSize.width - visibleWidth) / 2

    scrollOffsetProgress = isScrolling ? position - floor(position) : 0
    offsetX -= scrollOffsetProgress * itemWidth
    self.offsetX = offsetX

    visibleRect = CGRect(x: offsetX, y: 0, width: visibleWidth, height: boundsSize.height)

    let renderRangeEnd = hasHidden ? tail + 1 : tail
    renderRange = head...renderRangeEnd
  }
}

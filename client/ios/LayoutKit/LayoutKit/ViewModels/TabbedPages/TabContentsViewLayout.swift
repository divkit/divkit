import CoreGraphics
import Foundation

import BasePublic
import BaseUIPublic

struct TabContentsViewLayout: Equatable {
  let pageFrames: [CGRect]
  let footerFrame: CGRect

  init(pages: [Block], footer: Block?, size: CGSize, pagesInsets: SideInsets) {
    let width = size.width - pagesInsets.sum

    let footerHeight = footer?.heightOfVerticallyNonResizableBlock(forWidth: width) ?? 0
    let maxHeight = size.height - footerHeight

    pageFrames = pages.enumerated().map { offset, page in
      let newWidth = page.isHorizontallyResizable ?
        width :
        page.widthOfHorizontallyNonResizableBlock

      let height = page.isVerticallyResizable ?
        maxHeight :
        page.heightOfVerticallyNonResizableBlock(forWidth: newWidth)
      let x = CGFloat(offset) * width + pagesInsets.leading
      let frame = CGRect(x: x, y: 0, width: newWidth, height: height)
      return frame
    }

    footerFrame = CGRect(x: 0, y: maxHeight, width: size.width, height: footerHeight)
  }

  static func intrinsicWidth(
    for pages: [Block],
    footer: Block?,
    pagesInsets: SideInsets
  ) -> CGFloat {
    let width = (pages + footer.asArray()).maxWidthOfHorizontallyNonResizableBlocks ?? 0
    return width + pagesInsets.sum
  }

  static func intrinsicHeight(
    forWidth width: CGFloat,
    pages: [Block],
    pagesHeightMode: TabbedPagesHeightMode,
    selectedPageIndex: CGFloat,
    footer: Block?,
    pagesInsets: EdgeInsets
  ) -> CGFloat {
    let itemWidth = width - pagesInsets.horizontalInsets.sum
    let listHeight: CGFloat
    switch pagesHeightMode {
    case .byHighestPage:
      listHeight = pages.map { $0.intrinsicContentHeight(forWidth: itemWidth) }.max()!
    case .bySelectedPage:
      listHeight = pages.intrinsicContentHeight(
        forWidth: itemWidth,
        selectedPageIndex: selectedPageIndex
      )
    }
    let footerHeight = footer?.heightOfVerticallyNonResizableBlock(forWidth: width) ?? 0
    return listHeight + footerHeight + pagesInsets.verticalInsets.sum
  }
}

extension Array where Element == Block {
  fileprivate func intrinsicContentHeight(
    forWidth width: CGFloat,
    selectedPageIndex index: CGFloat
  ) -> CGFloat {
    let boundedIndex = clamp(
      index,
      min: 0,
      max: Swift.max(0, CGFloat(count) - 1)
    )
    let flooredIndex = boundedIndex.floored()
    let ceiledIndex = boundedIndex.ceiled()
    let fraction = boundedIndex - flooredIndex
    let fromHeight = self[Int(flooredIndex)].intrinsicContentHeight(forWidth: width)
    let toHeight = self[Int(ceiledIndex)].intrinsicContentHeight(forWidth: width)
    return fromHeight.interpolated(to: toHeight, progress: fraction)
  }
}

import CoreGraphics

extension [Block] {
  func intrinsicHeights(forWidth width: CGFloat) -> [CGFloat] {
    map {
      let blockWidth: CGFloat
      if $0.isHorizontallyResizable {
        blockWidth = width
      } else if $0.isHorizontallyConstrained {
        blockWidth = Swift.min($0.widthOfHorizontallyNonResizableBlock, width)
      } else {
        blockWidth = $0.widthOfHorizontallyNonResizableBlock
      }
      return $0.intrinsicContentHeight(forWidth: blockWidth)
    }
  }
}

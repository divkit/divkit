import CoreGraphics

extension [Block] {
  func intrinsicHeights(forWidth width: CGFloat) -> [CGFloat] {
    map {
      let blockWidth: CGFloat = if $0.isHorizontallyResizable {
        width
      } else if $0.isHorizontallyConstrained {
        Swift.min($0.widthOfHorizontallyNonResizableBlock, width)
      } else {
        $0.widthOfHorizontallyNonResizableBlock
      }
      return $0.intrinsicContentHeight(forWidth: blockWidth)
    }
  }
}

import CoreGraphics

extension Array where Element == Block {
  func intrinsicHeights(forWidth width: CGFloat) -> [CGFloat] {
    map {
      let widthConstraint = $0.isHorizontallyResizable
        ? width
        : $0.widthOfHorizontallyNonResizableBlock
      return $0.intrinsicContentHeight(forWidth: widthConstraint)
    }
  }
}

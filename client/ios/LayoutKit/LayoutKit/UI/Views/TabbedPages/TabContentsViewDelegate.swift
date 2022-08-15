import CoreGraphics

protocol TabContentsViewDelegate: AnyObject {
  func tabContentsViewDidChangeRelativeContentOffsetTo(_ offset: CGFloat)
  func tabContentsViewDidEndAnimation()
}

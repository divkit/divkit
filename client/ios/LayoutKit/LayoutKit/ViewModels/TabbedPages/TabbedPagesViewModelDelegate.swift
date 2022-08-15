import CoreGraphics

public protocol TabbedPagesViewModelDelegate: AnyObject {
  func onSelectedPageIndexChanged(_ index: CGFloat, inModel model: TabContentsViewModel)
}

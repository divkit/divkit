#if os(iOS)
import CoreGraphics
import Foundation

protocol TabListViewDelegateTabSelection: AnyObject {
  func tabListViewDidScrollTo(_ offset: CGFloat)
  func tabListViewDidSelectItemAt(_ index: Int, withUrl url: URL?, path: UIElementPath)
}
#endif

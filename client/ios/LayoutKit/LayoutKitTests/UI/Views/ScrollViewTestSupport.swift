#if os(iOS)
@testable import LayoutKit
import UIKit
import VGSL

let scrollBounds = CGRect(x: 0, y: 0, width: 100, height: 100)

func scrollPage(text: String = "Page") -> Block {
  TextBlock(
    widthTrait: .fixed(100),
    heightTrait: .fixed(100),
    text: NSAttributedString(string: text)
  )
}

extension VisibleBoundsTrackingContainer {
  var scrollView: UIScrollView {
    visibleBoundsTrackingSubviews[0] as! UIScrollView
  }
}
#endif

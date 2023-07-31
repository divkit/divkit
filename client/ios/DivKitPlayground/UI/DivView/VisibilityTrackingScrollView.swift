import UIKit

import BaseUIPublic
import CommonCorePublic
import LayoutKit

final class VisibilityTrackingScrollView: UIScrollView {
  var divView: DivView? {
    didSet {
      oldValue?.removeFromSuperview()
      divView.map { addSubview($0) }
      setNeedsLayout()
    }
  }

  private var divViewSize: CGSize {
    divView?.intrinsicContentSize(for: bounds.size) ?? .zero
  }

  var previousVisibleRect: CGRect = .zero

  override func layoutSubviews() {
    super.layoutSubviews()
    divView?.frame = CGRect(origin: .zero, size: divViewSize)
    divView?.layoutIfNeeded()
    let newVisibleRect = bounds.intersection(divView?.frame ?? .zero)
    divView?.onVisibleBoundsChanged(from: previousVisibleRect, to: newVisibleRect)
    previousVisibleRect = newVisibleRect
    contentSize = divViewSize
  }
}

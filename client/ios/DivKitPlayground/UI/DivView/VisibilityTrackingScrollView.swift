import UIKit

import BaseUIPublic
import CommonCorePublic
import DivKit
import LayoutKit

final class VisibilityTrackingScrollView: UIScrollView {
  var divView: DivView? {
    didSet {
      oldValue?.removeFromSuperview()
      sizeChangedSubscription = divView?.addObserver { _ in self.setNeedsLayout() }
      divView.map { addSubview($0) }
      setNeedsLayout()
    }
  }

  private var sizeChangedSubscription: Disposable?

  private var divViewSize: CGSize {
    divView?.cardSize?.sizeFor(parentViewSize: boundsSize) ?? .zero
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

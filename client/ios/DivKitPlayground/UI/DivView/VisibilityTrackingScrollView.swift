import UIKit

import BaseUIPublic
import CommonCorePublic
import DivKit
import LayoutKit

final class VisibilityTrackingScrollView: UIScrollView {
  var divView: DivView? {
    didSet {
      oldValue?.removeFromSuperview()
      sizeChangedSubscription = divView?.addObserver { [weak self] _ in self?.setNeedsLayout() }
      divView.map { addSubview($0) }
      setNeedsLayout()
    }
  }

  private var sizeChangedSubscription: Disposable?

  private var divViewSize: CGSize {
    divView?.cardSize?.sizeFor(parentViewSize: boundsSize) ?? .zero
  }

  override func layoutSubviews() {
    super.layoutSubviews()

    let divViewFrame = CGRect(origin: .zero, size: divViewSize)
    divView?.frame = divViewFrame
    divView?.layoutIfNeeded()
    divView?.onVisibleBoundsChanged(to: bounds.intersection(divViewFrame))
    contentSize = divViewSize
  }
}

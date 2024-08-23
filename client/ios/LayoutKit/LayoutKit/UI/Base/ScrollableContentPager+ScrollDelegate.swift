import Foundation
import UIKit

import VGSL

extension ScrollableContentPager: ScrollDelegate {
  public func onWillBeginDragging(_ scrollView: ScrollView) {
    setInitialOffset(scrollView.contentOffset.projection(isHorizontal))
  }

  public func onWillEndDragging(
    _ scrollView: ScrollView,
    withVelocity velocity: CGPoint,
    targetContentOffset: UnsafeMutablePointer<CGPoint>
  ) {
    var translation: CGPoint?
    if let uiScrollView = scrollView as? UIScrollView {
      translation = uiScrollView.panGestureRecognizer.translation(in: uiScrollView.superview)
    }
    
    let proposedOffset = isHorizontal ? targetContentOffset.pointee.x : targetContentOffset.pointee.y
    if let resultOffset = targetPageOffset(
      forProposedOffset: proposedOffset,
      direction: translation ?? velocity,
      isHorizontal: isHorizontal
    ) {
      if isHorizontal {
        targetContentOffset.pointee.x = resultOffset
      } else {
        targetContentOffset.pointee.y = resultOffset
      }
    }
  }
}

extension CGPoint {
  fileprivate func projection(_ isHorizontal: Bool) -> CGFloat {
    isHorizontal ? x : y
  }
}

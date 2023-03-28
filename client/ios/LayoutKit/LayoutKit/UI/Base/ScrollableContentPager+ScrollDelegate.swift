import Foundation
import UIKit

import CommonCorePublic

extension ScrollableContentPager: ScrollDelegate {
  public func onWillBeginDragging(_ scrollView: ScrollView) {
    setInitialOffset(scrollView.contentOffset.projection(isHorizontal))
  }

  public func onWillEndDragging(
    _: ScrollView,
    withVelocity velocity: CGPoint,
    targetContentOffset: UnsafeMutablePointer<CGPoint>
  ) {
    let proposedOffset = isHorizontal
      ? targetContentOffset.pointee.x
      : targetContentOffset.pointee.y
    if let resultOffset = targetPageOffset(
      forProposedOffset: proposedOffset,
      velocity: velocity.projection(isHorizontal)
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

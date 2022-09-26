// Copyright 2018 Yandex LLC. All rights reserved.

import UIKit

extension UIScrollView: ScrollToDrag {}

extension UIScrollView: ScrollViewType {
  public var boundsSize: CGSize {
    bounds.size
  }

  // Base is exported with minimal iOS version 9.0.
  // It's needed by RealTimeAnalytics pod, which is integrated in YXMobileMetrica.
  @available(iOS 11, tvOS 11, *)
  public func disableContentInsetAdjustmentBehavior() {
    contentInsetAdjustmentBehavior = .never
    if #available(iOS 13, tvOS 13, *) {
      automaticallyAdjustsScrollIndicatorInsets = false
    }
  }

  @inlinable
  public func performWithDetachedDelegate<T>(_ closure: () throws -> T) rethrows -> T {
    let delegate = self.delegate
    self.delegate = nil
    defer {
      self.delegate = delegate
    }
    return try closure()
  }

  @objc public func withDetachedDelegate(_ closure: Action) {
    performWithDetachedDelegate(closure)
  }

  public var isBouncingHorizontally: Bool {
    isBouncingLeft || isBouncingRight
  }

  public var isBouncingLeft: Bool {
    contentOffset.x < -contentInset.left
  }

  public var isBouncingRight: Bool {
    contentSize.width + contentInset.left + contentInset.right >= bounds.width &&
      contentOffset.x > contentSize.width - bounds.width + contentInset.right
  }

  public var isBouncingOnTop: Bool {
    contentOffset.y < -contentInset.top
  }

  public var isContentMoving: Bool {
    isDragging || isDecelerating || isZooming || isZoomBouncing
  }

  public var isVerticallyScrollable: Bool {
    bounds.height < contentSize.height && !alwaysBounceVertical
  }

  public var isHorizontallyScrollable: Bool {
    bounds.width < contentSize.width && !alwaysBounceHorizontal
  }

  // Base is exported with minimal iOS version 9.0.
  // It's needed by RealTimeAnalytics pod, which is integrated in YXMobileMetrica.
  public var adjustedInsetForContent: UIEdgeInsets {
    if #available(iOS 11, tvOS 11, *) {
      return adjustedContentInset
    } else {
      return contentInset
    }
  }
}

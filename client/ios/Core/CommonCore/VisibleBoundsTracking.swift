// Copyright 2019 Yandex LLC. All rights reserved.

import CoreGraphics
import UIKit

public protocol VisibleBoundsTracking {
  func onVisibleBoundsChanged(from: CGRect, to: CGRect)
}

public protocol VisibleBoundsTrackingContainer: VisibleBoundsTracking {
  var visibleBoundsTrackingSubviews: [VisibleBoundsTrackingView] { get }
}

extension VisibleBoundsTrackingContainer where Self: UICoordinateSpace {
  public func passVisibleBoundsChanged(from: CGRect, to: CGRect) {
    guard !from.isEmpty || !to.isEmpty else {
      return
    }
    visibleBoundsTrackingSubviews.forEach {
      let fromFrame = convert(from, to: $0)
      let toFrame = convert(to, to: $0)

      $0.onVisibleBoundsChanged(
        from: $0.bounds.intersection(fromFrame),
        to: $0.bounds.intersection(toFrame)
      )
    }
  }

  public func onVisibleBoundsChanged(from: CGRect, to: CGRect) {
    passVisibleBoundsChanged(from: from, to: to)
  }
}

public protocol VisibleBoundsTrackingLeaf: VisibleBoundsTracking {}

extension VisibleBoundsTrackingLeaf {
  public func onVisibleBoundsChanged(from _: CGRect, to _: CGRect) {}
}

public typealias VisibleBoundsTrackingView = VisibleBoundsTracking & UIView

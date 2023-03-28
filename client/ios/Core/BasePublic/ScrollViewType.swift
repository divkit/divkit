// Copyright 2017 Yandex LLC. All rights reserved.

import CoreGraphics

import BaseUIPublic

#if os(iOS)
import UIKit.UIScrollView
#endif

public protocol ScrollToDrag: ScrollingToTop {
  var contentInset: EdgeInsets { get set }
  var contentOffset: CGPoint { get set }
  var contentSize: CGSize { get }
  #if !os(tvOS)
  var scrollsToTop: Bool { get set }
  #endif
  var bounces: Bool { get set }

  var isTracking: Bool { get }
  var boundsSize: CGSize { get }

  func setContentOffset(_ contentOffset: CGPoint, animated: Bool)
}

public protocol ScrollViewType: ScrollToDrag, ScrollViewTrackable {
  var contentSize: CGSize { get set }
  var contentInset: EdgeInsets { get set }
  var scrollIndicatorInsets: EdgeInsets { get set }
  var showsVerticalScrollIndicator: Bool { get set }
  #if !os(tvOS)
  var scrollsToTop: Bool { get set }
  #endif
  var bounces: Bool { get set }

  #if os(iOS)
  var decelerationRate: UIScrollView.DecelerationRate { get set }
  #endif

  var contentOffset: CGPoint { get set }
  func setContentOffset(_ contentOffset: CGPoint, animated: Bool)
}

extension ScrollToDrag {
  public func scrollToTopAnimated(_ animated: Bool) {
    let offsetAtTop = CGPoint(x: contentOffset.x, y: -contentInset.top)
    setContentOffset(offsetAtTop, animated: animated)
  }

  public var isAtTopOrBouncing: Bool {
    contentOffset.y.isApproximatelyLessOrEqualThan(-contentInset.top, withAccuracy: accuracy)
  }

  public var isAtTop: Bool {
    contentOffset.y.isApproximatelyEqualTo(-contentInset.top, withAccuracy: accuracy)
  }

  public var isAtLeft: Bool {
    contentOffset.x.isApproximatelyEqualTo(-contentInset.left, withAccuracy: accuracy)
  }

  public var isAtBottom: Bool {
    (contentOffset.y + boundsSize.height).isApproximatelyEqualTo(
      contentSize.height + contentInset.bottom,
      withAccuracy: accuracy
    )
  }

  public var isAtRight: Bool {
    (contentOffset.x + boundsSize.width).isApproximatelyEqualTo(
      contentSize.width + contentInset.right,
      withAccuracy: accuracy
    )
  }
}

extension ScrollViewType {
  public var activeEdges: ScrollEdge {
    var result = ScrollEdge()
    if isAtTop { result.formUnion(.top) }
    if isAtBottom { result.formUnion(.bottom) }
    if isAtLeft { result.formUnion(.left) }
    if isAtRight { result.formUnion(.right) }

    return result
  }

  public func stopScrollingAnimation() {
    setContentOffset(contentOffset, animated: false)
  }

  public func setContentInsetPreservingScrollPosition(_ newContentInset: EdgeInsets) {
    let activeEdgesBeforeSetting = activeEdges
    contentInset = newContentInset
    scrollToEdges(activeEdgesBeforeSetting)
  }

  // top and left have priority on bottom and right
  public func scrollToEdges(_ edges: ScrollEdge) {
    guard !edges.isEmpty else { return }

    let newOffsetX: CGFloat
    let newOffsetY: CGFloat
    if edges.contains(.left) {
      newOffsetX = -contentInset.left
    } else if edges.contains(.right) {
      newOffsetX = maxContentOffsetXBeforeBounce
    } else {
      newOffsetX = contentOffset.x
    }

    if edges.contains(.top) {
      newOffsetY = -contentInset.top
    } else if edges.contains(.bottom) {
      newOffsetY = maxContentOffsetYBeforeBounce
    } else {
      newOffsetY = contentOffset.y
    }

    contentOffset = CGPoint(x: newOffsetX, y: newOffsetY)
  }

  public var maxContentOffsetXBeforeBounce: CGFloat {
    maxContentOffset(
      forContentSize: contentSize.width,
      scrollInsets: contentInset.horizontalInsets,
      bound: boundsSize.width
    )
  }

  public var maxContentOffsetYBeforeBounce: CGFloat {
    maxContentOffset(
      forContentSize: contentSize.height,
      scrollInsets: contentInset.verticalInsets,
      bound: boundsSize.height
    )
  }
}

public protocol InsetsLayoutOverriding: AnyObject {
  var contentInset: EdgeInsets { get set }

  func overrideLayoutParameters(maxInset: EdgeInsets, minInset: EdgeInsets)
}

public protocol ComplexLayoutRequiring: InsetsLayoutOverriding {
  func resizeWhileHidingContentWithUpdates(_ updates: @escaping @convention(block) () -> Void)
  func beginAnimatedResizeWithUpdates(_ updates: @escaping @convention(block) () -> Void)
  func endAnimatedResize()
}

private let accuracy: CGFloat = 1e-5

private func maxContentOffset(
  forContentSize contentSize: CGFloat,
  scrollInsets: SideInsets,
  bound: CGFloat
) -> CGFloat {
  let contentExceedsBound = contentSize > (bound - scrollInsets.sum)
  let maxContentOffset = contentExceedsBound
    ? contentSize - (bound - scrollInsets.trailing)
    : -scrollInsets.leading
  return maxContentOffset.roundedToScreenScale
}

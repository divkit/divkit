// Copyright 2018 Yandex LLC. All rights reserved.

import UIKit

public final class CompoundScrollDelegate: NSObject {
  private var delegates = WeakCollection<ScrollDelegate>()
  private let contentOffsetSignalPipe = SignalPipe<CGPoint>()

  public var contentOffsetSignal: Signal<CGPoint> {
    contentOffsetSignalPipe.signal
  }

  public func add(_ delegate: ScrollDelegate) {
    guard !contains(delegate) else {
      assertionFailure("delegate already added")
      return
    }
    delegates.append(delegate)
  }

  public func remove(_ delegate: ScrollDelegate) {
    delegates.remove(delegate)
  }

  public func contains(_ delegate: ScrollDelegate) -> Bool {
    delegates.contains(delegate)
  }
}

extension CompoundScrollDelegate: ScrollDelegate {
  public func onWillBeginDragging(_ scrollView: ScrollView) {
    delegates.forEach { $0?.onWillBeginDragging(scrollView) }
  }

  public func onWillEndDragging(
    _ scrollView: ScrollView,
    withVelocity velocity: CGPoint,
    targetContentOffset: UnsafeMutablePointer<CGPoint>
  ) {
    delegates.forEach {
      $0?.onWillEndDragging(
        scrollView,
        withVelocity: velocity,
        targetContentOffset: targetContentOffset
      )
    }
  }

  public func onDidScroll(_ scrollView: ScrollView) {
    contentOffsetSignalPipe.send(scrollView.contentOffset)
    delegates.forEach { $0?.onDidScroll(scrollView) }
  }

  public func onDidEndDragging(_ scrollView: ScrollView, willDecelerate decelerate: Bool) {
    delegates.forEach { $0?.onDidEndDragging(scrollView, willDecelerate: decelerate) }
  }

  public func onWillBeginDecelerating(_ scrollView: ScrollView) {
    delegates.forEach { $0?.onWillBeginDecelerating(scrollView) }
  }

  public func onDidEndDecelerating(_ scrollView: ScrollView) {
    delegates.forEach { $0?.onDidEndDecelerating(scrollView) }
  }

  public func onDidEndScrollingAnimation(_ scrollView: ScrollView) {
    delegates.forEach { $0?.onDidEndScrollingAnimation(scrollView) }
  }

  public func shouldStartScrollingToTop(_ scrollView: ScrollView) -> Bool? {
    let results = delegates.compactMap { $0?.shouldStartScrollingToTop(scrollView) }
    assert(results.count <= 1, "Only one value could be returned from \(#function)")
    return results.first
  }

  public func onDidEndScrollingToTop(_ scrollView: ScrollView) {
    delegates.forEach { $0?.onDidEndScrollingToTop(scrollView) }
  }
}

extension CompoundScrollDelegate: UIScrollViewDelegate {
  public func scrollViewWillBeginDragging(_ scrollView: UIScrollView) {
    onWillBeginDragging(scrollView)
  }

  public func scrollViewWillEndDragging(
    _ scrollView: UIScrollView,
    withVelocity velocity: CGPoint,
    targetContentOffset: UnsafeMutablePointer<CGPoint>
  ) {
    onWillEndDragging(scrollView, withVelocity: velocity, targetContentOffset: targetContentOffset)
  }

  public func scrollViewDidScroll(_ scrollView: UIScrollView) {
    onDidScroll(scrollView)
  }

  public func scrollViewDidEndDragging(
    _ scrollView: UIScrollView,
    willDecelerate decelerate: Bool
  ) {
    onDidEndDragging(scrollView, willDecelerate: decelerate)
  }

  public func scrollViewWillBeginDecelerating(_ scrollView: UIScrollView) {
    onWillBeginDecelerating(scrollView)
  }

  public func scrollViewDidEndDecelerating(_ scrollView: UIScrollView) {
    onDidEndDecelerating(scrollView)
  }

  public func scrollViewDidEndScrollingAnimation(_ scrollView: UIScrollView) {
    onDidEndScrollingAnimation(scrollView)
  }

  public func scrollViewShouldScrollToTop(_ scrollView: UIScrollView) -> Bool {
    shouldStartScrollingToTop(scrollView) ?? true
  }

  public func scrollViewDidScrollToTop(_ scrollView: UIScrollView) {
    onDidEndScrollingToTop(scrollView)
  }
}

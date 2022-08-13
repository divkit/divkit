// Copyright 2017 Yandex LLC. All rights reserved.

import CoreGraphics

public protocol ScrollDelegate: AnyObject {
  func onWillBeginDragging(_ scrollView: ScrollView)
  func onWillEndDragging(
    _ scrollView: ScrollView,
    withVelocity velocity: CGPoint,
    targetContentOffset: UnsafeMutablePointer<CGPoint>
  )
  func onDidScroll(_ scrollView: ScrollView)
  func onDidEndDragging(_ scrollView: ScrollView, willDecelerate decelerate: Bool)
  func onWillBeginDecelerating(_ scrollView: ScrollView)
  func onDidEndDecelerating(_ scrollView: ScrollView)
  func onDidEndScrollingAnimation(_ scrollView: ScrollView)
  func shouldStartScrollingToTop(_ scrollView: ScrollView) -> Bool?
  func onDidEndScrollingToTop(_ scrollView: ScrollView)
}

extension ScrollDelegate {
  public func onWillBeginDragging(_: ScrollView) {}
  public func onWillEndDragging(
    _: ScrollView,
    withVelocity _: CGPoint,
    targetContentOffset _: UnsafeMutablePointer<CGPoint>
  ) {}
  public func onDidScroll(_: ScrollView) {}
  public func onDidEndDragging(_: ScrollView, willDecelerate _: Bool) {}
  public func onWillBeginDecelerating(_: ScrollView) {}
  public func onDidEndDecelerating(_: ScrollView) {}
  public func onDidEndScrollingAnimation(_: ScrollView) {}
  public func shouldStartScrollingToTop(_: ScrollView) -> Bool? { nil }
  public func onDidEndScrollingToTop(_: ScrollView) {}
}

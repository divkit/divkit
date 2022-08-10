// Copyright 2019 Yandex LLC. All rights reserved.

import UIKit

public protocol ScrollableContent: AnyObject {
  var scrollDelegate: ScrollDelegate? { get set }
  var scrollView: ScrollView { get }
}

public protocol ScrollToDragContent: AnyObject {
  var scrollDelegate: ScrollDelegate? { get set }
  var scrollToDragView: ScrollToDragView { get }
  var scrollEnabled: Bool { get set }
  func reattachPanGestureRecognizer(toView: UIView)
  func restorePanGestureRecognizer()
  func interruptPanGesture()
  func checkPanGestureRecognizerEquals(to gestureRecognizer: UIGestureRecognizer) -> Bool
}

extension UIScrollView {
  public func makeScrollableContent() -> ScrollableContent & ScrollToDragContent {
    ScrollViewWrapper(scrollView: self)
  }
}

private final class ScrollViewWrapper: ScrollableContent, ScrollToDragContent {
  private let subject: UIScrollView
  private let adapter = CompoundScrollDelegate()

  init(scrollView: UIScrollView) {
    subject = scrollView
    scrollView.delegate = adapter
  }

  weak var scrollDelegate: ScrollDelegate? {
    didSet {
      guard scrollDelegate !== oldValue else { return }
      if let oldValue = oldValue {
        adapter.remove(oldValue)
      }
      if let delegate = scrollDelegate {
        adapter.add(delegate)
      }
    }
  }

  var scrollView: ScrollView {
    subject
  }

  var scrollToDragView: ScrollToDragView {
    subject
  }

  var scrollEnabled: Bool {
    get { subject.isScrollEnabled }
    set { subject.isScrollEnabled = newValue }
  }

  func reattachPanGestureRecognizer(toView view: UIView) {
    view.addGestureRecognizer(subject.panGestureRecognizer)
  }

  func restorePanGestureRecognizer() {
    subject.addGestureRecognizer(subject.panGestureRecognizer)
  }

  func interruptPanGesture() {
    subject.panGestureRecognizer.isEnabled = false
    subject.panGestureRecognizer.isEnabled = true
  }

  func checkPanGestureRecognizerEquals(to gestureRecognizer: UIGestureRecognizer) -> Bool {
    subject.panGestureRecognizer === gestureRecognizer
  }
}

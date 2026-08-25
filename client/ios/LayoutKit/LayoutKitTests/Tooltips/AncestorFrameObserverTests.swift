#if os(iOS)
import Foundation
@testable import LayoutKit
import Testing
import UIKit

@Suite
@MainActor
struct AncestorFrameObserverTests {
  @Test
  func superviewFrameChange_notifies() {
    let hierarchy = ViewHierarchy()
    let counter = ChangesCounter()
    let observer = AncestorFrameObserver(view: hierarchy.leaf) { counter.value += 1 }

    withExtendedLifetime(observer) {
      hierarchy.middle.frame = hierarchy.middle.frame.offsetBy(dx: 10, dy: 20)
    }

    #expect(counter.value > 0)
  }

  @Test
  func intermediateAncestorCenterChange_notifies() {
    let hierarchy = ViewHierarchy()
    let counter = ChangesCounter()
    let observer = AncestorFrameObserver(view: hierarchy.leaf) { counter.value += 1 }

    withExtendedLifetime(observer) {
      hierarchy.root.center = CGPoint(x: 100, y: 200)
    }

    #expect(counter.value > 0)
  }

  @Test
  func observedViewBoundsChange_notifies() {
    let hierarchy = ViewHierarchy()
    let counter = ChangesCounter()
    let observer = AncestorFrameObserver(view: hierarchy.leaf) { counter.value += 1 }

    withExtendedLifetime(observer) {
      hierarchy.leaf.bounds = CGRect(x: 0, y: 0, width: 30, height: 30)
    }

    #expect(counter.value > 0)
  }

  @Test
  func enclosingScrollViewContentOffsetChange_notifies() {
    let scrollView = UIScrollView(frame: CGRect(x: 0, y: 0, width: 100, height: 100))
    let content = UIView(frame: CGRect(x: 0, y: 0, width: 100, height: 500))
    let leaf = UIView(frame: CGRect(x: 0, y: 0, width: 50, height: 50))
    scrollView.addSubview(content)
    content.addSubview(leaf)

    let counter = ChangesCounter()
    let observer = AncestorFrameObserver(view: leaf) { counter.value += 1 }

    withExtendedLifetime(observer) {
      scrollView.contentOffset = CGPoint(x: 0, y: 40)
    }

    #expect(counter.value > 0)
  }

  @Test
  func viewOutsideAncestorChain_doesNotNotify() {
    let hierarchy = ViewHierarchy()
    let sibling = UIView(frame: CGRect(x: 0, y: 0, width: 10, height: 10))
    hierarchy.root.addSubview(sibling)

    let counter = ChangesCounter()
    let observer = AncestorFrameObserver(view: hierarchy.leaf) { counter.value += 1 }

    withExtendedLifetime(observer) {
      sibling.frame = sibling.frame.offsetBy(dx: 10, dy: 10)
    }

    #expect(counter.value == 0)
  }

  @Test
  func invalidatedObserver_doesNotNotify() {
    let hierarchy = ViewHierarchy()
    let counter = ChangesCounter()
    let observer = AncestorFrameObserver(view: hierarchy.leaf) { counter.value += 1 }

    withExtendedLifetime(observer) {
      observer.invalidate()
      hierarchy.middle.frame = hierarchy.middle.frame.offsetBy(dx: 10, dy: 20)
    }

    #expect(counter.value == 0)
  }

  @Test
  func deallocatedObserver_doesNotNotify() {
    let hierarchy = ViewHierarchy()
    let counter = ChangesCounter()
    var observer: AncestorFrameObserver? = AncestorFrameObserver(view: hierarchy.leaf) {
      counter.value += 1
    }
    #expect(observer != nil)

    observer = nil
    hierarchy.middle.frame = hierarchy.middle.frame.offsetBy(dx: 10, dy: 20)

    #expect(counter.value == 0)
  }
}

private final class ChangesCounter {
  var value = 0
}

@MainActor
private struct ViewHierarchy {
  let root = UIView(frame: CGRect(x: 0, y: 0, width: 200, height: 200))
  let middle = UIView(frame: CGRect(x: 10, y: 10, width: 100, height: 100))
  let leaf = UIView(frame: CGRect(x: 0, y: 0, width: 50, height: 50))

  init() {
    root.addSubview(middle)
    middle.addSubview(leaf)
  }
}
#endif

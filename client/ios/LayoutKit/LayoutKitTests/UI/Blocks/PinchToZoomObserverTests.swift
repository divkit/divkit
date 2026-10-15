@testable import LayoutKit
import UIKit
import XCTest

@MainActor
final class PinchToZoomObserverTests: XCTestCase {
  func test_originalInitializerRemainsUsableAsFunction() {
    let factory: (Block, UIView) -> PinchToZoomBlock = PinchToZoomBlock.init(child:overlayView:)
    let block = factory(EmptyBlock.zeroSized, UIView())

    XCTAssertNil(block.zoomObserver)
  }

  func test_copyPreservesObserver() {
    let observer = ZoomObserver()
    let block = PinchToZoomBlock(
      child: EmptyBlock.zeroSized,
      overlayView: UIView(),
      zoomObserver: observer
    )

    XCTAssertIdentical(block.makeCopy(wrapping: EmptyBlock.zeroSized).zoomObserver, observer)
  }

  func test_equalityIncludesObserverIdentity() {
    let firstObserver = ZoomObserver()
    let secondObserver = ZoomObserver()
    let overlayView = UIView()
    let first = PinchToZoomBlock(
      child: EmptyBlock.zeroSized, overlayView: overlayView, zoomObserver: firstObserver
    )
    let same = PinchToZoomBlock(
      child: EmptyBlock.zeroSized, overlayView: overlayView, zoomObserver: firstObserver
    )
    let different = PinchToZoomBlock(
      child: EmptyBlock.zeroSized, overlayView: overlayView, zoomObserver: secondObserver
    )

    XCTAssertEqual(first, same)
    XCTAssertNotEqual(first, different)
  }

  func test_blockAndViewDoNotRetainObserver() {
    var observer: ZoomObserver? = ZoomObserver()
    let overlayView = UIView()
    let block = PinchToZoomBlock(
      child: EmptyBlock.zeroSized, overlayView: overlayView, zoomObserver: observer
    )
    let view = block.makeBlockView()
    block.configureBlockView(view)

    observer = nil

    XCTAssertNil(block.zoomObserver)
  }

  func test_reportsAppliedScaleAndIgnoresUnchangedScale() {
    let observer = ZoomObserver()
    let overlayView = UIView()
    let view = makeView(overlayView: overlayView, observer: observer)
    pinch(view, state: .began, scale: 0.5)
    pinch(view, state: .changed, scale: 8)
    pinch(view, state: .changed, scale: 9)

    XCTAssertEqual(observer.events, [.began(1), .changed(4)])
    XCTAssertEqual(observer.contentView?.transform.a, 4)
    XCTAssertFalse(observer.contentView === view)
  }

  func test_endIsReportedAfterReturnAnimation() {
    let observer = ZoomObserver()
    let overlayView = UIView()
    let view = makeView(overlayView: overlayView, observer: observer)
    let ended = expectation(description: "Return animation completed")
    observer.onEnd = { ended.fulfill() }
    pinch(view, state: .began, scale: 2)
    pinch(view, state: .ended, scale: 2)

    XCTAssertEqual(observer.events, [.began(2)])
    wait(for: [ended], timeout: 2)
    XCTAssertEqual(observer.events, [.began(2), .ended])
    XCTAssertTrue(observer.contentView?.superview === view)
  }

  func test_reconfigurationEndsOldSessionExactlyOnce() {
    let oldObserver = ZoomObserver()
    let newObserver = ZoomObserver()
    let overlayView = UIView()
    let view = makeView(overlayView: overlayView, observer: oldObserver)
    pinch(view, state: .began, scale: 2)
    pinch(view, state: .ended, scale: 2)

    PinchToZoomBlock(
      child: EmptyBlock.zeroSized, overlayView: overlayView, zoomObserver: newObserver
    ).configureBlockView(view)
    pinch(view, state: .changed, scale: 3)

    XCTAssertEqual(oldObserver.events, [.began(2), .ended])
    XCTAssertTrue(newObserver.events.isEmpty)
  }

  func test_newGestureDuringReturnIsNotEndedByOldCompletion() {
    let observer = ZoomObserver()
    let overlayView = UIView()
    let view = makeView(overlayView: overlayView, observer: observer)
    pinch(view, state: .began, scale: 2)
    pinch(view, state: .ended, scale: 2)
    pinch(view, state: .began, scale: 3)
    let elapsed = expectation(description: "Old completion passed")
    DispatchQueue.main.asyncAfter(deadline: .now() + 0.4) { elapsed.fulfill() }

    wait(for: [elapsed], timeout: 2)

    XCTAssertEqual(observer.events, [.began(2), .ended, .began(3)])
    XCTAssertEqual(observer.contentView?.transform.a, 3)
    XCTAssertFalse(observer.contentView?.superview === view)
  }

  func test_detachingEndsSession() {
    let observer = ZoomObserver()
    let overlayView = UIView()
    let view = makeView(overlayView: overlayView, observer: observer)
    let window = UIWindow(frame: CGRect(x: 0, y: 0, width: 100, height: 100))
    window.addSubview(view)
    pinch(view, state: .began, scale: 2)

    view.removeFromSuperview()
    view.removeFromSuperview()

    XCTAssertEqual(observer.events, [.began(2), .ended])
  }

  func test_newGestureDuringReturnStopsIfEndCallbackDetachesWrapper() throws {
    let observer = ZoomObserver()
    let overlayView = UIView()
    let view = makeView(overlayView: overlayView, observer: observer)
    let window = UIWindow(frame: view.bounds)
    window.addSubview(view)
    let contentView = try XCTUnwrap(view.subviews.first)
    observer.onEnd = {
      observer.onEnd = nil
      view.removeFromSuperview()
    }
    pinch(view, state: .began, scale: 2)
    pinch(view, state: .ended, scale: 2)

    pinch(view, state: .began, scale: 3)
    pinch(view, state: .changed, scale: 4)
    pinch(view, state: .ended, scale: 4)

    XCTAssertEqual(observer.events, [.began(2), .ended])
    XCTAssertNil(view.superview)
    XCTAssertTrue(window.subviews.isEmpty)
    XCTAssertTrue(contentView.superview === view)
    XCTAssertEqual(contentView.transform, .identity)
    XCTAssertTrue(overlayView.subviews.isEmpty)
  }

  func test_newGestureDuringReturnStopsIfEndCallbackReconfiguresWrapper() {
    let oldObserver = ZoomObserver()
    let newObserver = ZoomObserver()
    let overlayView = UIView()
    let view = makeView(overlayView: overlayView, observer: oldObserver)
    let recognizer = TestPinchGestureRecognizer()
    oldObserver.onEnd = {
      oldObserver.onEnd = nil
      PinchToZoomBlock(
        child: EmptyBlock.zeroSized, overlayView: overlayView, zoomObserver: newObserver
      ).configureBlockView(view)
    }
    pinch(view, state: .began, scale: 2, recognizer: recognizer)
    pinch(view, state: .ended, scale: 2, recognizer: recognizer)

    pinch(view, state: .began, scale: 3, recognizer: recognizer)
    pinch(view, state: .changed, scale: 4, recognizer: recognizer)

    XCTAssertEqual(oldObserver.events, [.began(2), .ended])
    XCTAssertTrue(newObserver.events.isEmpty)
    XCTAssertTrue(overlayView.subviews.isEmpty)
    pinch(view, state: .began, scale: 2)
    XCTAssertEqual(newObserver.events, [.began(2)])
    PinchToZoomBlock(child: EmptyBlock.zeroSized, overlayView: overlayView).configureBlockView(view)
    XCTAssertEqual(newObserver.events, [.began(2), .ended])
    XCTAssertEqual(oldObserver.events, [.began(2), .ended])
  }

  func test_outerReconfigurationDoesNotOverwriteReentrantContentOrObserver() throws {
    let oldObserver = ZoomObserver()
    let outerObserver = ZoomObserver()
    let innerObserver = ZoomObserver()
    let overlayView = UIView()
    let view = makeView(overlayView: overlayView, observer: oldObserver)
    var innerContentView: UIView?
    oldObserver.onEnd = {
      oldObserver.onEnd = nil
      PinchToZoomBlock(
        child: PinchToZoomBlock(child: EmptyBlock.zeroSized, overlayView: overlayView),
        overlayView: overlayView,
        zoomObserver: innerObserver
      ).configureBlockView(view)
      innerContentView = view.subviews.first
    }
    pinch(view, state: .began, scale: 2)
    pinch(view, state: .ended, scale: 2)

    PinchToZoomBlock(
      child: EmptyBlock.zeroSized, overlayView: overlayView, zoomObserver: outerObserver
    ).configureBlockView(view)

    let contentView = try XCTUnwrap(innerContentView)
    XCTAssertTrue(view.subviews.first === contentView)
    XCTAssertEqual(contentView.gestureRecognizers?.count, 2)
    XCTAssertEqual(oldObserver.events, [.began(2), .ended])
    XCTAssertTrue(outerObserver.events.isEmpty)
    pinch(view, state: .began, scale: 3)
    XCTAssertEqual(innerObserver.events, [.began(3)])
    XCTAssertTrue(outerObserver.events.isEmpty)
    PinchToZoomBlock(child: EmptyBlock.zeroSized, overlayView: overlayView).configureBlockView(view)
    XCTAssertEqual(innerObserver.events, [.began(3), .ended])
    XCTAssertEqual(oldObserver.events, [.began(2), .ended])
  }

  func test_missingOverlayEndsSession() {
    let observer = ZoomObserver()
    let view = autoreleasepool {
      let overlayView = UIView()
      let view = makeView(overlayView: overlayView, observer: observer)
      pinch(view, state: .began, scale: 2)
      return view
    }
    view.setNeedsLayout()
    view.layoutIfNeeded()

    XCTAssertEqual(observer.events, [.began(2), .ended])
    XCTAssertTrue(observer.contentView?.superview === view)
  }

  func test_cancelledAndFailedGesturesEndExactlyOnce() {
    for state: UIGestureRecognizer.State in [.cancelled, .failed] {
      let observer = ZoomObserver()
      let overlayView = UIView()
      let view = makeView(overlayView: overlayView, observer: observer)
      let ended = expectation(description: "Cancelled return completed")
      observer.onEnd = { ended.fulfill() }
      pinch(view, state: .began, scale: 2)
      pinch(view, state: state, scale: 2)
      pinch(view, state: state, scale: 2)

      wait(for: [ended], timeout: 2)
      XCTAssertEqual(observer.events, [.began(2), .ended])
    }
  }

  func test_withoutObserverPreservesZoomTransform() throws {
    let overlayView = UIView()
    let block = PinchToZoomBlock(child: EmptyBlock.zeroSized, overlayView: overlayView)
    let view = block.makeBlockView()
    block.configureBlockView(view)
    let contentView = try XCTUnwrap(view.subviews.first)

    pinch(view, state: .began, scale: 2)

    XCTAssertEqual(contentView.transform.a, 2)
    XCTAssertTrue(contentView.superview?.superview === overlayView)
  }

  func test_endingZoomPreservesUnrelatedChildAnimation() throws {
    let observer = ZoomObserver()
    let overlayView = UIView()
    let view = makeView(overlayView: overlayView, observer: observer)
    let contentView = try XCTUnwrap(view.subviews.first)
    let animation = CABasicAnimation(keyPath: "opacity")
    animation.fromValue = 1
    animation.toValue = 0.5
    animation.duration = 30
    contentView.layer.add(animation, forKey: "unrelated")
    pinch(view, state: .began, scale: 2)
    pinch(view, state: .ended, scale: 2)
    PinchToZoomBlock(child: EmptyBlock.zeroSized, overlayView: overlayView)
      .configureBlockView(view)

    XCTAssertNotNil(contentView.layer.animation(forKey: "unrelated"))
  }

  func test_independentViewsDoNotShareZoomState() {
    let firstObserver = ZoomObserver()
    let secondObserver = ZoomObserver()
    let overlayView = UIView()
    let first = makeView(overlayView: overlayView, observer: firstObserver)
    let second = makeView(overlayView: overlayView, observer: secondObserver)

    pinch(first, state: .began, scale: 2)

    XCTAssertEqual(firstObserver.events, [.began(2)])
    XCTAssertTrue(secondObserver.events.isEmpty)
    XCTAssertFalse(firstObserver.contentView === second.subviews.first)
  }

  func test_interruptedPanDoesNotOffsetNextZoom() throws {
    for terminalState: UIGestureRecognizer.State in [.ended, .cancelled, .failed] {
      let observer = ZoomObserver()
      let overlayView = UIView()
      let view = makeView(overlayView: overlayView, observer: observer)
      pinch(view, state: .began, scale: 2)
      PinchToZoomBlock(
        child: EmptyBlock.zeroSized, overlayView: overlayView, zoomObserver: observer
      ).configureBlockView(view)

      pan(view, state: .changed, translation: CGPoint(x: 80, y: 40))
      pinch(view, state: .changed, scale: 3)
      pinch(view, state: terminalState, scale: 3)
      pinch(view, state: .began, scale: 2)

      let contentView = try XCTUnwrap(observer.contentView)
      XCTAssertEqual(contentView.transform.tx, 0)
      XCTAssertEqual(contentView.transform.ty, 0)
      XCTAssertEqual(observer.events, [.began(2), .ended, .began(2)])
    }
  }

  func test_newZoomAfterInterruptionDoesNotRequireTerminalEvent() throws {
    let observer = ZoomObserver()
    let overlayView = UIView()
    let view = makeView(overlayView: overlayView, observer: observer)
    pinch(view, state: .began, scale: 2)
    PinchToZoomBlock(
      child: EmptyBlock.zeroSized, overlayView: overlayView, zoomObserver: observer
    ).configureBlockView(view)
    pan(view, state: .changed, translation: CGPoint(x: 80, y: 40))

    pinch(view, state: .began, scale: 3)

    let contentView = try XCTUnwrap(observer.contentView)
    XCTAssertEqual(contentView.transform, CGAffineTransform(scaleX: 3, y: 3))
    XCTAssertEqual(observer.events, [.began(2), .ended, .began(3)])
  }

  func test_panDuringReturnDoesNotChangeTransform() throws {
    let observer = ZoomObserver()
    let overlayView = UIView()
    let view = makeView(overlayView: overlayView, observer: observer)
    pinch(view, state: .began, scale: 2)
    pinch(view, state: .ended, scale: 2)
    let contentView = try XCTUnwrap(observer.contentView)
    let returnTransform = contentView.transform

    pan(view, state: .changed, translation: CGPoint(x: 80, y: 40))

    XCTAssertEqual(contentView.transform, returnTransform)
  }

  func test_panBeforePinchIgnoresOnlyInitialTranslation() throws {
    let observer = ZoomObserver()
    let overlayView = UIView()
    let view = makeView(overlayView: overlayView, observer: observer)
    pan(view, state: .began, translation: CGPoint(x: 80, y: 40))
    pinch(view, state: .began, scale: 2)
    let contentView = try XCTUnwrap(observer.contentView)
    XCTAssertEqual(contentView.transform.tx, 0)
    XCTAssertEqual(contentView.transform.ty, 0)

    pan(view, state: .changed, translation: CGPoint(x: 90, y: 60))

    XCTAssertEqual(contentView.transform.tx, 10)
    XCTAssertEqual(contentView.transform.ty, 20)
  }

  func test_panWithoutPinchDoesNotOffsetNextZoom() throws {
    for terminalState: UIGestureRecognizer.State in [.ended, .cancelled, .failed] {
      let observer = ZoomObserver()
      let overlayView = UIView()
      let view = makeView(overlayView: overlayView, observer: observer)
      pan(view, state: .began, translation: CGPoint(x: 80, y: 40))
      pan(view, state: terminalState, translation: CGPoint(x: 80, y: 40))

      pinch(view, state: .began, scale: 2)

      let contentView = try XCTUnwrap(observer.contentView)
      XCTAssertEqual(contentView.transform.tx, 0)
      XCTAssertEqual(contentView.transform.ty, 0)
    }
  }

  func test_childEndCallbackReconfigurationPreservesNewContentAndObserver() throws {
    let innerObserver = ZoomObserver()
    let outerObserver = ZoomObserver()
    let replacementObserver = ZoomObserver()
    let overlayView = UIView()
    let block = PinchToZoomBlock(
      child: PinchToZoomBlock(
        child: EmptyBlock.zeroSized, overlayView: overlayView, zoomObserver: innerObserver
      ),
      overlayView: overlayView,
      zoomObserver: outerObserver
    )
    let view = block.makeBlockView()
    block.configureBlockView(view)
    let innerView = try XCTUnwrap(view.subviews.first as? BlockView)
    var replacementView: UIView?
    innerObserver.onEnd = {
      innerObserver.onEnd = nil
      PinchToZoomBlock(
        child: EmptyBlock.zeroSized, overlayView: overlayView, zoomObserver: replacementObserver
      ).configureBlockView(view)
      replacementView = view.subviews.first
    }
    pinch(innerView, state: .began, scale: 2)

    block.configureBlockView(view)

    XCTAssertEqual(view.subviews.count, 1)
    XCTAssertTrue(view.subviews.first === replacementView)
    XCTAssertNil(innerView.superview)
    pinch(view, state: .began, scale: 3)
    XCTAssertEqual(innerObserver.events, [.began(2), .ended])
    XCTAssertTrue(outerObserver.events.isEmpty)
    XCTAssertEqual(replacementObserver.events, [.began(3)])
  }

  private func pan(
    _ view: BlockView,
    state: UIGestureRecognizer.State,
    translation: CGPoint
  ) {
    let recognizer = TestPanGestureRecognizer()
    recognizer.simulatedState = state
    recognizer.simulatedTranslation = translation
    view.perform(NSSelectorFromString("handlePan:"), with: recognizer)
  }

  private func makeView(overlayView: UIView, observer: ZoomObserver) -> BlockView {
    let block = PinchToZoomBlock(
      child: EmptyBlock.zeroSized, overlayView: overlayView, zoomObserver: observer
    )
    let view = block.makeBlockView()
    block.configureBlockView(view)
    view.frame = CGRect(x: 0, y: 0, width: 100, height: 100)
    view.layoutIfNeeded()
    return view
  }

  private func pinch(
    _ view: BlockView,
    state: UIGestureRecognizer.State,
    scale: CGFloat,
    recognizer: TestPinchGestureRecognizer? = nil
  ) {
    let recognizer = recognizer ?? TestPinchGestureRecognizer()
    recognizer.simulatedState = state
    recognizer.scale = scale
    view.perform(NSSelectorFromString("handlePinch:"), with: recognizer)
  }
}

@MainActor
private final class ZoomObserver: PinchToZoomObserver {
  enum Event: Equatable {
    case began(CGFloat)
    case changed(CGFloat)
    case ended
  }

  private(set) var events: [Event] = []
  private(set) weak var contentView: BlockView?
  var onEnd: (() -> Void)?

  func pinchToZoomDidBegin(in view: BlockView, scale: CGFloat) {
    contentView = view
    events.append(.began(scale))
  }

  func pinchToZoomDidChange(in view: BlockView, scale: CGFloat) {
    contentView = view
    events.append(.changed(scale))
  }

  func pinchToZoomDidEnd(in view: BlockView) {
    contentView = view
    events.append(.ended)
    onEnd?()
  }
}

private final class TestPinchGestureRecognizer: UIPinchGestureRecognizer {
  override var state: UIGestureRecognizer.State {
    get { simulatedState }
    set { simulatedState = newValue }
  }

  var simulatedState: UIGestureRecognizer.State = .possible

  override func location(in view: UIView?) -> CGPoint {
    guard let view else { return .zero }
    return CGPoint(x: view.bounds.midX, y: view.bounds.midY)
  }
}

private final class TestPanGestureRecognizer: UIPanGestureRecognizer {
  override var state: UIGestureRecognizer.State {
    get { simulatedState }
    set { simulatedState = newValue }
  }

  var simulatedState: UIGestureRecognizer.State = .possible
  var simulatedTranslation = CGPoint.zero

  override func translation(in _: UIView?) -> CGPoint {
    simulatedTranslation
  }
}

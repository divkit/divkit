#if os(iOS)
@testable import LayoutKit
import Testing
import UIKit
import VGSL

@MainActor
@Suite
struct DecoratingViewVisibilityTests {
  @Test
  func visibilityLayoutPending_tracksLayoutRequests() {
    let view = makeView(DecoratingBlock(child: EmptyBlock.zeroSized))

    #expect(view.isPendingLayout)
    view.layer.layoutIfNeeded()
    #expect(!view.isPendingLayout)

    view.setNeedsLayout()
    #expect(view.isPendingLayout)
    view.layer.layoutIfNeeded()
    #expect(!view.isPendingLayout)
  }

  @Test
  func visibilityLayoutPending_tracksDirectLayerRequests() {
    let view = makeView(DecoratingBlock(child: EmptyBlock.zeroSized))
    view.layer.layoutIfNeeded()
    #expect(!view.isPendingLayout)

    view.layer.setNeedsLayout()
    #expect(view.isPendingLayout)
    view.layer.layoutIfNeeded()
    #expect(!view.isPendingLayout)
  }

  @Test
  func visibilityLayoutPending_tracksGeometryChanges() {
    let view = makeView(DecoratingBlock(child: EmptyBlock.zeroSized))
    view.layer.layoutIfNeeded()

    view.bounds.size = CGSize(width: 80, height: 60)
    #expect(view.isPendingLayout)
    view.layer.layoutIfNeeded()
    #expect(!view.isPendingLayout)

    view.frame = CGRect(x: 10, y: 20, width: 100, height: 70)
    #expect(view.isPendingLayout)
    view.layer.layoutIfNeeded()
    #expect(!view.isPendingLayout)
  }

  @Test(.enabled("Requires the OS to clear the child's flag before parent layout") {
    await clearsChildLayoutFlagBeforeParentLayout()
  })
  func visibilityDelivery_waitsForLayoutAfterSystemFlagWasCleared() async throws {
    var lastVisibleArea = 0
    let block = DecoratingBlock(
      child: EmptyBlock.zeroSized,
      visibilityParams: VisibilityParams(
        actions: [],
        isVisible: true,
        lastVisibleArea: Property(
          getter: { lastVisibleArea },
          setter: { lastVisibleArea = $0 }
        ),
        scheduler: TestTimerScheduler()
      )
    )
    let view = block.makeBlockView()
    block.configureBlockView(view)
    let root = VisibilityRoot(frame: CGRect(x: 0, y: 0, width: 100, height: 100))
    root.addSubview(view)

    var encounteredClearedSystemFlag = false
    root.onLayout = {
      // On affected iOS versions CA clears the descendant's flag before
      // invoking its parent's layout delegate, while the child is still unlaid out.
      guard !view.layer.needsLayout(), view.bounds.isEmpty else { return }
      encounteredClearedSystemFlag = true
      root.passVisibleBoundsChanged(from: .zero, to: root.bounds)
      view.frame = root.bounds
    }
    root.setNeedsLayout()
    root.layer.layoutIfNeeded()
    root.onLayout = nil

    try #require(encounteredClearedSystemFlag)
    #expect(lastVisibleArea == 0)

    await withCheckedContinuation { (continuation: CheckedContinuation<Void, Never>) in
      DispatchQueue.main.async { continuation.resume() }
    }
    #expect(lastVisibleArea == 100)
  }

  private func makeView(_ block: DecoratingBlock) -> BlockView {
    let view = block.makeBlockView()
    view.frame = CGRect(x: 0, y: 0, width: 50, height: 50)
    block.configureBlockView(view)
    return view
  }
}

// Swift Testing evaluates this condition before running the regression test,
// reporting a skipped test on systems that do not expose the affected ordering.
@MainActor
private func clearsChildLayoutFlagBeforeParentLayout() -> Bool {
  let block = DecoratingBlock(child: EmptyBlock.zeroSized)
  let view = block.makeBlockView()
  block.configureBlockView(view)
  let root = VisibilityRoot(frame: CGRect(x: 0, y: 0, width: 100, height: 100))
  root.addSubview(view)

  var encounteredClearedSystemFlag = false
  root.onLayout = {
    encounteredClearedSystemFlag = !view.layer.needsLayout() && view.bounds.isEmpty
  }
  root.setNeedsLayout()
  root.layer.layoutIfNeeded()
  root.onLayout = nil
  return encounteredClearedSystemFlag
}

@MainActor
private final class VisibilityRoot: UIView, @preconcurrency VisibleBoundsTrackingContainer {
  var onLayout: (() -> Void)?

  var visibleBoundsTrackingSubviews: [VisibleBoundsTrackingView] {
    subviews.compactMap { $0 as? VisibleBoundsTrackingView }
  }

  override func layoutSubviews() {
    super.layoutSubviews()
    let callback = onLayout
    onLayout = nil
    callback?()
  }
}
#endif

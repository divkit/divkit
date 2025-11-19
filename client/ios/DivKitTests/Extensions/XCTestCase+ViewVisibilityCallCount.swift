import LayoutKit
import VGSL
import XCTest

extension XCTestCase {
  @MainActor
  func getViewVisibilityCallCount(
    view: BlockView,
    rect: CGRect,
    visibilityRect: CGRect? = nil,
    timerScheduler: TestTimerScheduler
  ) async throws -> Int {
    let performer = UIActionEventPerformerMock()

    performer.addSubview(view)
    try await changeViewVisibility(
      view: view,
      rect: rect,
      visibilityRect: visibilityRect,
      timerScheduler: timerScheduler
    )

    return performer.callCount
  }
}

final class UIActionEventPerformerMock: UIView, UIActionEventPerforming {
  var callCount: Int = 0
  var receivedEvents: [UIActionEvent] = []

  func perform(uiActionEvent: UIActionEvent, from _: AnyObject) {
    callCount += 1
    receivedEvents.append(uiActionEvent)
  }
}

final class VisibilityTester {
  let view: BlockView
  let timer: TestTimerScheduler
  let performer = UIActionEventPerformerMock()

  var callsCount: Int { performer.callCount }

  init(block: Block, timer: TestTimerScheduler) {
    self.view = block.makeBlockView()
    self.timer = timer
    performer.addSubview(view)
  }

  func setViewVisibleAndDisappear(repeatCount: Int) async throws {
    for _ in 0..<repeatCount {
      try await setViewAppear()
      try await setViewDisappear()
    }
  }

  func setViewAppear() async throws {
    try await updateViewVisibility(isVisible: true)
  }

  func setViewDisappear() async throws {
    try await updateViewVisibility(isVisible: false)
  }

  func callsCount(type: VisibilityActionType, url: URL? = nil) -> Int {
    performer.receivedEvents.filter {
      $0.type == type && (url == nil || $0.payload.divActionParams?.url == url)
    }.count
  }

  private func updateViewVisibility(isVisible: Bool) async throws {
    let rect = CGRect(origin: .zero, size: CGSize(squareDimension: 20))

    try await changeViewVisibility(
      view: view,
      rect: rect,
      visibilityRect: isVisible ? rect : .zero,
      timerScheduler: timer
    )
  }
}

extension UIActionEvent {
  fileprivate var type: VisibilityActionType {
    VisibilityActionType(
      rawValue: uiAction.path.parent?.leaf ?? ""
    )!
  }
}

@MainActor
func changeViewVisibility(
  view: BlockView,
  rect: CGRect,
  visibilityRect: CGRect? = nil,
  timerScheduler: TestTimerScheduler
) async throws {
  view.frame = rect
  view.layoutIfNeeded()
  view.onVisibleBoundsChanged(from: rect, to: visibilityRect ?? rect)

  await skipMainRunLoopCycles(view.visibilityHierarchyDepth())

  for timer in timerScheduler.timers {
    guard timer.isValid else { continue }
    timer.fire()
  }
}

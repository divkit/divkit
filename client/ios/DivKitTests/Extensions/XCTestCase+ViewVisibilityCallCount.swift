import LayoutKit
import XCTest

extension XCTestCase {
  func getViewVisibilityCallCount(
    view: BlockView,
    rect: CGRect,
    visibilityRect: CGRect? = nil,
    timerScheduler: TestTimerScheduler
  ) -> Int {
    let performer = UIActionEventPerformerMock()

    performer.addSubview(view)
    changeViewVisibility(
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

  init(
    block: Block,
    timer: TestTimerScheduler
  ) {
    self.view = block.makeBlockView()
    self.timer = timer
    performer.addSubview(view)
  }

  var callsCount: Int {
    performer.callCount
  }

  func setViewAppear() {
    updateViewVisibility(isVisible: true)
  }

  func setViewDisappear() {
    updateViewVisibility(isVisible: false)
  }

  func setViewVisibleAndDissappear(repeatCount: Int) {
    for _ in 0..<repeatCount {
      setViewAppear()
      setViewDisappear()
    }
  }

  func callsCount(type: VisibilityActionType, url: URL? = nil) -> Int {
    performer.receivedEvents.filter {
      $0.type == type && (url == nil || $0.payload.divActionParams?.url == url)
    }.count
  }

  private func updateViewVisibility(isVisible: Bool) {
    let rect = CGRect(origin: .zero, size: CGSize(squareDimension: 20))
    changeViewVisibility(
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

private func changeViewVisibility(
  view: BlockView,
  rect: CGRect,
  visibilityRect: CGRect? = nil,
  timerScheduler: TestTimerScheduler
) {
  view.frame = rect
  view.layoutIfNeeded()
  view.onVisibleBoundsChanged(from: rect, to: visibilityRect ?? rect)
  timerScheduler.timers.forEach { guard $0.isValid else { return }; $0.fire() }
}

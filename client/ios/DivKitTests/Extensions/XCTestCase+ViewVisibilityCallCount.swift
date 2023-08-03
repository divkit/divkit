import LayoutKit
import XCTest

extension XCTestCase {
  func getViewVisibilityCallCount(
    view: BlockView,
    rect: CGRect,
    timerScheduler: TestTimerScheduler
  ) -> Int {
    let performer = UIActionEventPerformerMock()

    performer.addSubview(view)
    view.frame = rect
    view.layoutIfNeeded()
    view.onVisibleBoundsChanged(from: rect, to: rect)
    timerScheduler.timers.forEach { guard $0.isValid else { return }; $0.fire() }

    return performer.callCount
  }
}

private class UIActionEventPerformerMock: UIView, UIActionEventPerforming {
  var callCount: Int = 0

  func perform(uiActionEvent _: UIActionEvent, from _: AnyObject) {
    callCount += 1
  }
}

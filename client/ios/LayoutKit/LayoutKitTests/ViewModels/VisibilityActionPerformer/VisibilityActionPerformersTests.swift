@testable import LayoutKit
import UIKit
import VGSL
import XCTest

final class VisibilityActionPerformerTests: XCTestCase {
  private let view = TestView()
  private let scheduler = TestTimerScheduler()

  private lazy var actionPerformers: VisibilityActionPerformers! = {
    let actionView = UIView()
    view.addSubview(actionView)
    return VisibilityActionPerformers(
      visibilityParams: VisibilityParams(
        actions: [
          VisibilityAction(
            uiAction: UserInterfaceAction(payload: .empty, path: "test"),
            requiredDuration: requiredDuration,
            targetPercentage: 50,
            limiter: ActionLimiter(
              canSend: { [unowned self] in self.actionCount < maxActionCount },
              markSent: { [unowned self] in self.actionCount += 1 }
            ),
            actionType: actionType
          ),
        ],
        isVisible: isVisible,
        lastVisibleArea: Property(
          getter: { self.lastVisibleArea },
          setter: { self.lastVisibleArea = $0 }
        ),
        scheduler: scheduler
      ),
      actionSender: actionView
    )
  }()

  private var actionType = VisibilityActionType.appear
  private var isVisible = true
  private var lastVisibleArea = 0
  private var maxActionCount = 100
  private var actionCount = 0
  private var requiredDuration = TimeInterval(10)

  func test_onVisibleBoundsChanged_UpdatesLastVisibleArea_PartiallyVisible() {
    actionPerformers.onVisibleBoundsChanged(to: rect(100, 50), bounds: rect(100, 100))

    XCTAssertEqual(lastVisibleArea, 50)
  }

  func test_onVisibleBoundsChanged_UpdatesLastVisibleArea_FullyVisible() {
    actionPerformers.onVisibleBoundsChanged(to: rect(100, 200), bounds: rect(100, 100))

    XCTAssertEqual(lastVisibleArea, 100)
  }

  func test_SetsUpTimer_WhenViewAppears() {
    actionPerformers.onVisibleBoundsChanged(to: rect(100, 50), bounds: rect(100, 100))

    XCTAssertFalse(scheduler.timers.isEmpty)
    XCTAssertEqual(scheduler.timers.first!.timeInterval, TimeInterval(10))
  }

  func test_DoesNotResetTimer_WhenVisibleBoundsChangeMultipleTimes() {
    actionPerformers.onVisibleBoundsChanged(to: rect(100, 50), bounds: rect(100, 100))
    actionPerformers.onVisibleBoundsChanged(to: rect(100, 60), bounds: rect(100, 100))

    XCTAssertEqual(scheduler.timers.count, 1)
  }

  func test_DoesNotPerformAction_BeforeTimerIsTriggered() {
    actionPerformers.onVisibleBoundsChanged(to: rect(100, 50), bounds: rect(100, 100))

    XCTAssertEqual(view.eventCount, 0)
  }

  func test_PerformsAction_WhenViewAppears() {
    actionPerformers.onVisibleBoundsChanged(to: rect(100, 200), bounds: rect(100, 100))

    runTimers()

    XCTAssertEqual(view.eventCount, 1)
  }

  func test_PerformsAction_WhenViewPartiallyAppears() {
    actionPerformers.onVisibleBoundsChanged(to: rect(100, 50), bounds: rect(100, 100))

    runTimers()

    XCTAssertEqual(view.eventCount, 1)
  }

  func test_DoesNotPerformAction_WhenViewAppears_BelowThreshold() {
    actionPerformers.onVisibleBoundsChanged(to: rect(100, 40), bounds: rect(100, 100))

    runTimers()

    XCTAssertEqual(view.eventCount, 0)
  }

  func test_DoesNotPerformAction_WhenViewAppearsAndDisappears() {
    actionPerformers.onVisibleBoundsChanged(to: rect(100, 100), bounds: rect(100, 100))
    actionPerformers.onVisibleBoundsChanged(to: rect(100, 40), bounds: rect(100, 100))

    runTimers()

    XCTAssertEqual(view.eventCount, 0)
  }

  func test_DoesNotPerformAction_WhenShowLimitIsReached() {
    maxActionCount = 2

    actionPerformers.onVisibleBoundsChanged(to: rect(100, 100), bounds: rect(100, 100))
    runTimers()
    actionPerformers.onVisibleBoundsChanged(to: rect(100, 0), bounds: rect(100, 100))
    runTimers()
    actionPerformers.onVisibleBoundsChanged(to: rect(100, 100), bounds: rect(100, 100))
    runTimers()
    actionPerformers.onVisibleBoundsChanged(to: rect(100, 0), bounds: rect(100, 100))
    runTimers()
    actionPerformers.onVisibleBoundsChanged(to: rect(100, 100), bounds: rect(100, 100))
    runTimers()

    XCTAssertEqual(view.eventCount, 2)
  }

  func test_PerformsAction_ForZeroSizedView() {
    actionPerformers.onVisibleBoundsChanged(to: rect(100, 100), bounds: rect(0, 0))

    runTimers()

    XCTAssertEqual(view.eventCount, 0)
  }

  func test_DoesNotPerformAction_WhenViewSizeIsChanged() {
    actionPerformers.onVisibleBoundsChanged(to: rect(100, 100), bounds: rect(100, 100))
    runTimers()

    actionPerformers.onVisibleBoundsChanged(to: rect(100, 100), bounds: rect(0, 0))
    runTimers()

    XCTAssertEqual(view.eventCount, 1)
  }

  func test_MarksAsSent_WhenTimerFires() {
    actionPerformers.onVisibleBoundsChanged(to: rect(100, 100), bounds: rect(100, 100))

    runTimers()

    XCTAssertEqual(actionCount, 1)
  }

  func test_DoesNotPerformDisappearAction_WhenViewAppears() {
    actionType = .disappear

    actionPerformers.onVisibleBoundsChanged(to: rect(100, 100), bounds: rect(100, 100))

    runTimers()

    XCTAssertEqual(view.eventCount, 0)
  }

  func test_PerformsDisappearAction_WhenViewDisappears() {
    actionType = .disappear

    actionPerformers.onVisibleBoundsChanged(to: rect(100, 100), bounds: rect(100, 100))
    actionPerformers.onVisibleBoundsChanged(to: rect(100, 0), bounds: rect(100, 100))

    runTimers()

    XCTAssertEqual(view.eventCount, 1)
  }

  func test_PerformsDisappearAction_WhenViewPartiallyDisappears() {
    actionType = .disappear

    actionPerformers.onVisibleBoundsChanged(to: rect(100, 100), bounds: rect(100, 100))
    actionPerformers.onVisibleBoundsChanged(to: rect(100, 40), bounds: rect(100, 100))

    runTimers()

    XCTAssertEqual(view.eventCount, 1)
  }

  func test_PerformsDisappearAction_WhenViewIsGone() {
    actionType = .disappear
    lastVisibleArea = 100
    isVisible = false
    requiredDuration = 0

    actionPerformers.onVisibleBoundsChanged(to: rect(100, 100), bounds: rect(100, 100))

    isVisible = true
    lastVisibleArea = 100

    actionPerformers.onVisibleBoundsChanged(to: rect(100, 100), bounds: rect(0, 0))

    runTimers()

    XCTAssertEqual(view.eventCount, 2)
  }

  func test_InvalidatesTimer_WhenDestroyed() {
    actionPerformers.onVisibleBoundsChanged(to: rect(100, 100), bounds: rect(100, 100))
    actionPerformers = nil

    XCTAssertFalse(scheduler.timers.first!.isValid)
  }

  private func runTimers() {
    for timer in scheduler.timers {
      if timer.isValid {
        timer.fire()
      }
    }
  }
}

private func rect(_ width: Int, _ height: Int) -> CGRect {
  CGRect(x: 0, y: 0, width: width, height: height)
}

private final class TestView: UIView, UIActionEventPerforming {
  private(set) var eventCount = 0

  func perform(uiActionEvent _: UIActionEvent, from _: AnyObject) {
    eventCount += 1
  }
}

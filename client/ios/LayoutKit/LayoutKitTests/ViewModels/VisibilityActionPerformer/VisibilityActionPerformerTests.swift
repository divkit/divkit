@testable import LayoutKit

import XCTest

import CommonCorePublic

final class VisibilityActionPerformerTests: XCTestCase {
  private let requiredDuration = TimeInterval(10)
  private let timerScheduler = TestTimerScheduler()

  private var me: VisibilityActionPerformer!

  private var canSend = true
  private var markSentCounter = 0
  private var actionCounter = 0

  override func setUp() {
    super.setUp()
    me = VisibilityActionPerformer(
      requiredDuration: requiredDuration,
      targetVisibilityPercentage: 50,
      limiter: ActionLimiter(
        canSend: { [unowned self] in self.canSend },
        markSent: { [unowned self] in self.markSentCounter += 1 }
      ),
      action: { [unowned self] in self.actionCounter += 1 },
      type: .appear,
      timerScheduler: timerScheduler
    )
  }

  private var timer: TestTimer {
    timerScheduler.timers.first!
  }

  func test_WhenVisibilityPercentrageExceedsThreshold_SetsUpTimer() {
    me.onVisibleBoundsChanged(visibleAreaPercentageBefore: 0, visibleAreaPercentageAfter: 100)
    XCTAssertFalse(timerScheduler.timers.isEmpty)
    XCTAssertEqual(timer.timeInterval, requiredDuration)
  }

  func test_WhenExceedLimit_DoesNotSetUpTimer() {
    canSend = false
    me.onVisibleBoundsChanged(visibleAreaPercentageBefore: 0, visibleAreaPercentageAfter: 100)
    XCTAssertTrue(timerScheduler.timers.isEmpty)
  }

  func test_WhenVisibilityPercentrageDoesNotExceedThreshold_InvalidatesTimer() {
    me.onVisibleBoundsChanged(visibleAreaPercentageBefore: 0, visibleAreaPercentageAfter: 100)
    me.onVisibleBoundsChanged(visibleAreaPercentageBefore: 100, visibleAreaPercentageAfter: 0)
    XCTAssertFalse(timer.isValid)
  }

  func test_WhenTimerFires_CallsAction() {
    me.onVisibleBoundsChanged(visibleAreaPercentageBefore: 0, visibleAreaPercentageAfter: 100)
    timer.fire()
    XCTAssertEqual(actionCounter, 1)
  }

  func test_WhenTimerFires_MarksAsSent() {
    me.onVisibleBoundsChanged(visibleAreaPercentageBefore: 0, visibleAreaPercentageAfter: 100)
    timer.fire()
    XCTAssertEqual(markSentCounter, 1)
  }

  func test_WhenVisibilityPercentrageExceedsThresholdSeveralTimes_DoesNotResetTimer() {
    me.onVisibleBoundsChanged(visibleAreaPercentageBefore: 0, visibleAreaPercentageAfter: 90)
    me.onVisibleBoundsChanged(visibleAreaPercentageBefore: 0, visibleAreaPercentageAfter: 100)
    XCTAssertEqual(timerScheduler.timers.count, 1)
  }

  func test_WhenDestroyed_InvalidatesTimer() {
    me.onVisibleBoundsChanged(visibleAreaPercentageBefore: 0, visibleAreaPercentageAfter: 100)
    me = nil
    XCTAssertFalse(timer.isValid)
  }
}

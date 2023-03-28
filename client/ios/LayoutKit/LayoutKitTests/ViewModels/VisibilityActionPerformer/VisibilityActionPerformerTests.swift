@testable import LayoutKit

import XCTest

import CommonCorePublic

final class VisibilityActionPerformerTests: XCTestCase {
  private let requiredVisibilityDuration = TimeInterval(10)
  private let timerScheduler = TestTimerScheduler()

  private var me: VisibilityActionPerformer!

  private var canSend = true
  private var markSentCounter = 0
  private var actionCounter = 0

  override func setUp() {
    super.setUp()
    me = VisibilityActionPerformer(
      requiredVisibilityDuration: requiredVisibilityDuration,
      targetVisibilityPercentage: 50,
      limiter: VisibilityAction.Limiter(
        canSend: { [unowned self] in self.canSend },
        markSent: { [unowned self] in self.markSentCounter += 1 }
      ),
      action: { [unowned self] in self.actionCounter += 1 },
      timerScheduler: timerScheduler
    )
  }

  private var timer: TestTimer {
    timerScheduler.timers.first!
  }

  func test_WhenVisibilityPercentrageExceedsThreshold_SetsUpTimer() {
    me.onVisibleBoundsChanged(visibleAreaPercentage: 100)
    XCTAssertFalse(timerScheduler.timers.isEmpty)
    XCTAssertEqual(timer.timeInterval, requiredVisibilityDuration)
  }

  func test_WhenExceedLimit_DoesNotSetUpTimer() {
    canSend = false
    me.onVisibleBoundsChanged(visibleAreaPercentage: 100)
    XCTAssertTrue(timerScheduler.timers.isEmpty)
  }

  func test_WhenVisibilityPercentrageDoesNotExceedThreshold_InvalidatesTimer() {
    me.onVisibleBoundsChanged(visibleAreaPercentage: 100)
    me.onVisibleBoundsChanged(visibleAreaPercentage: 0)
    XCTAssertFalse(timer.isValid)
  }

  func test_WhenTimerFires_CallsAction() {
    me.onVisibleBoundsChanged(visibleAreaPercentage: 100)
    timer.fire()
    XCTAssertEqual(actionCounter, 1)
  }

  func test_WhenTimerFires_MarksAsSent() {
    me.onVisibleBoundsChanged(visibleAreaPercentage: 100)
    timer.fire()
    XCTAssertEqual(markSentCounter, 1)
  }

  func test_WhenVisibilityPercentrageExceedsThresholdSeveralTimes_DoesNotResetTimer() {
    me.onVisibleBoundsChanged(visibleAreaPercentage: 90)
    me.onVisibleBoundsChanged(visibleAreaPercentage: 100)
    XCTAssertEqual(timerScheduler.timers.count, 1)
  }

  func test_WhenDestroyed_InvalidatesTimer() {
    me.onVisibleBoundsChanged(visibleAreaPercentage: 100)
    me = nil
    XCTAssertFalse(timer.isValid)
  }
}

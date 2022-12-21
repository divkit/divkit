@testable import DivKit

import Foundation
import XCTest

import CommonCore

final class DivTimerControllerTests: XCTestCase {
  private let variableName = "test_variable_name"
  private let tickInterval = 1000
  private let duration = 3000
  private let tickActions = [DivAction(logId: "Tick action")]
  private let endActions = [DivAction(logId: "End action")]
  private var variableValue: String? = nil
  private var tickActionsCount = 0
  private var endActionsCount = 0

  private let timerScheduler = TestTimerScheduler()
  private let timeMeasuring = TestTimeMeasuring()

  private var divTimer: DivTimer!
  private var timer: DivTimerController!

  override func setUp() {
    super.setUp()
    divTimer = DivTimer(
      duration: .value(duration),
      endActions: endActions,
      id: "test_timer",
      tickActions: tickActions,
      tickInterval: .value(tickInterval),
      valueVariable: variableName
    )
    timer = DivTimerController(
      divTimer: divTimer,
      timerScheduler: timerScheduler,
      timeMeasuring: timeMeasuring,
      runActions: { [unowned self] actions in
        if actions == tickActions {
          tickActionsCount += 1
        } else if actions == endActions {
          endActionsCount += 1
        }
      },
      updateVariable: { [unowned self] name, value in
        if name.rawValue == variableName {
          variableValue = value
        }
      },
      updateCard: {}
    )
  }
  
  func test_WhenTimerStart_StateIsStarted() throws {
    timer.start()
    XCTAssertEqual(timer.state, .started)
  }

  func test_WhenTimerStart_VariableSetToZero() throws {
    timer.start()
    XCTAssertEqual(variableValue, "0")
  }

  func test_timerStartOnlyOnce() throws {
    timer.start()
    timer.start()
    XCTAssertEqual(timeMeasuring.started, 1)
  }

  func test_WhenTimerPaused_TimerNotStart() throws {
    timer.start()
    timer.pause()
    timer.start()
    XCTAssertEqual(timeMeasuring.started, 1)
  }

  func test_WhenTimerInited_StateIsStopped() throws {
    XCTAssertEqual(timer.state, .stopped)
  }

  func test_WhenTimerStarted_StateIsStoppedOnStop() throws {
    timer.start()
    timer.stop()
    XCTAssertEqual(timer.state, .stopped)
  }

  func test_WhenTimerStarted_VariableEqualPassedIntervalOnStop() throws {
    timer.start()
    makeTick()
    timer.stop()
    XCTAssertEqual(variableValue, String(tickInterval))
  }

  func test_WhenTimerStarted_runEndActionsOnStop() throws {
    timer.start()
    timer.stop()
    XCTAssertEqual(endActionsCount, 1)
  }

  func test_WhenTimerPaused_runEndActionsOnStop() throws {
    timer.start()
    timer.pause()
    timer.stop()
    XCTAssertEqual(endActionsCount, 1)
  }

  func test_WhenTimerPaused_stateIsStopOnStop() throws {
    timer.start()
    timer.pause()
    timer.stop()
    XCTAssertEqual(timer.state, .stopped)
  }

  func test_WhenTimerStopped_StateNotChangedOnPaused() throws {
    timer.pause()
    XCTAssertEqual(timer.state, .stopped)
  }

  func test_WhenTimerStarted_StateIsPausedOnPaused() throws {
    timer.start()
    timer.pause()
    XCTAssertEqual(timer.state, .paused)
  }

  func test_WhenTimerStarted_VariableEqualPassedIntervalOnPause() throws {
    timer.start()
    makeTick()
    timer.pause()
    XCTAssertEqual(variableValue, String(tickInterval))
  }

  func test_timerPausedOnlyOnce() throws {
    timer.start()
    timer.pause()
    timer.pause()
    XCTAssertEqual(timeMeasuring.paused, 1)
  }

  func test_WhenTimerStopped_StateNotChangedOnResume() throws {
    timer.resume()
    XCTAssertEqual(timer.state, .stopped)
  }

  func test_WhenTimerStarted_StateNotChangedOnResume() throws {
    timer.start()
    timer.resume()
    XCTAssertEqual(timer.state, .started)
  }

  func test_WhenTimerPaused_StateIsStartedOnResume() throws {
    timer.start()
    timer.pause()
    timer.resume()
    XCTAssertEqual(timer.state, .started)
  }

  func test_WhenTimerStopped_StateNotChangedOnCancel() throws {
    timer.cancel()
    XCTAssertEqual(timer.state, .stopped)
  }

  func test_WhenTimerStarted_StateIsStoppedOnCancel() throws {
    timer.start()
    timer.cancel()
    XCTAssertEqual(timer.state, .stopped)
  }

  func test_WhenTimerStarted_VariableEqualPassedIntervalOnCancel() throws {
    timer.start()
    makeTick()
    timer.cancel()
    XCTAssertEqual(variableValue, String(tickInterval))
  }

  func test_WhenTimerPaused_StateIsStoppedOnCancel() throws {
    timer.start()
    timer.pause()
    timer.cancel()
    XCTAssertEqual(timer.state, .stopped)
  }

  func test_WhenTimerStopped_StateIsStartedOnReset() throws {
    timer.reset()
    XCTAssertEqual(timer.state, .started)
  }

  func test_WhenTimerStopped_VariableSetToZeroOnReset() throws {
    timer.reset()
    XCTAssertEqual(variableValue, "0")
  }

  func test_WhenTimerStarted_StateIsStartedOnReset() throws {
    timer.start()
    timer.reset()
    XCTAssertEqual(timer.state, .started)
  }

  func test_WhenTimerStarted_VariableSetToZeroOnReset() throws {
    timer.start()
    makeTick()
    timer.reset()
    XCTAssertEqual(variableValue, "0")
  }

  func test_WhenTimerPaused_StateIsStartedOnReset() throws {
    timer.start()
    timer.pause()
    timer.reset()
    XCTAssertEqual(timer.state, .started)
  }

  func test_WhenTimerTick_runTickActions() throws {
    timer.start()
    makeTick()
    XCTAssertEqual(tickActionsCount, 1)
  }

  func test_WhenTimerTick_VariableEqualTickInterval() throws {
    timer.start()
    makeTick()
    XCTAssertEqual(variableValue, String(tickInterval))
  }

  func test_WhenTimerEnded_runEndActions() throws {
    timer.start()
    endTimer()
    XCTAssertEqual(endActionsCount, 1)
  }

  func test_WhenTimerEnded_VariableEqualDuration() throws {
    timer.start()
    endTimer()
    XCTAssertEqual(variableValue, String(duration))
  }

  func test_WhenTimerEnded_stateStopped() throws {
    timer.start()
    endTimer()
    XCTAssertEqual(timer.state, .stopped)
  }

  func test_WhenTimerResumed_callTickActions() throws {
    timer.start()
    makeHalfTick()
    timer.pause()
    timer.resume()
    XCTAssertEqual(tickActionsCount, 1)
  }

  func test_WhenTimerResumed_checkVariableOnNextTick() throws {
    timer.start()
    makeHalfTick()
    timer.pause()
    timer.resume()
    makeHalfTick()
    XCTAssertEqual(variableValue, String(tickInterval))
  }

  func test_WhenTimerResumed_checkTickActionsCountOnNextTick() throws {
    timer.start()
    makeHalfTick()
    timer.pause()
    timer.resume()
    makeHalfTick()
    XCTAssertEqual(tickActionsCount, 2)
  }

  func test_WhenTimerResumed_checkTickActionsCountOnSecondTick() throws {
    timer.start()
    makeHalfTick()
    timer.pause()
    timer.resume()
    makeHalfTick()
    makeTick()
    XCTAssertEqual(tickActionsCount, 3)
  }

  func test_WhenTimerResumed_checkVariableOnSecondTick() throws {
    timer.start()
    makeHalfTick()
    timer.pause()
    timer.resume()
    makeHalfTick()
    makeTick()
    XCTAssertEqual(variableValue, String(tickInterval * 2))
  }

  func test_WhenTimerResumed_VariableChanged() throws {
    timer.start()
    makeHalfTick()
    timer.pause()
    timer.resume()
    XCTAssertEqual(variableValue, String(tickInterval / 2))
  }

  private func makeHalfTick() {
    let halfTick = tickInterval.timeInterval / 2
    timeMeasuring.passedTime = timeMeasuring.passedTime + halfTick
    fireTimer(with: halfTick)
  }

  private func makeTick() {
    let tick = tickInterval.timeInterval
    timeMeasuring.passedTime = timeMeasuring.passedTime + tick
    fireTimer(with: tick)
  }

  private func endTimer() {
    let duration = duration.timeInterval
    timeMeasuring.passedTime = duration
    fireTimer(with: duration)
  }

  private func fireTimer(with interval: TimeInterval) {
    timerScheduler.timers.forEach {
      if $0.isValid && $0.timeInterval == interval {
        $0.fire()
      }
    }
  }
}

fileprivate extension Int {
  var timeInterval: TimeInterval {
    TimeInterval(self / 1000)
  }
}

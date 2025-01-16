@testable @_spi(Internal) import DivKit
import Foundation
import XCTest

final class DivTimerControllerTests: XCTestCase {
  private let tickActions = [divAction(logId: "Tick action")]
  private let endActions = [divAction(logId: "End action")]
  private var tickActionsCount = 0
  private var endActionsCount = 0

  private let timerScheduler = TestTimerScheduler()
  private let timeMeasuring = TestTimeMeasuring()

  private var timer: DivTimerController!
  private var timerDuration: Int = 0
  private var timerTickInterval: Int = 0

  private let variablesStorage = DivVariablesStorage()

  private let functionsStorage = DivFunctionsStorage()

  private var variableValue: Int? {
    variablesStorage.getVariableValue(cardId: "test_card", name: "test_variable_name")
  }

  override func setUp() {
    super.setUp()

    variablesStorage.set(
      cardId: "test_card",
      variables: ["test_variable_name": .integer(-1)]
    )

    timer = makeTimer()
  }

  func test_WhenTimerStart_StateIsStarted() throws {
    timer.start()
    XCTAssertEqual(timer.state, .started)
  }

  func test_WhenTimerStart_VariableSetToZero() throws {
    timer.start()
    XCTAssertEqual(variableValue, 0)
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
    XCTAssertEqual(variableValue, tickInterval)
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
    XCTAssertEqual(variableValue, tickInterval)
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
    XCTAssertEqual(variableValue, tickInterval)
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
    XCTAssertEqual(variableValue, 0)
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
    XCTAssertEqual(variableValue, 0)
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
    XCTAssertEqual(variableValue, tickInterval)
  }

  func test_WhenTimerEnded_runEndActions() throws {
    timer.start()
    endTimer()
    XCTAssertEqual(endActionsCount, 1)
  }

  func test_WhenTimerEnded_VariableEqualDuration() throws {
    timer.start()
    endTimer()
    XCTAssertEqual(variableValue, duration)
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
    XCTAssertEqual(variableValue, tickInterval)
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
    XCTAssertEqual(variableValue, tickInterval * 2)
  }

  func test_WhenTimerResumed_VariableChanged() throws {
    timer.start()
    makeHalfTick()
    timer.pause()
    timer.resume()
    XCTAssertEqual(variableValue, tickInterval / 2)
  }

  func test_WhenTimerEnded_runTickActionWhenNeeded() throws {
    timer.start()
    makeTick()
    makeTick()
    endTimer()
    XCTAssertEqual(tickActionsCount, tickCount)
  }

  func test_WhenTimerEnded_doNotRunTickActionWhenCountEnough() throws {
    timer.start()
    makeTick()
    makeTick()
    makeTick()
    endTimer()
    XCTAssertEqual(tickActionsCount, tickCount)
  }

  func test_WhenTimerEnded_doNotRunTickAction() throws {
    timer = makeTimer(duration: 3000, tickInterval: 2000)
    timer.start()
    endTimer()
    XCTAssertEqual(tickActionsCount, 0)
  }

  func test_timerWithSmallValues() throws {
    timer = makeTimer(duration: 1, tickInterval: 1)
    timer.start()
    endTimer()
    XCTAssertEqual(tickActionsCount, 1)
    XCTAssertEqual(endActionsCount, 1)
  }

  private func makeHalfTick() {
    let halfTick = timerTickInterval.toTimeInterval / 2
    timeMeasuring.passedTime = timeMeasuring.passedTime + halfTick
    fireTimer(with: halfTick)
  }

  private func makeTick() {
    let interval = timerTickInterval.toTimeInterval
    timeMeasuring.passedTime = timeMeasuring.passedTime + interval
    fireTimer(with: interval)
  }

  private func endTimer() {
    let interval = timerDuration.toTimeInterval
    timeMeasuring.passedTime = interval
    fireTimer(with: interval)
  }

  private func fireTimer(with interval: TimeInterval) {
    for timer in timerScheduler.timers {
      if timer.isValid, timer.timeInterval == interval {
        timer.fire()
      }
    }
  }

  private func makeTimer(
    duration: Int = duration,
    tickInterval: Int = tickInterval
  ) -> DivTimerController {
    let divTimer = DivTimer(
      duration: .value(duration),
      endActions: endActions,
      id: "test_timer",
      tickActions: tickActions,
      tickInterval: .value(tickInterval),
      valueVariable: "test_variable_name"
    )
    timerDuration = duration
    timerTickInterval = tickInterval
    return DivTimerController(
      cardId: "test_card",
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
      updateCard: {},
      variablesStorage: variablesStorage,
      functionsStorage: functionsStorage,
      persistentValuesStorage: DivPersistentValuesStorage(),
      reporter: DefaultDivReporter()
    )
  }
}

extension Int {
  fileprivate var timeInterval: TimeInterval {
    TimeInterval(self / 1000)
  }
}

private let tickCount = 3
private let tickInterval = 1000
private let duration = tickInterval * tickCount

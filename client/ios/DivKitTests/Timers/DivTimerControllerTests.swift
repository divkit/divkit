@testable @_spi(Internal) import DivKit
import DivKitTestsSupport
import Foundation
import Testing

@Suite
struct DivTimerControllerTests {
  private class State {
    var tickCount = 0
    var endCount = 0
  }

  private let state = State()
  private let timerScheduler = TestTimerScheduler()
  private let timeMeasuring = TestTimeMeasuring()
  private let variablesStorage = DivVariablesStorage()

  private var variableValue: Int? {
    variablesStorage.getVariableValue(cardId: cardId, name: "elapsed_time")
  }

  init() {
    variablesStorage.set(
      cardId: cardId,
      variables: ["elapsed_time": .integer(-1)]
    )
  }

  @Test
  func initialStateIsStopped() {
    let timer = makeTimer()

    #expect(timer.state == .stopped)
  }

  @Test
  func start_SetsStartedState() {
    let timer = makeTimer()
    timer.start()

    #expect(timer.state == .started)
  }

  @Test
  func start_SetsVariableToZero() {
    let timer = makeTimer()
    timer.start()

    #expect(variableValue == 0)
  }

  @Test
  func start_DoesNotAffectStartedTimer() {
    let timer = makeTimer()
    timer.start()
    timer.start()

    #expect(timeMeasuring.started == 1)
  }

  @Test
  func start_DoesNotAffectPausedTimer() {
    let timer = makeTimer()
    timer.start()
    timer.pause()
    timer.start()

    #expect(timeMeasuring.started == 1)
  }

  @Test
  func stop_SetsStoppedState_ForStartedTimer() {
    let timer = makeTimer()
    timer.start()
    timer.stop()

    #expect(timer.state == .stopped)
  }

  @Test
  func stop_SetsStoppedState_ForPausedTimer() {
    let timer = makeTimer()
    timer.start()
    timer.pause()
    timer.stop()

    #expect(timer.state == .stopped)
  }

  @Test
  func stop_TriggersEndActions_ForStartedTimer() {
    let timer = makeTimer()
    timer.start()
    timer.stop()

    #expect(state.endCount == 1)
  }

  @Test
  func stop_TriggersEndActions_ForPausedTimer() {
    let timer = makeTimer()
    timer.start()
    timer.pause()
    timer.stop()

    #expect(state.endCount == 1)
  }

  @Test
  func pause_DoesNotAffectStoppedTimer() {
    let timer = makeTimer()
    timer.pause()

    #expect(timer.state == .stopped)
  }

  @Test
  func pause_SetsPausedState() {
    let timer = makeTimer()
    timer.start()
    timer.pause()

    #expect(timer.state == .paused)
  }

  @Test
  func pause_UpdatesVariable() {
    let timer = makeTimer(duration: 3000, tickInterval: 1000)
    timer.start()
    tick(1000)
    tick(500)

    #expect(variableValue == 1000)

    timer.pause()

    #expect(variableValue == 1500)
  }

  @Test
  func pause_DoesNotAffectPausedTimer() {
    let timer = makeTimer()
    timer.start()
    timer.pause()
    timer.pause()

    #expect(timeMeasuring.paused == 1)
  }

  @Test
  func resume_DoesNotAffectStoppedTimer() {
    let timer = makeTimer()
    timer.resume()

    #expect(timer.state == .stopped)
  }

  @Test
  func resume_DoesNotAffectStartedTimer() {
    let timer = makeTimer()
    timer.start()
    timer.resume()

    #expect(timer.state == .started)
  }

  @Test
  func resume_SetsStartedState_ForPausedTimer() {
    let timer = makeTimer()
    timer.start()
    timer.pause()
    timer.resume()

    #expect(timer.state == .started)
  }

  @Test
  func resume_DoesNotTriggerTickActions() {
    let timer = makeTimer(duration: 3000, tickInterval: 1000)
    timer.start()
    tick(500)
    timer.pause()

    #expect(state.tickCount == 0)

    timer.resume()

    #expect(state.tickCount == 0)
  }

  @Test
  func cancel_DoesNotAffectStoppedTimer() {
    let timer = makeTimer()
    timer.cancel()

    #expect(timer.state == .stopped)
  }

  @Test
  func cancel_SetsStateToStopped_ForStartedTimer() {
    let timer = makeTimer()
    timer.start()
    timer.cancel()

    #expect(timer.state == .stopped)
  }

  @Test
  func cancel_SetsStateToStopped_ForPausedTimer() {
    let timer = makeTimer()
    timer.start()
    timer.pause()
    timer.cancel()

    #expect(timer.state == .stopped)
  }

  @Test
  func cancel_DoesNotUpdateVariable() {
    let timer = makeTimer(duration: 3000, tickInterval: 1000)
    timer.start()
    tick(1000)

    #expect(variableValue == 1000)

    tick(500)
    timer.cancel()

    #expect(variableValue == 1000)
  }

  @Test
  func reset_SetsStartedState_ForStoppedTimer() {
    let timer = makeTimer()
    timer.reset()

    #expect(timer.state == .started)
  }

  @Test
  func reset_SetsVariableToZero_ForStoppedTimer() {
    let timer = makeTimer()
    timer.reset()

    #expect(variableValue == 0)
  }

  @Test
  func reset_SetsStartedState_ForStartedTimer() {
    let timer = makeTimer()
    timer.start()
    timer.reset()

    #expect(timer.state == .started)
  }

  @Test
  func reset_SetsStartedState_ForPausedTimer() {
    let timer = makeTimer()
    timer.start()
    timer.pause()
    timer.reset()

    #expect(timer.state == .started)
  }

  @Test
  func reset_SetsVariableToZero_ForStartedTimer() {
    let timer = makeTimer(duration: 3000, tickInterval: 1000)
    timer.start()
    tick(1000)

    #expect(variableValue == 1000)

    timer.reset()

    #expect(variableValue == 0)
  }

  @Test
  func tick_TriggersTickActions() {
    let timer = makeTimer(duration: 3000, tickInterval: 1000)
    timer.start()
    tick(1000)

    #expect(state.tickCount == 1)

    tick(1000)

    #expect(state.tickCount == 2)
  }

  @Test
  func tick_UpdatesVariable() {
    let timer = makeTimer(duration: 3000, tickInterval: 1000)
    timer.start()
    tick(1000)

    #expect(variableValue == 1000)
  }

  @Test
  func endActionsAreTriggered_WhenTimerEnded() {
    let timer = makeTimer(duration: 3000, tickInterval: 1000)
    timer.start()
    tick(3000)

    #expect(state.endCount == 1)
  }

  @Test
  func variableIsUpdated_WhenTimerEnded() {
    let timer = makeTimer(duration: 3000, tickInterval: 1000)
    timer.start()
    tick(3000)

    #expect(variableValue == 3000)
  }

  @Test
  func stateIsStopped_WhenTimerEnded() {
    let timer = makeTimer(duration: 3000, tickInterval: 1000)
    timer.start()
    tick(3000)

    #expect(timer.state == .stopped)
  }

  @Test
  func variableIsUpdatedOnNextTickAfterResume() {
    let timer = makeTimer(duration: 3000, tickInterval: 1000)
    timer.start()
    tick(500)
    timer.pause()

    #expect(variableValue == 500)

    timer.resume()
    tick(500)

    #expect(variableValue == 1000)

    tick(1000)

    #expect(variableValue == 2000)
  }

  @Test
  func tickActionsAreTriggeredOnNextTickAfterResume() {
    let timer = makeTimer(duration: 3000, tickInterval: 1000)
    timer.start()
    tick(500)
    timer.pause()
    timer.resume()
    tick(500)

    #expect(state.tickCount == 1)

    tick(1000)

    #expect(state.tickCount == 2)
  }

  @Test
  func tickActionsAreTriggered_WhenTimerEnded() {
    let timer = makeTimer(duration: 3000, tickInterval: 1000)
    timer.start()
    tick(1000)
    tick(1000)

    #expect(state.tickCount == 2)

    tick(3000)

    #expect(state.tickCount == 3)
  }

  @Test
  func updatedTickIntervalIsAppliedAfterReset() {
    let timer = makeTimer(duration: 3000, tickInterval: 1000)
    timer.start()
    tick(1000)

    #expect(state.tickCount == 1)

    variablesStorage.update(cardId: cardId, name: "tick_interval", value: "500")

    tick(500)

    #expect(state.tickCount == 1)

    timer.reset()

    tick(500)

    #expect(state.tickCount == 2)

    tick(500)

    #expect(state.tickCount == 3)
  }

  private func tick(_ interval: Int) {
    timeMeasuring.passedTime += interval.toTimeInterval
    for timer in timerScheduler.timers {
      if timer.isValid, timer.timeInterval == interval.toTimeInterval {
        timer.fire()
      }
    }
  }

  private func makeTimer(
    duration: Int = 3000,
    tickInterval: Int = 1000
  ) -> DivTimerController {
    variablesStorage.append(
      variables: [
        "duration": .integer(duration),
        "tick_interval": .integer(tickInterval),
      ],
      for: cardId
    )
    let divTimer = DivTimer(
      duration: expression("@{duration}"),
      endActions: endActions,
      id: "test_timer",
      tickActions: tickActions,
      tickInterval: expression("@{tick_interval}"),
      valueVariable: "elapsed_time"
    )
    return DivTimerController(
      cardId: cardId,
      divTimer: divTimer,
      timerScheduler: timerScheduler,
      timeMeasuring: timeMeasuring,
      runActions: { actions in
        if actions == tickActions {
          state.tickCount += 1
        } else if actions == endActions {
          state.endCount += 1
        }
      },
      updateCard: {},
      variablesStorage: variablesStorage,
      functionsStorage: DivFunctionsStorage(),
      persistentValuesStorage: DivPersistentValuesStorage(),
      reporter: DefaultDivReporter()
    )
  }
}

private let cardId: DivCardID = "test_card"

private let tickActions = [divAction(logId: "tick")]
private let endActions = [divAction(logId: "end")]

@testable @_spi(Internal) import DivKit
import DivKitTestsSupport
import Foundation
import Testing

@Suite()
struct DivTimerStorageTests {
  private let scheduler = TestTimerScheduler()
  private let tickActionHandler = TickActionHandler()
  private let timeMeasuring = TestTimeMeasuring()

  private var storage: DivTimerStorage!

  init() {
    let timeMeasuring = timeMeasuring
    storage = DivTimerStorage(
      variablesStorage: DivVariablesStorage(),
      functionsStorage: DivFunctionsStorage(),
      actionHandler: DivActionHandler(
        urlHandler: tickActionHandler
      ),
      updateCard: { _ in },
      persistentValuesStorage: DivPersistentValuesStorage(),
      reporter: DefaultDivReporter(),
      scheduler: scheduler,
      timeMeasuringFactory: { timeMeasuring }
    )
  }

  @Test
  func start_startsTimer() {
    storage.set(cardId: cardId, timers: [timer("timer1")])

    storage.start(cardId: cardId, timerId: "timer1")

    tick()
    #expect(popAction() == "tick://timer1")

    tick()
    #expect(popAction() == "tick://timer1")
  }

  @Test
  func pause_pausesTimer() {
    storage.set(cardId: cardId, timers: [timer("timer1")])
    storage.start(cardId: cardId, timerId: "timer1")

    tick()
    #expect(popAction() == "tick://timer1")

    storage.pause(cardId: cardId, timerId: "timer1")

    tick()
    #expect(popAction() == nil)
  }

  @Test
  func stop_stopsTimer() {
    storage.set(cardId: cardId, timers: [timer("timer1")])
    storage.start(cardId: cardId, timerId: "timer1")

    tick()
    #expect(popAction() == "tick://timer1")

    storage.stop(cardId: cardId, timerId: "timer1")

    tick()
    #expect(popAction() == nil)
  }

  @Test
  func reset_removesTimer() {
    storage.set(cardId: cardId, timers: [timer("timer1")])

    storage.reset()

    storage.start(cardId: cardId, timerId: "timer1")
    tick()
    #expect(popAction() == nil)
  }

  @Test
  func reset_cancelsStartedTimer() {
    storage.set(cardId: cardId, timers: [timer("timer1")])
    storage.start(cardId: cardId, timerId: "timer1")

    tick()
    #expect(popAction() == "tick://timer1")

    storage.reset()

    tick()
    #expect(popAction() == nil)
  }

  @Test
  func reset_byCardId_removesTimer() {
    storage.set(cardId: cardId, timers: [timer("timer1")])
    storage.set(cardId: "card2", timers: [timer("timer2")])

    storage.reset(cardId: cardId)

    storage.start(cardId: cardId, timerId: "timer1")
    tick()
    #expect(popAction() == nil)

    storage.start(cardId: "card2", timerId: "timer2")
    tick()
    #expect(popAction() == "tick://timer2")
  }

  @Test
  func reset_byCardId_cancelsStartedTimer() {
    storage.set(cardId: cardId, timers: [timer("timer1")])
    storage.start(cardId: cardId, timerId: "timer1")

    tick()
    #expect(popAction() == "tick://timer1")

    storage.reset(cardId: cardId)

    tick()
    #expect(popAction() == nil)
  }

  @Test
  func set_removesOldTimer() {
    storage.set(cardId: cardId, timers: [timer("timer1")])

    storage.set(cardId: cardId, timers: [timer("timer2")])

    storage.start(cardId: cardId, timerId: "timer1")
    tick()
    #expect(popAction() == nil)
  }

  @Test
  func set_cancelsStartedTimer() {
    storage.set(cardId: cardId, timers: [timer("timer1")])
    storage.start(cardId: cardId, timerId: "timer1")

    tick()
    #expect(popAction() == "tick://timer1")

    storage.set(cardId: cardId, timers: [timer("timer2")])

    tick()
    #expect(popAction() == nil)
  }

  private func tick() {
    let interval = tickInterval.toTimeInterval
    timeMeasuring.passedTime = timeMeasuring.passedTime + interval

    for timer in scheduler.timers {
      if timer.isValid, timer.timeInterval == interval {
        timer.fire()
      }
    }
  }

  private func popAction() -> String? {
    let actions = tickActionHandler.pop()
    #expect(actions.count <= 1)
    return actions.first
  }
}

private func timer(_ id: String) -> DivTimer {
  DivTimer(
    id: id,
    tickActions: [
      divAction(url: "tick://\(id)"),
    ],
    tickInterval: .value(tickInterval)
  )
}

private final class TickActionHandler: DivUrlHandler {
  private(set) var actions: [String] = []

  func handle(_ url: URL, sender _: AnyObject?) {
    actions.append(url.absoluteString)
  }

  func pop() -> [String] {
    let actions = actions
    self.actions = []
    return actions
  }
}

private let cardId: DivCardID = "test_card"
private let tickInterval = 1000

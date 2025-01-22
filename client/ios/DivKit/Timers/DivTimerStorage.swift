import Foundation
import LayoutKit
import VGSL

final class DivTimerStorage {
  private let variablesStorage: DivVariablesStorage
  private let functionsStorage: DivFunctionsStorage
  private let actionHandler: DivActionHandler
  private let updateCard: DivActionURLHandler.UpdateCardAction
  private let timerScheduler = TimerScheduler()
  private let persistentValuesStorage: DivPersistentValuesStorage
  private let reporter: DivReporter

  private var cardsTimers = [DivCardID: [DivTimer]]()
  private var timerControllers = [String: DivTimerController]()

  init(
    variablesStorage: DivVariablesStorage,
    functionsStorage: DivFunctionsStorage,
    actionHandler: DivActionHandler,
    updateCard: @escaping DivActionURLHandler.UpdateCardAction,
    persistentValuesStorage: DivPersistentValuesStorage,
    reporter: DivReporter
  ) {
    self.variablesStorage = variablesStorage
    self.functionsStorage = functionsStorage
    self.actionHandler = actionHandler
    self.updateCard = updateCard
    self.persistentValuesStorage = persistentValuesStorage
    self.reporter = reporter
  }

  func set(
    cardId: DivCardID,
    timers: [DivTimer]
  ) {
    cardsTimers[cardId] = timers
  }

  func perform(_ cardId: DivCardID, _ timerId: String, _ action: DivTimerAction) {
    switch action {
    case .start:
      start(cardId: cardId, timerId: timerId)
    case .stop:
      stop(cardId: cardId, timerId: timerId)
    case .pause:
      pause(cardId: cardId, timerId: timerId)
    case .resume:
      resume(cardId: cardId, timerId: timerId)
    case .cancel:
      cancel(cardId: cardId, timerId: timerId)
    case .reset:
      reset(cardId: cardId, timerId: timerId)
    }
  }

  func start(cardId: DivCardID, timerId: String) {
    if let timer = getTimer(cardId: cardId, timerId: timerId) {
      timer.start()
    }
  }

  func stop(cardId: DivCardID, timerId: String) {
    if let timer = getTimer(cardId: cardId, timerId: timerId) {
      timer.stop()
    }
  }

  func pause(cardId: DivCardID, timerId: String) {
    if let timer = getTimer(cardId: cardId, timerId: timerId) {
      timer.pause()
    }
  }

  func resume(cardId: DivCardID, timerId: String) {
    if let timer = getTimer(cardId: cardId, timerId: timerId) {
      timer.resume()
    }
  }

  func cancel(cardId: DivCardID, timerId: String) {
    if let timer = getTimer(cardId: cardId, timerId: timerId) {
      timer.cancel()
    }
  }

  func reset(cardId: DivCardID, timerId: String) {
    if let timer = getTimer(cardId: cardId, timerId: timerId) {
      timer.reset()
    }
  }

  func reset() {
    for (_, timer) in timerControllers {
      timer.cancel()
    }
    timerControllers.removeAll()
  }

  func reset(cardId: DivCardID) {
    for (key, timer) in timerControllers {
      if key.starts(with: cardId.rawValue) {
        timer.cancel()
      }
    }
    timerControllers = timerControllers.filter { !$0.key.starts(with: cardId.rawValue) }
  }

  private func getTimer(cardId: DivCardID, timerId: String) -> DivTimerController? {
    guard let timers = cardsTimers[cardId],
          let divTimer = timers.filter({ $0.id == timerId }).first else {
      DivKitLogger.failure("Timer with id:'\(timerId)' not found for cardId:'\(cardId)'")
      return nil
    }
    let key = makeControllerKey(cardId: cardId, divTimer: divTimer)
    if let timer = timerControllers[key] {
      return timer
    }
    let timer = makeTimer(cardId: cardId, divTimer: divTimer)
    timerControllers[key] = timer
    return timer
  }

  private func makeControllerKey(cardId: DivCardID, divTimer: DivTimer) -> String {
    "\(cardId)_\(divTimer.id)"
  }

  private func makeTimer(
    cardId: DivCardID,
    divTimer: DivTimer
  ) -> DivTimerController {
    DivTimerController(
      cardId: cardId,
      divTimer: divTimer,
      timerScheduler: timerScheduler,
      timeMeasuring: TimeIntervalMeasuring(),
      runActions: { [weak self] actions in
        self?.runActions(cardId: cardId, actions: actions)
      },
      updateCard: { [weak self] in
        self?.updateCard(.timer(cardId))
      },
      variablesStorage: variablesStorage,
      functionsStorage: functionsStorage,
      persistentValuesStorage: persistentValuesStorage,
      reporter: reporter
    )
  }

  private func runActions(cardId: DivCardID, actions: [DivAction]) {
    for action in actions {
      actionHandler.handle(
        action,
        path: cardId.path,
        source: .timer,
        sender: nil
      )
    }
  }
}

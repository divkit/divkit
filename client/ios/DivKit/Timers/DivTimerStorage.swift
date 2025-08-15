import Foundation
import LayoutKit
import VGSL

final class DivTimerStorage {
  private let variablesStorage: DivVariablesStorage
  private let functionsStorage: DivFunctionsStorage
  private let actionHandler: DivActionHandler
  private let updateCard: DivActionHandler.UpdateCardAction
  private let persistentValuesStorage: DivPersistentValuesStorage
  private let reporter: DivReporter
  private let scheduler: Scheduling
  private let timeMeasuringFactory: () -> TimeMeasuring

  private var timers = [UIElementPath: DivTimer]()
  private var controllers = [UIElementPath: DivTimerController]()

  init(
    variablesStorage: DivVariablesStorage,
    functionsStorage: DivFunctionsStorage,
    actionHandler: DivActionHandler,
    updateCard: @escaping DivActionHandler.UpdateCardAction,
    persistentValuesStorage: DivPersistentValuesStorage,
    reporter: DivReporter,
    scheduler: Scheduling = TimerScheduler(),
    timeMeasuringFactory: @escaping () -> TimeMeasuring = { TimeIntervalMeasuring() }
  ) {
    self.variablesStorage = variablesStorage
    self.functionsStorage = functionsStorage
    self.actionHandler = actionHandler
    self.updateCard = updateCard
    self.persistentValuesStorage = persistentValuesStorage
    self.reporter = reporter
    self.scheduler = scheduler
    self.timeMeasuringFactory = timeMeasuringFactory
  }

  func set(cardId: DivCardID, timers: [DivTimer]) {
    reset(cardId: cardId)

    for timer in timers {
      let key = cardId.path + timer.id
      if self.timers[key] == nil {
        self.timers[key] = timer
      } else {
        DivKitLogger.error("Duplicate timer \(timer.id) in card \(cardId)")
      }
    }
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
    if let controller = getController(cardId: cardId, timerId: timerId) {
      controller.start()
    }
  }

  func stop(cardId: DivCardID, timerId: String) {
    if let controller = getController(cardId: cardId, timerId: timerId) {
      controller.stop()
    }
  }

  func pause(cardId: DivCardID, timerId: String) {
    if let controller = getController(cardId: cardId, timerId: timerId) {
      controller.pause()
    }
  }

  func resume(cardId: DivCardID, timerId: String) {
    if let controller = getController(cardId: cardId, timerId: timerId) {
      controller.resume()
    }
  }

  func cancel(cardId: DivCardID, timerId: String) {
    if let controller = getController(cardId: cardId, timerId: timerId) {
      controller.cancel()
    }
  }

  func reset(cardId: DivCardID, timerId: String) {
    if let controller = getController(cardId: cardId, timerId: timerId) {
      controller.reset()
    }
  }

  func reset() {
    timers = [:]

    for (_, controller) in controllers {
      controller.cancel()
    }
    controllers = [:]
  }

  func reset(cardId: DivCardID) {
    timers = timers.filter { key, _ in key.cardId != cardId }

    let oldControllers = controllers
    for (key, controller) in oldControllers {
      if key.cardId == cardId {
        controller.cancel()
        controllers.removeValue(forKey: key)
      }
    }
  }

  private func getController(cardId: DivCardID, timerId: String) -> DivTimerController? {
    let key = cardId.path + timerId
    if let controller = controllers[key] {
      return controller
    }

    guard let timer = timers[key] else {
      DivKitLogger.error("Timer \(timerId) not found for card \(cardId)")
      return nil
    }

    let controller = makeController(cardId: cardId, timer: timer)
    controllers[key] = controller
    return controller
  }

  private func makeController(
    cardId: DivCardID,
    timer: DivTimer
  ) -> DivTimerController {
    DivTimerController(
      cardId: cardId,
      divTimer: timer,
      timerScheduler: scheduler,
      timeMeasuring: timeMeasuringFactory(),
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

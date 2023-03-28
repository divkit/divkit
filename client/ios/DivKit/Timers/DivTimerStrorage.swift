import Foundation

import BasePublic
import LayoutKit

final class DivTimerStorage {
  private let variablesStorage: DivVariablesStorage
  private let actionHandler: DivActionHandler
  private let urlOpener: UrlOpener
  private let updateCard: DivActionURLHandler.UpdateCardAction
  private let timerScheduler = TimerScheduler()
  private var cardsTimers = [DivCardID: [DivTimer]]()
  private var timerControllers = [String: DivTimerController]()

  public init(
    variablesStorage: DivVariablesStorage,
    actionHandler: DivActionHandler,
    urlOpener: @escaping UrlOpener,
    updateCard: @escaping DivActionURLHandler.UpdateCardAction
  ) {
    self.variablesStorage = variablesStorage
    self.actionHandler = actionHandler
    self.urlOpener = urlOpener
    self.updateCard = updateCard
  }

  public func set(
    cardId: DivCardID,
    timers: [DivTimer]
  ) {
    cardsTimers[cardId] = timers
  }

  public func perform(_ cardId: DivCardID, _ timerId: String, _ action: DivTimerAction) {
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

  public func start(cardId: DivCardID, timerId: String) {
    if let timer = getTimer(cardId: cardId, timerId: timerId) {
      let variables = variablesStorage.makeVariables(for: cardId)
      timer.start(variables: variables)
    }
  }

  public func stop(cardId: DivCardID, timerId: String) {
    if let timer = getTimer(cardId: cardId, timerId: timerId) {
      timer.stop()
    }
  }

  public func pause(cardId: DivCardID, timerId: String) {
    if let timer = getTimer(cardId: cardId, timerId: timerId) {
      timer.pause()
    }
  }

  public func resume(cardId: DivCardID, timerId: String) {
    if let timer = getTimer(cardId: cardId, timerId: timerId) {
      timer.resume()
    }
  }

  public func cancel(cardId: DivCardID, timerId: String) {
    if let timer = getTimer(cardId: cardId, timerId: timerId) {
      timer.cancel()
    }
  }

  public func reset(cardId: DivCardID, timerId: String) {
    if let timer = getTimer(cardId: cardId, timerId: timerId) {
      let variables = variablesStorage.makeVariables(for: cardId)
      timer.reset(variables: variables)
    }
  }

  public func reset() {
    timerControllers.forEach { _, timer in
      timer.cancel()
    }
    timerControllers.removeAll()
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
      divTimer: divTimer,
      timerScheduler: timerScheduler,
      timeMeasuring: TimeIntervalMeasuring(),
      runActions: { [weak self] actions in
        self?.runActions(cardId: cardId, actions: actions)
      },
      updateVariable: { [weak self] name, value in
        self?.updateVariable(cardId: cardId, name: name, value: value)
      },
      updateCard: { [weak self] in
        self?.updateCard(.timer(cardId))
      }
    )
  }

  private func runActions(cardId: DivCardID, actions: [DivAction]) {
    actions.forEach {
      actionHandler.handle(
        $0,
        cardId: cardId,
        source: .custom,
        urlOpener: urlOpener
      )
    }
  }

  private func updateVariable(cardId: DivCardID, name: DivVariableName, value: String) {
    variablesStorage.update(cardId: cardId, name: name, value: value)
  }
}

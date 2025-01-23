import CoreGraphics
import Foundation
import LayoutKit
import VGSL

/// Deprecated. Use `DivActionHandler`.
public final class DivActionURLHandler {
  @frozen
  public enum UpdateReason {
    case patch(DivCardID, DivPatch)
    case timer(DivCardID)
    case state(DivCardID)
    case variable([DivCardID: Set<DivVariableName>])
    case external
  }

  private let patchProvider: DivPatchProvider
  private let updateCard: DivActionHandler.UpdateCardAction
  private let persistentValuesStorage: DivPersistentValuesStorage
  private let setStateActionHandler: SetStateActionHandler
  private let scrollActionHandler: ScrollActionHandler
  private let timerActionHandler: TimerActionHandler
  private let tooltipActionHandler: TooltipActionHandler

  init(
    patchProvider: DivPatchProvider,
    updateCard: @escaping DivActionHandler.UpdateCardAction,
    persistentValuesStorage: DivPersistentValuesStorage,
    scrollActionHandler: ScrollActionHandler,
    setStateActionHandler: SetStateActionHandler,
    timerActionHandler: TimerActionHandler,
    tooltipActionHandler: TooltipActionHandler
  ) {
    self.patchProvider = patchProvider
    self.updateCard = updateCard
    self.persistentValuesStorage = persistentValuesStorage
    self.setStateActionHandler = setStateActionHandler
    self.scrollActionHandler = scrollActionHandler
    self.timerActionHandler = timerActionHandler
    self.tooltipActionHandler = tooltipActionHandler
  }

  func handleURL(
    _ url: URL,
    info: DivActionInfo,
    context: DivActionHandlingContext,
    completion: @escaping (Result<Void, Error>) -> Void = { _ in }
  ) -> Bool {
    guard let intent = DivActionIntent(url: url) else {
      return false
    }

    let cardId = info.path.cardId
    switch intent {
    case let .showTooltip(id, multiple):
      let tooltipInfo = TooltipInfo(id: id, showsOnStart: false, multiple: multiple)
      tooltipActionHandler.showTooltip(tooltipInfo)
    case let .hideTooltip(id):
      tooltipActionHandler.hideTooltip(id: id)
    case let .download(patchUrl):
      patchProvider.getPatch(
        url: patchUrl,
        info: info,
        completion: { [unowned self] in
          self.applyPatch(cardId: cardId, result: $0, completion: completion)
        }
      )
    case let .setState(divStatePath, lifetime):
      setStateActionHandler.handle(
        divStatePath: divStatePath,
        lifetime: lifetime,
        context: context
      )
    case let .setVariable(name, value):
      context.variablesStorage.update(
        path: info.path,
        name: DivVariableName(rawValue: name),
        value: value
      )
    case let .setCurrentItem(id, index):
      scrollActionHandler.scrollToItem(cardId: cardId, id: id, index: index)
    case let .setNextItem(id, step, overflow):
      scrollActionHandler.scrollToNextItem(
        cardId: cardId,
        id: id,
        step: step,
        overflow: overflow
      )
    case let .setPreviousItem(id, step, overflow):
      scrollActionHandler.scrollToNextItem(
        cardId: cardId,
        id: id,
        step: -step,
        overflow: overflow
      )
    case let .scroll(id, mode):
      switch mode {
      case .start:
        scrollActionHandler.scrollToStart(cardId: cardId, id: id)
      case .end:
        scrollActionHandler.scrollToEnd(cardId: cardId, id: id)
      case let .forward(offset, overflow):
        scrollActionHandler.scrollToOffset(
          cardId: cardId,
          id: id,
          offset: offset,
          isRelative: true,
          overflow: overflow
        )
      case let .backward(offset, overflow):
        scrollActionHandler.scrollToOffset(
          cardId: cardId,
          id: id,
          offset: -offset,
          isRelative: true,
          overflow: overflow
        )
      case let .position(position):
        scrollActionHandler.scrollToOffset(cardId: cardId, id: id, offset: position)
      }
    case let .video(id: id, action: action):
      context.blockStateStorage.setState(
        id: id,
        cardId: cardId,
        state: VideoBlockViewState(state: action == .play ? .playing : .paused)
      )
      context.updateCard(.state(cardId))
    case let .timer(timerId, action):
      timerActionHandler.handle(cardId: cardId, timerId: timerId, action: action)
    case let .setStoredValue(storedValue):
      persistentValuesStorage.set(value: storedValue)
    }

    return true
  }

  private func applyPatch(
    cardId: DivCardID,
    result: Result<DivPatch, Error>,
    completion: @escaping (Result<Void, Error>) -> Void
  ) {
    switch result {
    case let .success(patch):
      updateCard(.patch(cardId, patch))
      completion(.success(()))
    case let .failure(error):
      completion(.failure(error))
    }
  }
}

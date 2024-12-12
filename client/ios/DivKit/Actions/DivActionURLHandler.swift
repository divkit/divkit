import CoreGraphics
import Foundation

import LayoutKit
import VGSL

/// Deprecated. Use `DivActionHandler`.
public final class DivActionURLHandler {
  public typealias UpdateCardAction = (UpdateReason) -> Void
  public typealias ShowTooltipAction = (TooltipInfo) -> Void
  public typealias PerformTimerAction = (
    _ cardId: DivCardID,
    _ timerId: String,
    _ action: DivTimerAction
  ) -> Void

  @frozen
  public enum UpdateReason {
    case patch(DivCardID, DivPatch)
    case timer(DivCardID)
    case state(DivCardID)
    case variable([DivCardID: Set<DivVariableName>])
    case external
  }

  private let stateUpdater: DivStateUpdater
  private let blockStateStorage: DivBlockStateStorage
  private let patchProvider: DivPatchProvider
  private let variableUpdater: DivVariableUpdater
  private let updateCard: UpdateCardAction
  private let showTooltip: ShowTooltipAction?
  private let tooltipActionPerformer: TooltipActionPerformer?
  private let performTimerAction: PerformTimerAction
  private let persistentValuesStorage: DivPersistentValuesStorage
  private let scrollActionHandler: ScrollActionHandler

  public init(
    stateUpdater: DivStateUpdater,
    blockStateStorage: DivBlockStateStorage,
    patchProvider: DivPatchProvider,
    variableUpdater: DivVariableUpdater,
    updateCard: @escaping UpdateCardAction,
    showTooltip: ShowTooltipAction?,
    tooltipActionPerformer: TooltipActionPerformer?,
    performTimerAction: @escaping PerformTimerAction = { _, _, _ in },
    persistentValuesStorage: DivPersistentValuesStorage
  ) {
    self.stateUpdater = stateUpdater
    self.blockStateStorage = blockStateStorage
    self.patchProvider = patchProvider
    self.variableUpdater = variableUpdater
    self.updateCard = updateCard
    self.showTooltip = showTooltip
    self.tooltipActionPerformer = tooltipActionPerformer
    self.performTimerAction = performTimerAction
    self.persistentValuesStorage = persistentValuesStorage
    self.scrollActionHandler = ScrollActionHandler(
      blockStateStorage: blockStateStorage,
      updateCard: updateCard
    )
  }

  public func canHandleURL(_ url: URL) -> Bool {
    url.scheme == DivActionIntent.scheme
  }

  public func handleURL(
    _ url: URL,
    cardId: DivCardID,
    completion: @escaping (Result<Void, Error>) -> Void = { _ in }
  ) -> Bool {
    let info = DivActionInfo(
      path: cardId.path,
      logId: cardId.rawValue,
      url: url,
      logUrl: nil,
      referer: nil,
      source: .tap,
      payload: nil
    )
    return handleURL(url, info: info, completion: completion)
  }

  func handleURL(
    _ url: URL,
    info: DivActionInfo,
    completion: @escaping (Result<Void, Error>) -> Void = { _ in }
  ) -> Bool {
    guard let intent = DivActionIntent(url: url) else {
      return false
    }

    let cardId = info.path.cardId
    switch intent {
    case let .showTooltip(id, multiple):
      let tooltipInfo = TooltipInfo(id: id, showsOnStart: false, multiple: multiple)
      guard showTooltip == nil else {
        showTooltip?(tooltipInfo)
        break
      }
      tooltipActionPerformer?.showTooltip(info: tooltipInfo)
    case let .hideTooltip(id):
      guard showTooltip == nil else {
        break
      }
      tooltipActionPerformer?.hideTooltip(id: id)
    case let .download(patchUrl):
      patchProvider.getPatch(
        url: patchUrl,
        info: info,
        completion: { [unowned self] in
          self.applyPatch(cardId: cardId, result: $0, completion: completion)
        }
      )
    case let .setState(divStatePath, lifetime):
      stateUpdater.set(
        path: divStatePath,
        cardId: cardId,
        lifetime: lifetime
      )
      updateCard(.state(cardId))
    case let .setVariable(name, value):
      variableUpdater.update(
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
      blockStateStorage.setState(
        id: id,
        cardId: cardId,
        state: VideoBlockViewState(state: action == .play ? .playing : .paused)
      )
      updateCard(.state(cardId))
    case let .timer(timerId, action):
      performTimerAction(cardId, timerId, action)
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

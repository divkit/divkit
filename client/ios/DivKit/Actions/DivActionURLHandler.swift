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
      setCurrentItem(id: id, cardId: cardId, index: index)
      updateCard(.state(cardId))
    case let .setNextItem(id, overflow):
      setNextItem(id: id, cardId: cardId, overflow: overflow)
      updateCard(.state(cardId))
    case let .setPreviousItem(id, overflow):
      setPreviousItem(id: id, cardId: cardId, overflow: overflow)
      updateCard(.state(cardId))
    case let .scroll(id, mode):
      setContentOffset(id: id, cardId: cardId, mode: mode)
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

  private func setContentOffset(id: String, cardId: DivCardID, mode: ScrollMode) {
    guard let state: GalleryViewState = blockStateStorage.getState(id, cardId: cardId),
          case .offset = state.contentPosition else {
      setEdgeItem(id: id, cardId: cardId, mode: mode)
      return
    }

    if let newOffset = getNewOffset(state: state, mode: mode) {
      blockStateStorage.setState(
        id: id,
        cardId: cardId,
        state: GalleryViewState(
          contentPosition: .offset(newOffset),
          itemsCount: state.itemsCount,
          isScrolling: state.isScrolling,
          scrollRange: state.scrollRange
        )
      )
      updateCard(.state(cardId))
    }
  }

  private func getNewOffset(state: GalleryViewState, mode: ScrollMode) -> CGFloat? {
    guard let scrollRange = state.scrollRange, scrollRange > 0 else {
      return nil
    }
    switch mode {
    case let .forward(step, overflow):
      return getOffset(
        offset: state.currentOffset + step,
        scrollRange: scrollRange,
        overflow: overflow
      )
    case let .backward(step, overflow):
      return getOffset(
        offset: state.currentOffset - step,
        scrollRange: scrollRange,
        overflow: overflow
      )
    case let .position(step):
      return getOffset(offset: step, scrollRange: scrollRange, overflow: .clamp)
    case .start:
      return 0
    case .end:
      return scrollRange
    }
  }

  private func getOffset(
    offset: CGFloat,
    scrollRange: CGFloat,
    overflow: OverflowMode
  ) -> CGFloat {
    switch overflow {
    case .ring:
      (offset + scrollRange).truncatingRemainder(dividingBy: scrollRange)
    case .clamp:
      offset.clamp(0...scrollRange)
    }
  }

  private func setCurrentItem(id: String, cardId: DivCardID, index: Int) {
    guard let state = blockStateStorage
      .getStateUntyped(id, cardId: cardId) as? GalleryTypeViewState else {
      DivKitLogger.error("\(#file).\(#function) get unexpected type for id \(id)")
      return
    }

    let clampedIndex = clamp(index, min: 0, max: max(0, state.itemsCount - 1))
    guard clampedIndex == index else {
      return
    }

    let newState = state.makeState(clampedIndex)

    blockStateStorage.setState(
      id: id,
      cardId: cardId,
      state: newState
    )
  }

  private func setEdgeItem(id: String, cardId: DivCardID, mode: ScrollMode) {
    guard let state = blockStateStorage
      .getStateUntyped(id, cardId: cardId) as? GalleryTypeViewState else {
      DivKitLogger.error("\(#file).\(#function) get unexpected type for id \(id)")
      return
    }

    let index: Int
    switch mode {
    case .start:
      index = 0
    case .end:
      index = state.itemsCount - 1
    default:
      return
    }

    setCurrentItem(
      id: id,
      cardId: cardId,
      index: index
    )

    updateCard(.state(cardId))
  }

  private func setNextItem(id: String, cardId: DivCardID, overflow: OverflowMode) {
    guard let state = blockStateStorage
      .getStateUntyped(id, cardId: cardId) as? GalleryTypeViewState else {
      DivKitLogger.error("\(#file).\(#function) get unexpected type for id \(id)")
      return
    }

    let index = getNextIndex(
      current: state.currentItemIndex,
      count: state.itemsCount,
      overflow: overflow
    )

    setCurrentItem(
      id: id,
      cardId: cardId,
      index: index
    )
  }

  private func getNextIndex(
    current: Int,
    count: Int,
    overflow: OverflowMode
  ) -> Int {
    guard count != 0 else {
      return current
    }
    switch overflow {
    case .ring:
      return (current + 1) % count
    case .clamp:
      return min(current + 1, count - 1)
    }
  }

  private func setPreviousItem(id: String, cardId: DivCardID, overflow: OverflowMode) {
    guard let state = blockStateStorage
      .getStateUntyped(id, cardId: cardId) as? GalleryTypeViewState else {
      DivKitLogger.error("\(#file).\(#function) get unexpected type for id \(id)")
      return
    }

    let index = getPreviousIndex(
      current: state.currentItemIndex,
      count: state.itemsCount,
      overflow: overflow
    )

    setCurrentItem(
      id: id,
      cardId: cardId,
      index: index
    )
  }

  private func getPreviousIndex(
    current: Int,
    count: Int,
    overflow: OverflowMode
  ) -> Int {
    guard count != 0 else {
      return current
    }
    switch overflow {
    case .ring:
      return (current + count - 1) % count
    case .clamp:
      return max(0, current - 1)
    }
  }
}

fileprivate protocol GalleryTypeViewState: ElementState {
  var itemsCount: Int { get }
  var currentItemIndex: Int { get }

  var makeState: (_ clampedIndex: Int) -> Self { get }
}

extension GalleryViewState: GalleryTypeViewState {
  var currentItemIndex: Int {
    switch contentPosition {
    case let .offset(_, firstVisibleItemsIndex):
      firstVisibleItemsIndex
    case let .paging(index):
      Int(index)
    }
  }

  var currentOffset: CGFloat {
    switch contentPosition {
    case let .offset(value, _):
      return value
    case let .paging(index):
      guard let scrollRange, itemsCount > 0 else {
        return 0
      }
      return scrollRange / CGFloat(itemsCount - 1) * index
    }
  }

  var makeState: (_ clampedIndex: Int) -> GalleryViewState {
    { clampedIndex in
      GalleryViewState(
        contentPosition: .paging(index: CGFloat(clampedIndex)),
        itemsCount: itemsCount,
        isScrolling: isScrolling,
        scrollRange: scrollRange
      )
    }
  }
}

extension PagerViewState: GalleryTypeViewState {
  var itemsCount: Int {
    numberOfPages
  }

  var currentItemIndex: Int {
    Int(currentPage)
  }

  var makeState: (_ clampedIndex: Int) -> PagerViewState {
    { clampedIndex in
      PagerViewState(
        numberOfPages: itemsCount,
        currentPage: clampedIndex
      )
    }
  }
}

extension TabViewState: GalleryTypeViewState {
  var itemsCount: Int {
    countOfPages
  }

  var currentItemIndex: Int {
    Int(selectedPageIndex)
  }

  var makeState: (_ clampedIndex: Int) -> TabViewState {
    { clampedIndex in
      TabViewState(
        selectedPageIndex: CGFloat(clampedIndex),
        countOfPages: itemsCount
      )
    }
  }
}
